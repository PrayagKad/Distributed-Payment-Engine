package com.bank.distributedpaymentssystem.dto;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class TransactionResponseDTO {

    private Long id;
    private String type;
    private BigDecimal amount;
    private BigDecimal balanceAfter;
    private String referenceAccountNumber;
    private String description;
    private LocalDateTime createdAt;
}
