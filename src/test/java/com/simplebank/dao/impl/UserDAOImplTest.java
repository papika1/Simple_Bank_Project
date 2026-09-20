package com.simplebank.dao.impl;

import com.simplebank.config.TestDatabaseConnection;
import com.simplebank.model.Customer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class UserDAOImplTest {

    @BeforeEach
    void cleanDatabase() throws Exception {

        try (Connection connection =
                     TestDatabaseConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(
                             "DELETE FROM customers"
                     )) {

            statement.executeUpdate();
        }
    }

    @Test
    void saveCustomerShouldWork() throws Exception {

        try (Connection connection =
                     TestDatabaseConnection.getConnection()) {

            String sql = """
                    INSERT INTO customers
                    (full_name, email, phone, registration_date)
                    VALUES (?, ?, ?, ?)
                    """;

            PreparedStatement statement =
                    connection.prepareStatement(sql);

            statement.setString(1, "Test User");
            statement.setString(2, "test@email.com");
            statement.setString(3, "123456");
            statement.setDate(
                    4,
                    java.sql.Date.valueOf(LocalDate.now())
            );

            int rows = statement.executeUpdate();

            assertEquals(1, rows);
        }
    }

    @Test
    void customerShouldBeFoundByEmail()
            throws Exception {

        try (Connection connection =
                     TestDatabaseConnection.getConnection()) {

            PreparedStatement insert =
                    connection.prepareStatement("""
                            INSERT INTO customers
                            (full_name, email, phone, registration_date)
                            VALUES (?, ?, ?, ?)
                            """);

            insert.setString(1, "Anna Test");
            insert.setString(2, "anna@test.com");
            insert.setString(3, "123");
            insert.setDate(
                    4,
                    java.sql.Date.valueOf(LocalDate.now())
            );

            insert.executeUpdate();

            PreparedStatement select =
                    connection.prepareStatement("""
                            SELECT *
                            FROM customers
                            WHERE email = ?
                            """);

            select.setString(1, "anna@test.com");

            ResultSet resultSet =
                    select.executeQuery();

            assertTrue(resultSet.next());

            assertEquals(
                    "Anna Test",
                    resultSet.getString("full_name")
            );
        }
    }
}