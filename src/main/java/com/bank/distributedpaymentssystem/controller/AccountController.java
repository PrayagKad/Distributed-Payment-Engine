package com.bank.distributedpaymentssystem.controller;

import com.bank.distributedpaymentssystem.dto.AccountRequestDTO;
import com.bank.distributedpaymentssystem.dto.AccountResponseDTO;
import com.bank.distributedpaymentssystem.service.AccountService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for account operations.
 *
 * ENDPOINTS:
 *   POST   /api/v1/accounts              → Create account
 *   GET    /api/v1/accounts              → List all accounts
 *   GET    /api/v1/accounts/{number}     → Get single account
 *   DELETE /api/v1/accounts/{number}     → Deactivate account
 *
 * CONSTRUCTOR INJECTION:
 * AccountService is injected via constructor — not field @Autowired.
 * This makes the controller testable with a simple mock:
 *   new AccountController(mockAccountService)
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/accounts")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @PostMapping
    public ResponseEntity<AccountResponseDTO> createAccount(
            @Valid @RequestBody AccountRequestDTO request) {

        log.info("POST /accounts - create account for {}", request.getEmail()); // creates a log for the account created
        AccountResponseDTO response = accountService.createAccount(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<AccountResponseDTO>> getAllAccounts() {
        return ResponseEntity.ok(accountService.getAllAccounts());
    }

    @GetMapping("/{accountNumber}")
    public ResponseEntity<AccountResponseDTO> getAccount(
            @PathVariable String accountNumber) {

        return ResponseEntity.ok(accountService.getAccount(accountNumber));
    }

    @DeleteMapping("/{accountNumber}")
    public ResponseEntity<Void> deactivateAccount(
            @PathVariable String accountNumber) {

        accountService.deactivateAccount(accountNumber);
        return ResponseEntity.noContent().build();
    }
}
