package com.bank.production.transcation_service.service;

// Importing DTO for account response structure
import com.bank.production.transcation_service.dto.AccountResponseDto;
// Marks this class as a Spring service component
import org.springframework.stereotype.Service;
// Used for making HTTP requests to other services
import org.springframework.web.client.RestClient;

// For representing monetary values with precision
import java.math.BigDecimal;
// For passing parameters as key-value pairs
import java.util.Map;

// Spring service annotation to register this class as a service bean
@Service
// HTTP client service for communicating with Account Service
public class AccountClient {

    // RestClient instance for making HTTP requests to account service endpoints
    private final RestClient restClient;

    // Constructor with RestClient injected via dependency injection
    public AccountClient(RestClient restClient) {
        // Initialize the RestClient for HTTP communication
        this.restClient = restClient;
    }

    // Method to credit money to an account via HTTP POST request
    public void credit(Long accountId, BigDecimal amount) {
        // Send POST request to credit endpoint with account ID and amount parameters
        restClient.post()
                // Build URI with path parameters: account ID and amount
                .uri("/accounts/{id}/credit?amount={amount}", Map.of("id", accountId, "amount", amount))
                // Retrieve response from the server
                .retrieve()
                // Parse response body as AccountResponseDto object
                .body(AccountResponseDto.class);
    }

    // Method to debit money from an account via HTTP POST request
    public void debit(Long accountId, BigDecimal amount) {
        // Send POST request to debit endpoint with account ID and amount parameters
        restClient.post()
                // Build URI with path parameters: account ID and amount
                .uri("/accounts/{id}/debit?amount={amount}", Map.of("id", accountId, "amount", amount))
                // Retrieve response from the server
                .retrieve()
                // Parse response body as AccountResponseDto object
                .body(AccountResponseDto.class);
    }
}
