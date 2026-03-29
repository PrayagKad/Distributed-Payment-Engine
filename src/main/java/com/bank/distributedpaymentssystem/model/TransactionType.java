package com.bank.distributedpaymentssystem.model;

public enum TransactionType {
    DEPOSIT,
    WITHDRAWAL,
    TRANSFER_DEBIT,   // money leaving the sender's account
    TRANSFER_CREDIT   // money arriving in the receiver's account
}

