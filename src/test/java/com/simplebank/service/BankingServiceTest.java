package com.simplebank.service;

import com.simplebank.dao.AccountDAO;
import com.simplebank.dao.TransactionDAO;
import com.simplebank.exception.AccountUnavailableException;
import com.simplebank.exception.InsufficientBalanceException;
import com.simplebank.exception.InvalidAmountException;
import com.simplebank.model.AccountStatus;
import com.simplebank.model.AccountType;
import com.simplebank.model.BankAccount;
import com.simplebank.model.Transaction;
import com.simplebank.model.TransactionType;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class BankingServiceTest {

    private FakeAccountDAO accountDAO;
    private FakeTransactionDAO transactionDAO;
    private BankingService bankingService;

    @BeforeEach
    void setUp() {
        accountDAO = new FakeAccountDAO();
        transactionDAO = new FakeTransactionDAO();

        bankingService =
                new BankingService(accountDAO, transactionDAO);

        accountDAO.save(
                createAccount(
                        1,
                        new BigDecimal("100000.00"),
                        AccountStatus.ACTIVE
                )
        );

        accountDAO.save(
                createAccount(
                        2,
                        new BigDecimal("50000.00"),
                        AccountStatus.ACTIVE
                )
        );
    }

    @Test
    void depositShouldIncreaseBalance() {

        bankingService.deposit(
                1,
                new BigDecimal("10000.00"),
                "Test deposit"
        );

        BigDecimal balance =
                bankingService.getBalance(1);

        assertEquals(
                0,
                balance.compareTo(
                        new BigDecimal("110000.00")
                )
        );
    }

    @Test
    void depositShouldCreateTransaction() {

        bankingService.deposit(
                1,
                new BigDecimal("10000.00"),
                "Test deposit"
        );

        assertEquals(
                1,
                transactionDAO.findAll().size()
        );

        Transaction transaction =
                transactionDAO.findAll().get(0);

        assertEquals(
                TransactionType.DEPOSIT,
                transaction.getTransactionType()
        );
    }

    @Test
    void depositShouldRejectZeroAmount() {

        assertThrows(
                InvalidAmountException.class,
                () -> bankingService.deposit(
                        1,
                        BigDecimal.ZERO,
                        "Invalid deposit"
                )
        );
    }

    @Test
    void depositShouldRejectNegativeAmount() {

        assertThrows(
                InvalidAmountException.class,
                () -> bankingService.deposit(
                        1,
                        new BigDecimal("-1000.00"),
                        "Invalid deposit"
                )
        );
    }

    @Test
    void withdrawShouldDecreaseBalance() {

        bankingService.withdraw(
                1,
                new BigDecimal("20000.00"),
                "Test withdrawal"
        );

        BigDecimal balance =
                bankingService.getBalance(1);

        assertEquals(
                0,
                balance.compareTo(
                        new BigDecimal("80000.00")
                )
        );
    }

    @Test
    void withdrawShouldFailWhenBalanceIsTooLow() {

        assertThrows(
                InsufficientBalanceException.class,
                () -> bankingService.withdraw(
                        1,
                        new BigDecimal("200000.00"),
                        "Too large withdrawal"
                )
        );
    }

    @Test
    void blockedAccountShouldRejectDeposit() {

        accountDAO.updateStatus(
                1,
                AccountStatus.BLOCKED
        );

        assertThrows(
                AccountUnavailableException.class,
                () -> bankingService.deposit(
                        1,
                        new BigDecimal("1000.00"),
                        "Blocked account deposit"
                )
        );
    }

    @Test
    void transferShouldMoveMoneyBetweenAccounts() {

        bankingService.transfer(
                1,
                2,
                new BigDecimal("25000.00"),
                "Test transfer"
        );

        BigDecimal sourceBalance =
                bankingService.getBalance(1);

        BigDecimal targetBalance =
                bankingService.getBalance(2);

        assertEquals(
                0,
                sourceBalance.compareTo(
                        new BigDecimal("75000.00")
                )
        );

        assertEquals(
                0,
                targetBalance.compareTo(
                        new BigDecimal("75000.00")
                )
        );
    }

    @Test
    void transferShouldCreateTwoTransactions() {

        bankingService.transfer(
                1,
                2,
                new BigDecimal("5000.00"),
                "Test transfer"
        );

        List<Transaction> transactions =
                transactionDAO.findAll();

        assertEquals(2, transactions.size());

        assertEquals(
                TransactionType.TRANSFER_OUT,
                transactions.get(0).getTransactionType()
        );

        assertEquals(
                TransactionType.TRANSFER_IN,
                transactions.get(1).getTransactionType()
        );
    }

    @Test
    void transferShouldRejectSameAccount() {

        assertThrows(
                IllegalArgumentException.class,
                () -> bankingService.transfer(
                        1,
                        1,
                        new BigDecimal("1000.00"),
                        "Invalid transfer"
                )
        );
    }

    private BankAccount createAccount(
            int id,
            BigDecimal balance,
            AccountStatus status
    ) {
        return new BankAccount(
                id,
                "TEST-" + id,
                1,
                AccountType.CURRENT,
                balance,
                status,
                LocalDate.now()
        );
    }

    private static class FakeAccountDAO
            implements AccountDAO {

        private final Map<Integer, BankAccount> accounts =
                new HashMap<>();

        @Override
        public boolean save(BankAccount account) {
            accounts.put(account.getId(), account);
            return true;
        }

        @Override
        public Optional<BankAccount> findById(int id) {
            return Optional.ofNullable(
                    accounts.get(id)
            );
        }

        @Override
        public Optional<BankAccount> findByAccountNumber(
                String accountNumber
        ) {
            return accounts.values()
                    .stream()
                    .filter(account ->
                            account.getAccountNumber()
                                    .equals(accountNumber))
                    .findFirst();
        }

        @Override
        public List<BankAccount> findAll() {
            return new ArrayList<>(
                    accounts.values()
            );
        }

        @Override
        public List<BankAccount> findByCustomerId(
                int customerId
        ) {
            return accounts.values()
                    .stream()
                    .filter(account ->
                            account.getCustomerId()
                                    == customerId)
                    .toList();
        }

        @Override
        public boolean update(BankAccount account) {
            accounts.put(
                    account.getId(),
                    account
            );
            return true;
        }

        @Override
        public boolean updateBalance(
                int accountId,
                BigDecimal newBalance
        ) {
            BankAccount account =
                    accounts.get(accountId);

            if (account == null) {
                return false;
            }

            account.setBalance(newBalance);
            return true;
        }

        @Override
        public boolean updateStatus(
                int accountId,
                AccountStatus status
        ) {
            BankAccount account =
                    accounts.get(accountId);

            if (account == null) {
                return false;
            }

            account.setStatus(status);
            return true;
        }

        @Override
        public boolean deleteById(int id) {
            return accounts.remove(id) != null;
        }
    }

    private static class FakeTransactionDAO
            implements TransactionDAO {

        private final List<Transaction> transactions =
                new ArrayList<>();

        @Override
        public boolean save(Transaction transaction) {
            transaction.setId(
                    transactions.size() + 1
            );

            transactions.add(transaction);
            return true;
        }

        @Override
        public Optional<Transaction> findById(int id) {
            return transactions.stream()
                    .filter(transaction ->
                            transaction.getId() == id)
                    .findFirst();
        }

        @Override
        public List<Transaction> findAll() {
            return new ArrayList<>(
                    transactions
            );
        }

        @Override
        public List<Transaction> findByAccountId(
                int accountId
        ) {
            return transactions.stream()
                    .filter(transaction ->
                            transaction.getAccountId()
                                    == accountId)
                    .toList();
        }

        @Override
        public List<Transaction> findByType(
                TransactionType transactionType
        ) {
            return transactions.stream()
                    .filter(transaction ->
                            transaction.getTransactionType()
                                    == transactionType)
                    .toList();
        }

        @Override
        public List<Transaction> findByDateRange(
                LocalDateTime startDate,
                LocalDateTime endDate
        ) {
            return transactions.stream()
                    .filter(transaction ->
                            !transaction
                                    .getTransactionDate()
                                    .isBefore(startDate)
                                    &&
                            !transaction
                                    .getTransactionDate()
                                    .isAfter(endDate))
                    .toList();
        }
    }
}