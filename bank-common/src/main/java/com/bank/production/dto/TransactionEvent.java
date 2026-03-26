package com.bank.production.dto;

// For monetary amounts with precision
import java.math.BigDecimal;
// For storing transaction timestamps
import java.time.LocalDateTime;

// DTO (Data Transfer Object) for transaction event messages sent via Kafka
public class TransactionEvent {

    // Source account ID - account from which money is transferred
    private Long fromAccount;
    // Destination account ID - account to which money is transferred
    private Long toAccount;
    // Transaction amount with decimal precision
    private BigDecimal amount;
    // Current status of the transaction (PENDING, SUCCESS, FAILED)
    private Status status;
    // Timestamp when the transaction was created
    private LocalDateTime createdAt;

    // Getter for source account ID
    public Long getFromAccount() {
        return fromAccount;
    }

    // Setter for source account ID
    public void setFromAccount(Long fromAccount) {
        this.fromAccount = fromAccount;
    }

    // Getter for destination account ID
    public Long getToAccount() {
        return toAccount;
    }

    // Setter for destination account ID
    public void setToAccount(Long toAccount) {
        this.toAccount = toAccount;
    }

    // Getter for transaction amount
    public BigDecimal getAmount() {
        return amount;
    }

    // Setter for transaction amount
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    // Getter for transaction status
    public Status getStatus() {
        return status;
    }

    // Setter for transaction status
    public void setStatus(Status status) {
        this.status = status;
    }

    // Getter for transaction creation timestamp
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    // Setter for transaction creation timestamp
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
