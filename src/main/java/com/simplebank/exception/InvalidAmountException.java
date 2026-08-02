package com.simplebank.exception;

public class InvalidAmountException extends RuntimeException {

    public InvalidAmountException() {
        super("The amount must be greater than zero.");
    }
}