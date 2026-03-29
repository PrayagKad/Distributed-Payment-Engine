package com.bank.distributedpaymentssystem.dto;


import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class AccountRequestDTO {

    @NotBlank(message = "Owner name is required")
    @Size(min = 2, max = 100)
    private String ownerName;

    @NotBlank(message = "Email is required")
    @Email(message = "Must be a valid email address")
    private String email;

    @NotBlank(message = "Account type is required")
    @Pattern(regexp = "SAVINGS|CURRENT", message = "Account type must be SAVINGS or CURRENT")
    private String accountType;

    @DecimalMin(value = "0.00", message = "Opening balance cannot be negative")
    @Digits(integer = 15, fraction = 4, message = "Invalid amount format")
    private BigDecimal openingBalance = BigDecimal.ZERO;
}