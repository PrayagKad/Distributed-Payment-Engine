package com.bank.distributedpaymentssystem;

import com.bank.distributedpaymentssystem.dto.AccountRequestDTO;
import com.bank.distributedpaymentssystem.dto.AccountResponseDTO;
import com.bank.distributedpaymentssystem.exception.AccountNotFoundException;
import com.bank.distributedpaymentssystem.model.Account;
import com.bank.distributedpaymentssystem.repository.AccountRepository;
import com.bank.distributedpaymentssystem.service.AccountService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for AccountService.
 *
 * WHY UNIT TESTS MATTER IN INTERVIEWS:
 * Banks care deeply about test coverage. Being able to say
 * "I wrote unit tests with Mockito that test the service layer
 * without hitting the database" shows professional maturity.
 *
 * Key pattern here:
 * - @Mock creates a fake AccountRepository — no real DB needed
 * - @InjectMocks creates AccountService with the mock injected
 * - We define what the mock returns (when/thenReturn)
 * - We verify the service behaves correctly given those inputs
 */
@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private AccountService accountService;

    private Account sampleAccount;

    @BeforeEach
    void setUp() {
        sampleAccount = Account.builder()
                .id(1L)
                .accountNumber("123456789012")
                .ownerName("John Doe")
                .email("john@example.com")
                .balance(new BigDecimal("1000.00"))
                .accountType("SAVINGS")
                .active(true)
                .version(0L)
                .build();
    }

    @Test
    @DisplayName("createAccount - should create and return a new account")
    void createAccount_success() {
        // Arrange
        AccountRequestDTO request = new AccountRequestDTO();
        request.setOwnerName("John Doe");
        request.setEmail("john@example.com");
        request.setAccountType("SAVINGS");
        request.setOpeningBalance(new BigDecimal("1000.00"));

        when(accountRepository.existsByEmail(anyString())).thenReturn(false);
        when(accountRepository.existsByAccountNumber(anyString())).thenReturn(false);
        when(accountRepository.save(any(Account.class))).thenReturn(sampleAccount);

        // Act
        AccountResponseDTO response = accountService.createAccount(request);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getOwnerName()).isEqualTo("John Doe");
        assertThat(response.getBalance()).isEqualByComparingTo("1000.00");
        verify(accountRepository, times(1)).save(any(Account.class));
    }

    @Test
    @DisplayName("createAccount - should throw when email already exists")
    void createAccount_duplicateEmail_throwsException() {
        // Arrange
        AccountRequestDTO request = new AccountRequestDTO();
        request.setEmail("john@example.com");
        request.setOwnerName("John Doe");
        request.setAccountType("SAVINGS");

        when(accountRepository.existsByEmail("john@example.com")).thenReturn(true);

        // Act & Assert
        assertThatThrownBy(() -> accountService.createAccount(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("john@example.com");

        verify(accountRepository, never()).save(any());
    }

    @Test
    @DisplayName("getAccount - should return account when found")
    void getAccount_found() {
        when(accountRepository.findByAccountNumber("123456789012"))
                .thenReturn(Optional.of(sampleAccount));

        AccountResponseDTO response = accountService.getAccount("123456789012");

        assertThat(response.getAccountNumber()).isEqualTo("123456789012");
        assertThat(response.isActive()).isTrue();
    }

    @Test
    @DisplayName("getAccount - should throw when account not found")
    void getAccount_notFound_throwsException() {
        when(accountRepository.findByAccountNumber("000000000000"))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> accountService.getAccount("000000000000"))
                .isInstanceOf(AccountNotFoundException.class);
    }

    @Test
    @DisplayName("deactivateAccount - should set active to false")
    void deactivateAccount_success() {
        when(accountRepository.findByAccountNumber("123456789012"))
                .thenReturn(Optional.of(sampleAccount));
        when(accountRepository.save(any(Account.class))).thenReturn(sampleAccount);

        accountService.deactivateAccount("123456789012");

        assertThat(sampleAccount.isActive()).isFalse();
        verify(accountRepository, times(1)).save(sampleAccount);
    }
}