package com.bank.distributedpaymentssystem.repository;

import com.bank.distributedpaymentssystem.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import jakarta.persistence.LockModeType;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

    Optional<Account> findByAccountNumber(String accountNumber);

    boolean existsByEmail(String email);

    boolean existsByAccountNumber(String accountNumber);

    /**
     * SELECT ... FOR UPDATE — acquires a pessimistic write lock on the row.
     *
     * Used during transfers and withdrawals to prevent two concurrent
     * transactions from both reading the same balance, both seeing
     * "sufficient funds", and both proceeding — causing the balance to go negative.
     *
     * With this lock, the second request waits until the first commits or rolls back,
     * then reads the updated balance before deciding.
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT a FROM Account a WHERE a.accountNumber = :accountNumber")
    Optional<Account> findByAccountNumberWithLock(String accountNumber);
}