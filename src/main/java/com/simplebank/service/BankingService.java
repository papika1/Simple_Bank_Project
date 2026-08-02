package com.simplebank.service;

import com.simplebank.dao.AccountDAO;
import com.simplebank.dao.TransactionDAO;
import com.simplebank.exception.AccountNotFoundException;
import com.simplebank.exception.AccountUnavailableException;
import com.simplebank.exception.InsufficientBalanceException;
import com.simplebank.exception.InvalidAmountException;
import com.simplebank.model.AccountStatus;
import com.simplebank.model.BankAccount;
import com.simplebank.model.Transaction;
import com.simplebank.model.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class BankingService {

    private final AccountDAO accountDAO;
    private final TransactionDAO transactionDAO;

    public BankingService(
            AccountDAO accountDAO,
            TransactionDAO transactionDAO
    ) {
        this.accountDAO = accountDAO;
        this.transactionDAO = transactionDAO;
    }

    public void deposit(
            int accountId,
            BigDecimal amount,
            String description
    ) {
        validateAmount(amount);

        BankAccount account = getActiveAccount(accountId);

        BigDecimal newBalance =
                account.getBalance().add(amount);

        accountDAO.updateBalance(accountId, newBalance);

        Transaction transaction = new Transaction(
                accountId,
                null,
                TransactionType.DEPOSIT,
                amount,
                LocalDateTime.now(),
                description
        );

        transactionDAO.save(transaction);
    }

    public void withdraw(
            int accountId,
            BigDecimal amount,
            String description
    ) {
        validateAmount(amount);

        BankAccount account = getActiveAccount(accountId);

        if (account.getBalance().compareTo(amount) < 0) {
            throw new InsufficientBalanceException();
        }

        BigDecimal newBalance =
                account.getBalance().subtract(amount);

        accountDAO.updateBalance(accountId, newBalance);

        Transaction transaction = new Transaction(
                accountId,
                null,
                TransactionType.WITHDRAWAL,
                amount,
                LocalDateTime.now(),
                description
        );

        transactionDAO.save(transaction);
    }

    public void transfer(
            int sourceAccountId,
            int targetAccountId,
            BigDecimal amount,
            String description
    ) {
        validateAmount(amount);

        if (sourceAccountId == targetAccountId) {
            throw new IllegalArgumentException(
                    "Source and target accounts must be different."
            );
        }

        BankAccount sourceAccount =
                getActiveAccount(sourceAccountId);

        BankAccount targetAccount =
                getActiveAccount(targetAccountId);

        if (sourceAccount.getBalance().compareTo(amount) < 0) {
            throw new InsufficientBalanceException();
        }

        BigDecimal sourceNewBalance =
                sourceAccount.getBalance().subtract(amount);

        BigDecimal targetNewBalance =
                targetAccount.getBalance().add(amount);

        accountDAO.updateBalance(
                sourceAccountId,
                sourceNewBalance
        );

        accountDAO.updateBalance(
                targetAccountId,
                targetNewBalance
        );

        Transaction outgoingTransaction = new Transaction(
                sourceAccountId,
                targetAccountId,
                TransactionType.TRANSFER_OUT,
                amount,
                LocalDateTime.now(),
                description
        );

        Transaction incomingTransaction = new Transaction(
                targetAccountId,
                sourceAccountId,
                TransactionType.TRANSFER_IN,
                amount,
                LocalDateTime.now(),
                description
        );

        transactionDAO.save(outgoingTransaction);
        transactionDAO.save(incomingTransaction);
    }

    public BigDecimal getBalance(int accountId) {
        return getAccount(accountId).getBalance();
    }

    private BankAccount getAccount(int accountId) {
        return accountDAO.findById(accountId)
                .orElseThrow(
                        () -> new AccountNotFoundException(accountId)
                );
    }

    private BankAccount getActiveAccount(int accountId) {
        BankAccount account = getAccount(accountId);

        if (account.getStatus() != AccountStatus.ACTIVE) {
            throw new AccountUnavailableException(accountId);
        }

        return account;
    }

    private void validateAmount(BigDecimal amount) {
        if (amount == null
                || amount.compareTo(BigDecimal.ZERO) <= 0) {

            throw new InvalidAmountException();
        }
    }
}