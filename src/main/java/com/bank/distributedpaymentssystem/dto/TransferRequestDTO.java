package com.bank.distributedpaymentssystem.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class TransferRequestDTO {

    @NotBlank(message = "Sender account number is required")
    private String fromAccountNumber;

    @NotBlank(message = "Receiver account number is required")
    private String toAccountNumber;

    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be greater than zero")
    @Digits(integer = 15, fraction = 4, message = "Invalid amount format")
    private BigDecimal amount;

    @Size(max = 255)
    private String description;
}
