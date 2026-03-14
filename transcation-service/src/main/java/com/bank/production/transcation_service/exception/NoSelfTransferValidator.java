package com.bank.production.transcation_service.exception;

import com.bank.production.transcation_service.dto.TransferRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class NoSelfTransferValidator
        implements ConstraintValidator<NoSelfTransfer, TransferRequest> {

    @Override
    public boolean isValid(TransferRequest request,
                           ConstraintValidatorContext context) {

        // Be defensive: if request or either id is null, don't treat this as a
        // self-transfer here. Let field-level @NotNull / @Positive validators
        // handle missing/invalid values. This prevents unexpected exceptions
        // during validation (HV000028 when a NPE occurs).
        if (request == null) return true;

        Long from = request.getFromAccountId();
        Long to = request.getToAccountId();

        if (from == null || to == null) return true;

        return !from.equals(to);
    }
}
