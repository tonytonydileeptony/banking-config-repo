package com.bank.production.transcation_service.service;

import com.bank.production.transcation_service.dto.*;
import com.bank.production.transcation_service.model.TransactionEntity;
import com.bank.production.transcation_service.repository.TransactionRepository;
import jakarta.transaction.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

import static com.bank.production.transcation_service.dto.Status.SUCCESS;

@Service
public class TransactionService {

    @Autowired
    private TransactionRepository repository;
    @Autowired
    private TransactionProducer producer;
    @Autowired
    private AccountClient accountClient;
    public Page<TransactionDto> getTransactions(
            String status,
            Double minAmount,
            LocalDateTime startDate,
            LocalDateTime endDate,
            Pageable pageable) {

        Specification<TransactionEntity> spec =
                TransactionSpecification.filter(
                        status,
                        minAmount,
                        startDate,
                        endDate
                );

        return repository.findAll(spec, pageable)
                .map(this::mapToDto);
    }
    public TransactionDto  transfer(TransferRequest request) {
    System.out.println("Initiating transfer: " + request.getAmount() + " from " + request.getFromAccountId() + " to " + request.getToAccountId());
        accountClient.debit(request.getFromAccountId(), request.getAmount());

        accountClient.credit(request.getToAccountId(), request.getAmount());

        TransactionEntity txn = new TransactionEntity();
        txn.setFromAccountId(request.getFromAccountId());
        txn.setToAccountId(request.getToAccountId());
        txn.setAmount(request.getAmount());
        txn.setStatus(SUCCESS);
        txn.setCreatedAt(LocalDateTime.now());
        System.out.println("Transfer completed: " + txn.getAmount() + " from " + txn.getFromAccountId() + " to " + txn.getToAccountId());
         TransactionEntity transactionEntity=repository.saveAndFlush(txn);
      TransactionDto transactionDto=  mapToDto(transactionEntity);
        System.out.println(txn.getId()+"after dto maping" + transactionDto.getId());
        TransactionEvent event = new TransactionEvent();
        mapToEvent(request, SUCCESS, LocalDateTime.now());
        producer.publish(event);
        return transactionDto;
    }

    private TransactionDto mapToDto(TransactionEntity txn) {
        TransactionDto dto = new TransactionDto();

        dto.setId(txn.getId());
        dto.setAmount(txn.getAmount());
        dto.setStatus(txn.getStatus());
        dto.setCreatedAt(txn.getCreatedAt());

        return dto;
    }
    private TransactionEvent mapToEvent(TransferRequest request, Status status, LocalDateTime time) {
        TransactionEvent event = new TransactionEvent();

        event.setFromAccount(request.getFromAccountId());
        event.setAmount(request.getAmount());
        event.setStatus(status);
        event.setToAccount(request.getToAccountId());
        event.setCreatedAt(time);

        return event;
    }
}