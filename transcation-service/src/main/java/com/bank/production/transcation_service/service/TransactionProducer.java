package com.bank.production.transcation_service.service;

import com.bank.production.transcation_service.dto.TransactionEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class TransactionProducer {

    private final KafkaTemplate<String, TransactionEvent> kafkaTemplate;
    @Autowired
    public TransactionProducer(KafkaTemplate<String, TransactionEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }


    public void publish(TransactionEvent event) {
//transaction-events
        kafkaTemplate.send("transaction-events", event);

    }
}
