package com.bank.distributedpaymentssystem.controller;

import com.bank.distributedpaymentssystem.dto.*;
import com.bank.distributedpaymentssystem.dto.DepositWithdrawDTO;
import com.bank.distributedpaymentssystem.dto.TransactionResponseDTO;
import com.bank.distributedpaymentssystem.dto.TransferRequestDTO;
import com.bank.distributedpaymentssystem.service.TransactionService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for all money movement operations.
 *
 * ENDPOINTS:
 *   POST /api/v1/accounts/{number}/deposit      → Deposit money
 *   POST /api/v1/accounts/{number}/withdraw     → Withdraw money
 *   POST /api/v1/transfers                      → Transfer between accounts
 *   GET  /api/v1/accounts/{number}/transactions → Transaction history (paginated)
 */
@Slf4j
@RestController
@RequestMapping("/api/v1")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping("/accounts/{accountNumber}/deposit")
    public ResponseEntity<TransactionResponseDTO> deposit(
            @PathVariable String accountNumber,
            @Valid @RequestBody DepositWithdrawDTO request) {

        log.info("POST /deposit - account={} amount={}", accountNumber, request.getAmount());
        return ResponseEntity.ok(transactionService.deposit(accountNumber, request));
    }

    @PostMapping("/accounts/{accountNumber}/withdraw")
    public ResponseEntity<TransactionResponseDTO> withdraw(
            @PathVariable String accountNumber,
            @Valid @RequestBody DepositWithdrawDTO request) {

        log.info("POST /withdraw - account={} amount={}", accountNumber, request.getAmount());
        return ResponseEntity.ok(transactionService.withdraw(accountNumber, request));
    }

    /**
     * Transfer between two accounts.
     * Returns TWO transaction records — the debit and the credit.
     */
    @PostMapping("/transfers")
    public ResponseEntity<List<TransactionResponseDTO>> transfer(
            @Valid @RequestBody TransferRequestDTO request) {

        log.info("POST /transfers - from={} to={} amount={}",
                request.getFromAccountNumber(),
                request.getToAccountNumber(),
                request.getAmount());

        return ResponseEntity.ok(transactionService.transfer(request));
    }

    /**
     * Paginated transaction history.
     * GET /api/v1/accounts/123456789012/transactions?page=0&size=20
     */
    @GetMapping("/accounts/{accountNumber}/transactions")
    public ResponseEntity<Page<TransactionResponseDTO>> getHistory(
            @PathVariable String accountNumber,
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "20") int size) {

        return ResponseEntity.ok(transactionService.getHistory(accountNumber, page, size));
    }
}