package com.bank.production.transcation_service.service;

import com.bank.production.transcation_service.dto.AccountResponseDto;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;
import java.util.Map;

@Service
public class AccountClient {

    private final RestClient restClient;

    public AccountClient(RestClient restClient) {
        this.restClient = restClient;
    }

    public void credit(Long accountId, BigDecimal amount) {

        restClient.post()
                .uri("/accounts/{id}/credit?amount={amount}", Map.of("id", accountId, "amount", amount))
                .retrieve()
                .body(AccountResponseDto.class);
    }

    public void debit(Long accountId, BigDecimal amount) {

        restClient.post()
                .uri("/accounts/{id}/debit?amount={amount}", Map.of("id", accountId, "amount", amount))
                .retrieve()
                .body(AccountResponseDto.class);
    }
}
