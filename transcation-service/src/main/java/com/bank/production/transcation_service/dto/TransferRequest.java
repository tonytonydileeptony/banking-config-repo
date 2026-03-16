package com.bank.production.transcation_service.dto;

import com.bank.production.transcation_service.exception.NoSelfTransfer;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import com.bank.production.dto.Status;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@NoSelfTransfer
public class TransferRequest {

    private String requestId;

    @JsonProperty("fromAccount")
    @NotNull(message = "From account is required")
    @Positive
    private Long fromAccountId;

    @JsonProperty("toAccount")
    @NotNull(message = "To account is required")
    @Positive
    private Long toAccountId;

    @NotNull(message= "Amount is required")
    private BigDecimal amount;
    private LocalDateTime createdAt;
    private Status status;

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public Long getFromAccountId() {
        return fromAccountId;
    }

    public void setFromAccountId(Long fromAccountId) {
        this.fromAccountId = fromAccountId;
    }

    public Long getToAccountId() {
        return toAccountId;
    }

    public void setToAccountId(Long toAccountId) {
        this.toAccountId = toAccountId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    // getters & setters

    @Override
    public String toString() {
        return "TransferRequest{" +
                "requestId='" + requestId + '\'' +
                ", fromAccountId=" + fromAccountId +
                ", toAccountId=" + toAccountId +
                ", amount=" + amount +
                '}';
    }
}
