package com.bank.distributedpaymentssystem.repository;


import com.bank.distributedpaymentssystem.model.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    // Paginated history — return 20 transactions per page by default
    // Spring Data generates: SELECT * FROM transactions WHERE account_id = ? ORDER BY created_at DESC
    Page<Transaction> findByAccountIdOrderByCreatedAtDesc(Long accountId, Pageable pageable);
}
