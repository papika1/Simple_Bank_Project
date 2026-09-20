package com.simplebank.dao.impl;

import com.simplebank.config.TestDatabaseConnection;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class TransactionDAOImplTest {

    private int accountId;

    @BeforeEach
    void setUp() throws Exception {

        try (Connection connection =
                     TestDatabaseConnection.getConnection()) {

            connection.createStatement()
                    .executeUpdate(
                            "DELETE FROM transactions"
                    );

            connection.createStatement()
                    .executeUpdate(
                            "DELETE FROM accounts"
                    );

            connection.createStatement()
                    .executeUpdate(
                            "DELETE FROM customers"
                    );

            PreparedStatement customerStatement =
                    connection.prepareStatement(
                            """
                            INSERT INTO customers
                            (full_name, email, phone, registration_date)
                            VALUES (?, ?, ?, ?)
                            """,
                            PreparedStatement.RETURN_GENERATED_KEYS
                    );

            customerStatement.setString(
                    1,
                    "Transaction Test User"
            );

            customerStatement.setString(
                    2,
                    "transaction@test.com"
            );

            customerStatement.setString(
                    3,
                    "123"
            );

            customerStatement.setDate(
                    4,
                    java.sql.Date.valueOf(LocalDate.now())
            );

            customerStatement.executeUpdate();

            ResultSet customerKeys =
                    customerStatement.getGeneratedKeys();

            customerKeys.next();

            int customerId =
                    customerKeys.getInt(1);

            PreparedStatement accountStatement =
                    connection.prepareStatement(
                            """
                            INSERT INTO accounts
                            (
                                account_number,
                                customer_id,
                                account_type,
                                balance,
                                status,
                                created_at
                            )
                            VALUES (?, ?, ?, ?, ?, ?)
                            """,
                            PreparedStatement.RETURN_GENERATED_KEYS
                    );

            accountStatement.setString(
                    1,
                    "TEST-TR-001"
            );

            accountStatement.setInt(
                    2,
                    customerId
            );

            accountStatement.setString(
                    3,
                    "CURRENT"
            );

            accountStatement.setBigDecimal(
                    4,
                    new BigDecimal("5000.00")
            );

            accountStatement.setString(
                    5,
                    "ACTIVE"
            );

            accountStatement.setDate(
                    6,
                    java.sql.Date.valueOf(LocalDate.now())
            );

            accountStatement.executeUpdate();

            ResultSet accountKeys =
                    accountStatement.getGeneratedKeys();

            accountKeys.next();

            accountId =
                    accountKeys.getInt(1);
        }
    }

    @Test
    void transactionShouldBeSaved()
            throws Exception {

        try (Connection connection =
                     TestDatabaseConnection.getConnection()) {

            PreparedStatement statement =
                    connection.prepareStatement("""
                            INSERT INTO transactions
                            (
                                account_id,
                                related_account_id,
                                transaction_type,
                                amount,
                                description
                            )
                            VALUES (?, ?, ?, ?, ?)
                            """);

            statement.setInt(
                    1,
                    accountId
            );

            statement.setNull(
                    2,
                    java.sql.Types.INTEGER
            );

            statement.setString(
                    3,
                    "DEPOSIT"
            );

            statement.setBigDecimal(
                    4,
                    new BigDecimal("1000.00")
            );

            statement.setString(
                    5,
                    "Test transaction"
            );

            int rows =
                    statement.executeUpdate();

            assertEquals(1, rows);
        }
    }
}