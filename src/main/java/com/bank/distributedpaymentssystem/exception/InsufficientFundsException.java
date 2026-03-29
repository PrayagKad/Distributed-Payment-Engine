package com.bank.distributedpaymentssystem.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.math.BigDecimal;

@ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
public class InsufficientFundsException extends RuntimeException {

    public InsufficientFundsException(String accountNumber, BigDecimal balance, BigDecimal requested) {
        super(String.format(
                "Insufficient funds in account %s. Available: %.2f, Requested: %.2f",
                accountNumber, balance, requested
        ));
    }
}