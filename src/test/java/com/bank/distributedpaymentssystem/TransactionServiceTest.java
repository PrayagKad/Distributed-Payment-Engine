package com.bank.distributedpaymentssystem;

import com.bank.distributedpaymentssystem.dto.DepositWithdrawDTO;
import com.bank.distributedpaymentssystem.dto.TransactionResponseDTO;
import com.bank.distributedpaymentssystem.dto.TransferRequestDTO;
import com.bank.distributedpaymentssystem.exception.InsufficientFundsException;
import com.bank.distributedpaymentssystem.model.Account;
import com.bank.distributedpaymentssystem.model.Transaction;
import com.bank.distributedpaymentssystem.repository.TransactionRepository;
import com.bank.distributedpaymentssystem.service.AccountService;
import com.bank.distributedpaymentssystem.service.TransactionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private AccountService accountService;

    @InjectMocks
    private TransactionService transactionService;

    private Account sender;
    private Account receiver;

    @BeforeEach
    void setUp() {
        sender = Account.builder()
                .id(1L)
                .accountNumber("111111111111")
                .ownerName("Alice")
                .balance(new BigDecimal("5000.00"))
                .active(true)
                .version(0L)
                .build();

        receiver = Account.builder()
                .id(2L)
                .accountNumber("222222222222")
                .ownerName("Bob")
                .balance(new BigDecimal("1000.00"))
                .active(true)
                .version(0L)
                .build();
    }

    // ── Deposit ─────────────────────────────────────────────────────

    @Test
    @DisplayName("deposit - should increase balance and save transaction")
    void deposit_success() {
        DepositWithdrawDTO request = new DepositWithdrawDTO();
        request.setAmount(new BigDecimal("500.00"));
        request.setDescription("Salary");

        when(accountService.findActiveAccount("111111111111")).thenReturn(sender);
        when(transactionRepository.save(any(Transaction.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        TransactionResponseDTO response = transactionService.deposit("111111111111", request);

        assertThat(response.getType()).isEqualTo("DEPOSIT");
        assertThat(response.getAmount()).isEqualByComparingTo("500.00");
        // Balance should have increased from 5000 to 5500
        assertThat(sender.getBalance()).isEqualByComparingTo("5500.00");
        verify(transactionRepository, times(1)).save(any(Transaction.class));
    }

    // ── Withdrawal ──────────────────────────────────────────────────

    @Test
    @DisplayName("withdraw - should decrease balance when funds are sufficient")
    void withdraw_success() {
        DepositWithdrawDTO request = new DepositWithdrawDTO();
        request.setAmount(new BigDecimal("200.00"));

        when(accountService.findActiveAccountWithLock("111111111111")).thenReturn(sender);
        when(transactionRepository.save(any(Transaction.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        TransactionResponseDTO response = transactionService.withdraw("111111111111", request);

        assertThat(response.getType()).isEqualTo("WITHDRAWAL");
        assertThat(sender.getBalance()).isEqualByComparingTo("4800.00");
    }

    @Test
    @DisplayName("withdraw - should throw InsufficientFundsException when balance is too low")
    void withdraw_insufficientFunds_throwsException() {
        DepositWithdrawDTO request = new DepositWithdrawDTO();
        request.setAmount(new BigDecimal("9999.00")); // More than the 5000 balance

        when(accountService.findActiveAccountWithLock("111111111111")).thenReturn(sender);

        assertThatThrownBy(() -> transactionService.withdraw("111111111111", request))
                .isInstanceOf(InsufficientFundsException.class);

        // Balance must not have changed
        assertThat(sender.getBalance()).isEqualByComparingTo("5000.00");
        verify(transactionRepository, never()).save(any());
    }

    // ── Transfer ────────────────────────────────────────────────────

    @Test
    @DisplayName("transfer - should debit sender and credit receiver atomically")
    void transfer_success() {
        TransferRequestDTO request = new TransferRequestDTO();
        request.setFromAccountNumber("111111111111");
        request.setToAccountNumber("222222222222");
        request.setAmount(new BigDecimal("1000.00"));
        request.setDescription("Rent payment");

        // Lock order is alphabetical — "111..." < "222..." so sender locked first
        when(accountService.findActiveAccountWithLock("111111111111")).thenReturn(sender);
        when(accountService.findActiveAccountWithLock("222222222222")).thenReturn(receiver);
        when(transactionRepository.save(any(Transaction.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        List<TransactionResponseDTO> results = transactionService.transfer(request);

        // Two transactions returned — debit and credit
        assertThat(results).hasSize(2);

        // Sender debited: 5000 - 1000 = 4000
        assertThat(sender.getBalance()).isEqualByComparingTo("4000.00");

        // Receiver credited: 1000 + 1000 = 2000
        assertThat(receiver.getBalance()).isEqualByComparingTo("2000.00");

        // Both transactions saved
        verify(transactionRepository, times(2)).save(any(Transaction.class));
    }

    @Test
    @DisplayName("transfer - should throw when sender has insufficient funds")
    void transfer_insufficientFunds_throwsException() {
        TransferRequestDTO request = new TransferRequestDTO();
        request.setFromAccountNumber("111111111111");
        request.setToAccountNumber("222222222222");
        request.setAmount(new BigDecimal("99999.00")); // Way more than 5000

        when(accountService.findActiveAccountWithLock("111111111111")).thenReturn(sender);
        when(accountService.findActiveAccountWithLock("222222222222")).thenReturn(receiver);

        assertThatThrownBy(() -> transactionService.transfer(request))
                .isInstanceOf(InsufficientFundsException.class);

        // Neither balance changed
        assertThat(sender.getBalance()).isEqualByComparingTo("5000.00");
        assertThat(receiver.getBalance()).isEqualByComparingTo("1000.00");
        verify(transactionRepository, never()).save(any());
    }

    @Test
    @DisplayName("transfer - should throw when sender and receiver are the same account")
    void transfer_sameAccount_throwsException() {
        TransferRequestDTO request = new TransferRequestDTO();
        request.setFromAccountNumber("111111111111");
        request.setToAccountNumber("111111111111");
        request.setAmount(new BigDecimal("100.00"));

        assertThatThrownBy(() -> transactionService.transfer(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("same account");

        verify(transactionRepository, never()).save(any());
    }
}

