package com.simplebank.dao.impl;

import com.simplebank.config.DatabaseConnection;
import com.simplebank.dao.UserDAO;
import com.simplebank.model.Customer;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserDAOImpl implements UserDAO {

    @Override
    public boolean save(Customer customer) {
        String sql = """
                INSERT INTO customers (
                    full_name,
                    email,
                    phone,
                    registration_date
                )
                VALUES (?, ?, ?, ?)
                """;

        try (
                Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(
                        sql,
                        Statement.RETURN_GENERATED_KEYS
                )
        ) {
            statement.setString(1, customer.getFullName());
            statement.setString(2, customer.getEmail());
            statement.setString(3, customer.getPhone());
            statement.setDate(
                    4,
                    Date.valueOf(customer.getRegistrationDate())
            );

            int affectedRows = statement.executeUpdate();

            if (affectedRows == 0) {
                return false;
            }

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    customer.setId(generatedKeys.getInt(1));
                }
            }

            return true;

        } catch (SQLException exception) {
            throw new RuntimeException(
                    "Failed to save customer.",
                    exception
            );
        }
    }

    @Override
    public Optional<Customer> findById(int id) {
        String sql = """
                SELECT
                    id,
                    full_name,
                    email,
                    phone,
                    registration_date
                FROM customers
                WHERE id = ?
                """;

        try (
                Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {
            statement.setInt(1, id);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(mapCustomer(resultSet));
                }
            }

            return Optional.empty();

        } catch (SQLException exception) {
            throw new RuntimeException(
                    "Failed to find customer by id.",
                    exception
            );
        }
    }

    @Override
    public Optional<Customer> findByEmail(String email) {
        String sql = """
                SELECT
                    id,
                    full_name,
                    email,
                    phone,
                    registration_date
                FROM customers
                WHERE email = ?
                """;

        try (
                Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {
            statement.setString(1, email);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(mapCustomer(resultSet));
                }
            }

            return Optional.empty();

        } catch (SQLException exception) {
            throw new RuntimeException(
                    "Failed to find customer by email.",
                    exception
            );
        }
    }

    @Override
    public List<Customer> findAll() {
        String sql = """
                SELECT
                    id,
                    full_name,
                    email,
                    phone,
                    registration_date
                FROM customers
                ORDER BY full_name
                """;

        List<Customer> customers = new ArrayList<>();

        try (
                Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement =
                        connection.prepareStatement(sql);
                ResultSet resultSet = statement.executeQuery()
        ) {
            while (resultSet.next()) {
                customers.add(mapCustomer(resultSet));
            }

            return customers;

        } catch (SQLException exception) {
            throw new RuntimeException(
                    "Failed to load customers.",
                    exception
            );
        }
    }

    @Override
    public boolean update(Customer customer) {
        String sql = """
                UPDATE customers
                SET
                    full_name = ?,
                    email = ?,
                    phone = ?,
                    registration_date = ?
                WHERE id = ?
                """;

        try (
                Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {
            statement.setString(1, customer.getFullName());
            statement.setString(2, customer.getEmail());
            statement.setString(3, customer.getPhone());
            statement.setDate(
                    4,
                    Date.valueOf(customer.getRegistrationDate())
            );
            statement.setInt(5, customer.getId());

            return statement.executeUpdate() > 0;

        } catch (SQLException exception) {
            throw new RuntimeException(
                    "Failed to update customer.",
                    exception
            );
        }
    }

    @Override
    public boolean deleteById(int id) {
        String sql = """
                DELETE FROM customers
                WHERE id = ?
                """;

        try (
                Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {
            statement.setInt(1, id);

            return statement.executeUpdate() > 0;

        } catch (SQLException exception) {
            throw new RuntimeException(
                    "Failed to delete customer.",
                    exception
            );
        }
    }

    private Customer mapCustomer(ResultSet resultSet)
            throws SQLException {

        return new Customer(
                resultSet.getInt("id"),
                resultSet.getString("full_name"),
                resultSet.getString("email"),
                resultSet.getString("phone"),
                resultSet.getDate("registration_date")
                        .toLocalDate()
        );
    }
}