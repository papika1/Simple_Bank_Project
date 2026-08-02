package com.simplebank.exception;

public class AccountNotFoundException extends RuntimeException {

    public AccountNotFoundException(int accountId) {
        super("Account not found with ID: " + accountId);
    }
}