package com.bank.production.transcation_service.service;

import com.bank.production.transcation_service.dto.AccountResponseDto;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;

@Service
public class AccountClient {

    private final RestClient restClient;

    public AccountClient(RestClient restClient) {
        this.restClient = restClient;
    }

    public void credit(Long accountId, BigDecimal amount) {

        restClient.post()
                .uri("/accounts/{id}/credit?amount={amount}", accountId, amount)
                .retrieve()
                .body(AccountResponseDto.class);
    }

    public void debit(Long accountId, BigDecimal amount) {

        restClient.post()
                .uri("/accounts/{id}/debit?amount={amount}", accountId, amount)
                .retrieve()
                .body(AccountResponseDto.class);
    }
}
