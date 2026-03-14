package com.bank.production.notification_service.service;

import com.bank.production.notification_service.dto.TransactionEvent;
import com.bank.production.notification_service.model.Notification;
import com.bank.production.notification_service.repository.NotificationRepository;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class NotificationConsumer {

    private final NotificationRepository repository;

    public NotificationConsumer(NotificationRepository repository) {
        this.repository = repository;
    }

    @KafkaListener(topics = "transaction-events")
    public void consume(TransactionEvent event) {

        Notification notification = new Notification();
        notification.setType("TRANSFER");
        notification.setRecipient(event.getToAccount().toString());
        notification.setMessage(
                "You received ₹" + event.getAmount());
        notification.setCreatedAt(LocalDateTime.now());

        repository.save(notification);

        System.out.println("Notification sent: " + notification.getMessage());
    }
}
