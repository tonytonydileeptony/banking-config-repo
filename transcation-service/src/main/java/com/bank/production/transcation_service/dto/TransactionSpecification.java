package com.bank.production.transcation_service.dto;

import com.bank.production.transcation_service.model.TransactionEntity;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class TransactionSpecification {

    public static Specification<TransactionEntity> filter(
            String status,
            Double minAmount,
            LocalDateTime start,
            LocalDateTime end) {

        return (root, query, cb) -> {

            List<Predicate> predicates = new ArrayList<>();

            if (status != null && !status.isEmpty()) {
                predicates.add(cb.equal(root.get("status"), status));
            }

            if (minAmount != null) {
                predicates.add(cb.greaterThanOrEqualTo(
                        root.get("amount"), minAmount));
            }

            if (start != null && end != null) {
                predicates.add(cb.between(
                        root.get("createdAt"), start, end));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
