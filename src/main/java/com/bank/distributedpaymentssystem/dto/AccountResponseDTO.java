package com.bank.distributedpaymentssystem.dto;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class AccountResponseDTO {

    private Long id;
    private String accountNumber;
    private String ownerName;
    private String email;
    private BigDecimal balance;
    private String accountType;
    private boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}