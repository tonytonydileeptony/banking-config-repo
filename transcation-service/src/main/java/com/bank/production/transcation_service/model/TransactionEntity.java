package com.bank.production.transcation_service.model;

// Status enum for transaction state
import com.bank.production.dto.Status;
// JPA annotations for ORM mapping
import jakarta.persistence.*;

// For serialization of entity objects
import java.io.Serializable;
// For representing monetary values with precision
import java.math.BigDecimal;
// For storing date and time information
import java.time.LocalDateTime;

// Maps this class to the 'transactions' database table
@Entity
// Defines the table name and unique constraint on transactionId column
@Table(name = "transactions",
        uniqueConstraints = @UniqueConstraint(columnNames = "transactionId"))

// Implements Serializable for object serialization and deserialization
public class TransactionEntity implements Serializable {
    // Serial version UID for maintaining version compatibility during serialization
    private static final long serialVersionUID = 1L;

    // Marks this field as the primary key with UUID generation strategy
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    // Unique identifier for each transaction record
    private String id;

    // Unique transaction identifier from the business logic
    private String transactionId;

    // Source account ID - the account from which money is transferred
    private Long fromAccountId;
    // Destination account ID - the account to which money is transferred
    private Long toAccountId;
    // Transaction amount with decimal precision for monetary values
    private BigDecimal amount;

    // Stores the enum as a string value in the database (e.g., "PENDING", "SUCCESS")
    @Enumerated(EnumType.STRING)
    // Current status of the transaction (PENDING, SUCCESS, FAILED)
    private Status status;

    // Timestamp when the transaction was created
    private LocalDateTime createdAt;


    // getters & setters

    // Getter for transaction ID - returns the unique identifier
    public String getId() {
        return id;
    }

    // Getter for source account ID
    public Long getFromAccountId() {
        return fromAccountId;
    }

    // Setter for source account ID - sets the account from which money is transferred
    public void setFromAccountId(Long fromAccountId) {
        this.fromAccountId = fromAccountId;
    }

    // Getter for destination account ID
    public Long getToAccountId() {
        return toAccountId;
    }

    // Setter for destination account ID - sets the account to which money is transferred
    public void setToAccountId(Long toAccountId) {
        this.toAccountId = toAccountId;
    }

    // Getter for transaction amount
    public BigDecimal getAmount() {
        return amount;
    }

    // Setter for transaction amount - sets the monetary value of the transaction
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    // Getter for transaction status
    public Status getStatus() {
        return status;
    }

    // Getter for transaction creation timestamp
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    // Setter for transaction creation timestamp
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    // Getter for unique transaction identifier from business logic
    public String getTransactionId() {
        return transactionId;
    }

    // Setter for unique transaction identifier
    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    // Setter for transaction status - updates the current state of the transaction
    public void setStatus(Status status) {
        this.status = status;
    }
    // getters & setters
}


