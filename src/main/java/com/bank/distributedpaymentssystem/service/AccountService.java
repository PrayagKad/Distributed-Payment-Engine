package com.bank.distributedpaymentssystem.service;



import com.bank.distributedpaymentssystem.dto.AccountRequestDTO;
import com.bank.distributedpaymentssystem.dto.AccountResponseDTO;
import com.bank.distributedpaymentssystem.exception.AccountNotFoundException;
import com.bank.distributedpaymentssystem.model.Account;
import com.bank.distributedpaymentssystem.repository.AccountRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * Business logic for account management.
 *
 * WHY CONSTRUCTOR INJECTION?
 * The repository is declared final — it cannot be null or swapped after
 * construction. This means unit tests can do:
 *   new AccountService(mockAccountRepository)
 * with zero Spring context needed. @Autowired field injection breaks this.
 */
@Slf4j
@Service
public class AccountService {

    private final AccountRepository accountRepository;

    // Single constructor — Spring injects automatically, no @Autowired needed
    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    /**
     * Create a new bank account.
     * Generates a unique 12-digit account number automatically.
     */
    @Transactional
    public AccountResponseDTO createAccount(AccountRequestDTO request) {

        // Guard: duplicate email
        if (accountRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException(
                    "An account with email " + request.getEmail() + " already exists");
        }

        Account account = Account.builder()
                .accountNumber(generateAccountNumber())
                .ownerName(request.getOwnerName())
                .email(request.getEmail())
                .accountType(request.getAccountType())
                .balance(request.getOpeningBalance() != null
                        ? request.getOpeningBalance()
                        : BigDecimal.ZERO)
                .active(true)
                .build();

        Account saved = accountRepository.save(account);
        log.info("Created account {} for {}", saved.getAccountNumber(), saved.getOwnerName()); // logs the new account created in the logs(idea IDE)

        return toResponse(saved);
    }

    /**
     * Get a single account by its account number.
     */
    @Transactional(readOnly = true)
    public AccountResponseDTO getAccount(String accountNumber) {
        Account account = findActiveAccount(accountNumber);
        return toResponse(account);
    }

    /**
     * Get all accounts — useful for admin view.
     */
    @Transactional(readOnly = true)
    public List<AccountResponseDTO> getAllAccounts() {
        return accountRepository.findAll()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Deactivate an account (soft delete — never hard delete in banking).
     * Deactivated accounts still exist for audit/history purposes.
     */
    @Transactional
    public void deactivateAccount(String accountNumber) {
        Account account = findActiveAccount(accountNumber);
        account.setActive(false);
        accountRepository.save(account);
        log.info("Deactivated account {}", accountNumber);
    }

    // ── Shared helpers used by TransactionService too ───────────────

    /**
     * Finds an account and throws a clean 404 if missing or inactive.
     * Used by both AccountService and TransactionService.
     */
    public Account findActiveAccount(String accountNumber) {
        return accountRepository.findByAccountNumber(accountNumber)
                .filter(Account::isActive)
                .orElseThrow(() -> new AccountNotFoundException(accountNumber));
    }

    /**
     * Finds an account with a pessimistic write lock.
     * Used during transfers and withdrawals to prevent concurrent balance corruption.
     */
    public Account findActiveAccountWithLock(String accountNumber) {
        return accountRepository.findByAccountNumberWithLock(accountNumber)
                .filter(Account::isActive)
                .orElseThrow(() -> new AccountNotFoundException(accountNumber));
    }

    // ── Private helpers ─────────────────────────────────────────────

    public AccountResponseDTO toResponse(Account account) {
        return AccountResponseDTO.builder()
                .id(account.getId())
                .accountNumber(account.getAccountNumber())
                .ownerName(account.getOwnerName())
                .email(account.getEmail())
                .balance(account.getBalance())
                .accountType(account.getAccountType())
                .active(account.isActive())
                .createdAt(account.getCreatedAt())
                .updatedAt(account.getUpdatedAt())
                .build();
    }

    /**
     * Generates a random 12-digit account number.
     * In production this would use a proper sequence or bank-format generator.
     */
    private String generateAccountNumber() {
        String number;
        do {
            number = String.format("%012d", (long)(Math.random() * 1_000_000_000_000L));
        } while (accountRepository.existsByAccountNumber(number));
        return number;
    }
}