package com.julian.account_movement_service.domain.exception;

public class InsufficientBalanceException extends BusinessRuleException {

    public InsufficientBalanceException(String message) {
        super(message);
    }
}