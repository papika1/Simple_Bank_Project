package com.simplebank.dao.impl;

import com.simplebank.config.TestDatabaseConnection;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class AccountDAOImplTest {

    private int customerId;

    @BeforeEach
    void setUp() throws Exception {

        try (Connection connection =
                     TestDatabaseConnection.getConnection()) {

            connection.createStatement()
                    .executeUpdate(
                            "DELETE FROM accounts"
                    );

            connection.createStatement()
                    .executeUpdate(
                            "DELETE FROM customers"
                    );

            PreparedStatement statement =
                    connection.prepareStatement(
                            """
                            INSERT INTO customers
                            (full_name, email, phone, registration_date)
                            VALUES (?, ?, ?, ?)
                            """,
                            PreparedStatement.RETURN_GENERATED_KEYS
                    );

            statement.setString(1, "Test Customer");
            statement.setString(2, "customer@test.com");
            statement.setString(3, "123");
            statement.setDate(
                    4,
                    java.sql.Date.valueOf(LocalDate.now())
            );

            statement.executeUpdate();

            ResultSet keys =
                    statement.getGeneratedKeys();

            keys.next();

            customerId =
                    keys.getInt(1);
        }
    }

    @Test
    void accountShouldBeSaved() throws Exception {

        try (Connection connection =
                     TestDatabaseConnection.getConnection()) {

            PreparedStatement statement =
                    connection.prepareStatement("""
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
                            """);

            statement.setString(
                    1,
                    "TEST-001"
            );

            statement.setInt(
                    2,
                    customerId
            );

            statement.setString(
                    3,
                    "CURRENT"
            );

            statement.setBigDecimal(
                    4,
                    new java.math.BigDecimal("1000.00")
            );

            statement.setString(
                    5,
                    "ACTIVE"
            );

            statement.setDate(
                    6,
                    java.sql.Date.valueOf(LocalDate.now())
            );

            int rows =
                    statement.executeUpdate();

            assertEquals(1, rows);
        }
    }
}