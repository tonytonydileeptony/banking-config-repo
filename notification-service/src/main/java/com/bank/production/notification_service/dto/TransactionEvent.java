package com.bank.production.notification_service.dto;

import java.math.BigDecimal;

public class TransactionEvent {

    private Long fromAccount;
    private Long toAccount;
    private BigDecimal amount;
    private Status status;

    public Long getFromAccount() {
        return fromAccount;
    }

    public void setFromAccount(Long fromAccount) {
        this.fromAccount = fromAccount;
    }

    public Long getToAccount() {
        return toAccount;
    }

    public void setToAccount(Long toAccount) {
        this.toAccount = toAccount;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
}
