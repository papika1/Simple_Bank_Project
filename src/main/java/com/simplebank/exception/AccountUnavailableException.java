package com.simplebank.exception;

public class AccountUnavailableException extends RuntimeException {

    public AccountUnavailableException(int accountId) {
        super("Account is not active: " + accountId);
    }
}