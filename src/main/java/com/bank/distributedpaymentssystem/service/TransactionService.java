package com.bank.distributedpaymentssystem.service;




import com.bank.distributedpaymentssystem.dto.DepositWithdrawDTO;
import com.bank.distributedpaymentssystem.dto.TransactionResponseDTO;
import com.bank.distributedpaymentssystem.dto.TransferRequestDTO;
import com.bank.distributedpaymentssystem.exception.InsufficientFundsException;
import com.bank.distributedpaymentssystem.model.Account;
import com.bank.distributedpaymentssystem.model.Transaction;
import com.bank.distributedpaymentssystem.model.TransactionType;
import com.bank.distributedpaymentssystem.repository.TransactionRepository;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

/**
 * Handles all money movement: deposit, withdraw, transfer.
 *
 * ════════════════════════════════════════════════════════════
 * THE MOST IMPORTANT METHOD: transfer()
 * ════════════════════════════════════════════════════════════
 *
 * A transfer must be ATOMIC — either both the debit AND credit
 * succeed, or neither does. @Transactional guarantees this.
 *
 * If the credit fails after the debit has already run,
 * Spring rolls back the entire transaction — the sender's money
 * is returned automatically. No money is lost.
 *
 * This is the ACID property that every bank relies on.
 *
 * ════════════════════════════════════════════════════════════
 * PESSIMISTIC LOCKING during transfer
 * ════════════════════════════════════════════════════════════
 *
 * We use SELECT FOR UPDATE (via findByAccountNumberWithLock)
 * to lock both accounts before reading balances.
 *
 * Without this: two concurrent transfers from the same account
 * could both read the same balance, both see "enough funds",
 * and both proceed — leaving the balance negative.
 *
 * With this: the second request waits at the DB level until
 * the first commits, then reads the updated balance.
 *
 * ════════════════════════════════════════════════════════════
 * CONSTRUCTOR INJECTION
 * ════════════════════════════════════════════════════════════
 * Both dependencies are final — injected once at construction,
 * never changeable. Makes unit testing trivial.
 */
