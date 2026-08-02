package com.simplebank.exception;

public class InsufficientBalanceException extends RuntimeException {

    public InsufficientBalanceException() {
        super("The account has insufficient balance.");
    }
}