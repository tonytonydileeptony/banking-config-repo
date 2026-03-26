package com.bank.production.transcation_service.repository;

// Entity representing a transaction record
import com.bank.production.transcation_service.model.TransactionEntity;
// Spring Data JPA repository for CRUD operations
import org.springframework.data.jpa.repository.JpaRepository;
// Allows for specification-based queries
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
// Marks this interface as a Spring repository bean
import org.springframework.stereotype.Repository;

// For representing optional return values
import java.util.Optional;

// Spring repository annotation to register this interface as a repository bean
@Repository
// Repository interface for TransactionEntity with ID type Long
// Extends JpaRepository for standard CRUD operations (Create, Read, Update, Delete)
// Extends JpaSpecificationExecutor for dynamic query capabilities
public interface TransactionRepository
        extends JpaRepository<TransactionEntity, Long>, JpaSpecificationExecutor<TransactionEntity> {

    // Query method to check if a transaction exists by its transaction ID
    // Spring Data JPA generates the SQL: SELECT EXISTS(SELECT 1 FROM transactions WHERE transactionId = ?)
    boolean existsByTransactionId(String transactionId);

    // Query method to find a transaction by its transaction ID
    // Returns an Optional to handle cases where no transaction is found
    // Spring Data JPA generates the SQL: SELECT * FROM transactions WHERE transactionId = ?
    Optional<TransactionEntity> findByTransactionId(String transactionId);
}
