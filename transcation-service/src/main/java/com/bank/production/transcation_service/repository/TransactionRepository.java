package com.bank.production.transcation_service.repository;

import com.bank.production.transcation_service.model.TransactionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TransactionRepository
        extends JpaRepository<TransactionEntity, Long>, JpaSpecificationExecutor<TransactionEntity> {


    boolean existsByTransactionId(String transactionId);
    Optional<TransactionEntity> findByTransactionId(String transactionId);
}

