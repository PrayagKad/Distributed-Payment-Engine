package com.bank.distributedpaymentssystem.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Represents a bank account.
 *
 * Key decisions:
 * - BigDecimal for balance: never use double/float for money.
 *   0.1 + 0.2 = 0.30000000000000004 in floating point — unacceptable in banking.
 * - @Version for optimistic locking: if two requests try to update
 *   the same account simultaneously, one will fail with an exception
 *   instead of silently overwriting the other's changes.
 */
@Entity
@Table(name = "accounts")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "account_number", nullable = false, unique = true, length = 20)
    private String accountNumber;

    @Column(name = "owner_name", nullable = false, length = 100)
    private String ownerName;

    @Column(name = "email", nullable = false, unique = true, length = 100)
    private String email;

    // NUMERIC(19,4) in DB — exact decimal, 4 decimal places
    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal balance;

    @Column(name = "account_type", nullable = false, length = 20)
    private String accountType; // SAVINGS, CURRENT

    @Column(nullable = false)
    @Builder.Default
    private boolean active = true;

    // Optimistic locking — prevents lost updates under concurrent requests
    @Version
    private Long version;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
