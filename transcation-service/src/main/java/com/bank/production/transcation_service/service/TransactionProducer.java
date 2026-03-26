package com.bank.production.transcation_service.service;

// Status enum for transaction state
import com.bank.production.dto.Status;
// For dependency injection of beans
import org.springframework.beans.factory.annotation.Autowired;
// Kafka client for sending messages
import org.springframework.kafka.core.KafkaTemplate;
// Marks this class as a Spring service component
import org.springframework.stereotype.Service;
// Event object representing a transaction
import com.bank.production.dto.TransactionEvent;

// Spring service annotation to register this class as a service bean
@Service
// Service class for publishing transaction events to Kafka message broker
public class TransactionProducer {

    // KafkaTemplate used for sending messages with String keys and TransactionEvent values
    private final KafkaTemplate<String, TransactionEvent> kafkaTemplate;

    // Constructor with @Autowired annotation for dependency injection of KafkaTemplate
    @Autowired
    public TransactionProducer(KafkaTemplate<String, TransactionEvent> kafkaTemplate) {
        // Inject the KafkaTemplate bean that will be used for sending messages
        this.kafkaTemplate = kafkaTemplate;
    }

    // Method to publish a transaction event to Kafka
    public void publish(TransactionEvent event) {
        // Send the event to the 'transaction-events' Kafka topic
        kafkaTemplate.send("transaction-events", event);

    }
}
