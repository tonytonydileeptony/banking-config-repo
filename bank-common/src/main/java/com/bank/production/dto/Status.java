package com.bank.production.dto;

// Enum for transaction status values
public enum Status {
    // Transaction is awaiting processing
    PENDING,
    // Transaction has been completed successfully
    SUCCESS,
    // Transaction has failed or encountered an error
    FAILED,
    // Transaction has been reversed/refunded
    REVERSED
}