@Slf4j
@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountService accountService;

    public TransactionService(
            TransactionRepository transactionRepository,
            AccountService accountService) {
        this.transactionRepository = transactionRepository;
        this.accountService = accountService;
    }

    // ── Deposit ──────────────────────────────────────────────────────

    /**
     * Add money to an account.
     * Simple operation — no locking needed since we're only increasing balance.
     */
    @Transactional
    public TransactionResponseDTO deposit(String accountNumber,  DepositWithdrawDTO request)
     {

        Account account = accountService.findActiveAccount(accountNumber);

        BigDecimal newBalance = account.getBalance().add(request.getAmount());
        account.setBalance(newBalance);

        Transaction tx = Transaction.builder()
                .account(account)
                .type(TransactionType.DEPOSIT)
                .amount(request.getAmount())
                .balanceAfter(newBalance)
                .description(request.getDescription() != null
                        ? request.getDescription() : "Deposit")
                .build();

        transactionRepository.save(tx);

        log.info("Deposit: account={} amount={} newBalance={}",
                accountNumber, request.getAmount(), newBalance);

        return toResponse(tx);
    }

    // ── Withdrawal ───────────────────────────────────────────────────

    /**
     * Remove money from an account.
     * Uses a pessimistic lock to prevent concurrent overdrafts.
     */
    @Transactional
    public TransactionResponseDTO withdraw(String accountNumber, DepositWithdrawDTO request) {

        // Lock the row — prevents two simultaneous withdrawals both passing the balance check
        Account account = accountService.findActiveAccountWithLock(accountNumber);

        if (account.getBalance().compareTo(request.getAmount()) < 0) {
            throw new InsufficientFundsException(
                    accountNumber, account.getBalance(), request.getAmount());
        }

        BigDecimal newBalance = account.getBalance().subtract(request.getAmount());
        account.setBalance(newBalance);

        Transaction tx = Transaction.builder()
                .account(account)
                .type(TransactionType.WITHDRAWAL)
                .amount(request.getAmount())
                .balanceAfter(newBalance)
                .description(request.getDescription() != null
                        ? request.getDescription() : "Withdrawal")
                .build();

        transactionRepository.save(tx);

        log.info("Withdrawal: account={} amount={} newBalance={}",
                accountNumber, request.getAmount(), newBalance);

        return toResponse(tx);
    }

    // ── Transfer ─────────────────────────────────────────────────────

    /**
     * Transfer money between two accounts.
     *
     * This is the crown jewel of Phase 1.
     *
     * WHAT HAPPENS STEP BY STEP:
     *
     *  1. Validate: sender != receiver
     *  2. Lock BOTH accounts (always in consistent order by account number
     *     to prevent deadlocks — if A→B and B→A run simultaneously,
     *     both lock in alphabetical order so neither deadlocks waiting for the other)
     *  3. Check sender has enough balance
     *  4. Debit sender
     *  5. Credit receiver
     *  6. Save two Transaction records (one per account)
     *
     *  If ANYTHING between step 3 and 6 fails:
     *  → @Transactional rolls back ALL changes automatically
     *  → Sender's money is restored, receiver never gets credited
     *  → No money lost, no inconsistency
     */
    @Transactional
    public List<TransactionResponseDTO> transfer(TransferRequestDTO request) {

        // Guard: can't transfer to yourself
        if (request.getFromAccountNumber().equals(request.getToAccountNumber())) {
            throw new IllegalArgumentException("Cannot transfer to the same account");
        }

        // Lock both accounts in consistent alphabetical order to prevent deadlocks
        // Example: if A→B and B→A run concurrently, both will try to lock A first,
        // so one waits — no circular waiting, no deadlock
        String first  = request.getFromAccountNumber().compareTo(request.getToAccountNumber()) < 0
                ? request.getFromAccountNumber() : request.getToAccountNumber();
        String second = first.equals(request.getFromAccountNumber())
                ? request.getToAccountNumber() : request.getFromAccountNumber();

        Account accountFirst  = accountService.findActiveAccountWithLock(first);
        Account accountSecond = accountService.findActiveAccountWithLock(second);

        // Map back to sender/receiver regardless of lock order
        Account sender   = accountFirst.getAccountNumber().equals(request.getFromAccountNumber())
                ? accountFirst : accountSecond;
        Account receiver = accountFirst.getAccountNumber().equals(request.getToAccountNumber())
                ? accountFirst : accountSecond;

        // Check sufficient funds
        if (sender.getBalance().compareTo(request.getAmount()) < 0) {
            throw new InsufficientFundsException(
                    sender.getAccountNumber(), sender.getBalance(), request.getAmount());
        }

        String desc = request.getDescription() != null ? request.getDescription() : "Transfer";

        // Debit sender
        BigDecimal senderNewBalance = sender.getBalance().subtract(request.getAmount());
        sender.setBalance(senderNewBalance);

        Transaction debit = Transaction.builder()
                .account(sender)
                .type(TransactionType.TRANSFER_DEBIT)
                .amount(request.getAmount())
                .balanceAfter(senderNewBalance)
                .referenceAccountNumber(receiver.getAccountNumber())
                .description(desc)
                .build();

        // Credit receiver
        BigDecimal receiverNewBalance = receiver.getBalance().add(request.getAmount());
        receiver.setBalance(receiverNewBalance);

        Transaction credit = Transaction.builder()
                .account(receiver)
                .type(TransactionType.TRANSFER_CREDIT)
                .amount(request.getAmount())
                .balanceAfter(receiverNewBalance)
                .referenceAccountNumber(sender.getAccountNumber())
                .description(desc)
                .build();

        // Save both transaction records
        transactionRepository.save(debit);
        transactionRepository.save(credit);

        log.info("Transfer: from={} to={} amount={} | senderBalance={} receiverBalance={}",
                sender.getAccountNumber(), receiver.getAccountNumber(),
                request.getAmount(), senderNewBalance, receiverNewBalance);

        return List.of(toResponse(debit), toResponse(credit));
    }

    // ── Transaction history ───────────────────────────────────────────

    /**
     * Paginated transaction history for an account.
     * Returns newest transactions first.
     *
     * @param page zero-based page index
     * @param size number of records per page (default 20)
     */
    @Transactional(readOnly = true)
    public Page<TransactionResponseDTO> getHistory(String accountNumber, int page, int size) {
        Account account = accountService.findActiveAccount(accountNumber);
        return transactionRepository
                .findByAccountIdOrderByCreatedAtDesc(account.getId(), PageRequest.of(page, size))
                .map(this::toResponse);
    }

    // ── Mapper ───────────────────────────────────────────────────────

    private TransactionResponseDTO toResponse(Transaction tx) {
        return TransactionResponseDTO.builder()
                .id(tx.getId())
                .type(tx.getType().name())
                .amount(tx.getAmount())
                .balanceAfter(tx.getBalanceAfter())
                .referenceAccountNumber(tx.getReferenceAccountNumber())
                .description(tx.getDescription())
                .createdAt(tx.getCreatedAt())
                .build();
    }
}