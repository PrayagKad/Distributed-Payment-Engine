package com.bank.distributedpaymentssystem.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Immutable record of every balance change.
 *
 * Transactions are NEVER updated or deleted — only inserted.
 * This append-only pattern is a regulatory requirement in banking:
 * every balance change must be permanently traceable.
 *
 * For a transfer from A → B, two Transaction rows are created:
 *   - TRANSFER_DEBIT  on account A (money out)
 *   - TRANSFER_CREDIT on account B (money in)
 * Both are created in the same @Transactional call — if one fails, both roll back.
 */
@Entity
@Table(name = "transactions",
        indexes = @Index(name = "idx_transactions_account_id", columnList = "account_id"))
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Which account this transaction belongs to
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TransactionType type;

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal amount;

    // Balance AFTER this transaction was applied — useful for statements
    @Column(name = "balance_after", nullable = false, precision = 19, scale = 4)
    private BigDecimal balanceAfter;

    // For transfers: stores the other account's number for reference
    @Column(name = "reference_account_number", length = 20)
    private String referenceAccountNumber;

    @Column(length = 255)
    private String description;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}