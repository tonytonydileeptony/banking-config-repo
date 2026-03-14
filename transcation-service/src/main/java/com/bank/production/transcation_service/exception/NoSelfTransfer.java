package com.bank.production.transcation_service.exception;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = NoSelfTransferValidator.class)
public @interface NoSelfTransfer {

    String message() default "Cannot transfer to same account";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}

