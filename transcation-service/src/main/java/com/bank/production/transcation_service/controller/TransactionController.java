package com.bank.production.transcation_service.controller;


import com.bank.production.transcation_service.dto.ApiResponse;
import com.bank.production.transcation_service.dto.TransactionDto;
import com.bank.production.transcation_service.dto.TransferRequest;
import com.bank.production.transcation_service.service.TransactionService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/transactions")
public class TransactionController {

    @Autowired
    private TransactionService service;

    @GetMapping
    public ResponseEntity<ApiResponse<Page<TransactionDto>>> getTransactions(

            @RequestParam(required = false) String status,
            @RequestParam(required = false) Double minAmount,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime startDate,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime endDate,

            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,

            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String direction
    ) {

        Sort sort = direction.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(page, size, sort);

        Page<TransactionDto> transactions =
                service.getTransactions(
                        status,
                        minAmount,
                        startDate,
                        endDate,
                        pageable
                );

        ApiResponse<Page<TransactionDto>> response =
                new ApiResponse<>(
                        true,
                        "Transactions fetched successfully",
                        transactions
                );

        return ResponseEntity.ok(response);
    }
    @PostMapping
    public ResponseEntity<ApiResponse<TransactionDto>> createTransaction(@Valid @RequestBody TransferRequest request) {
        TransactionDto created = service.transfer(request);
        ApiResponse<TransactionDto> response =
                new ApiResponse<>(
                        true,
                        "Transaction created successfully",
                        created
                );
        return ResponseEntity.ok(response);
    }
}
