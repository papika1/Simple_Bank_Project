package com.simplebank.dao.impl;

import com.simplebank.config.DatabaseConnection;
import com.simplebank.dao.AccountDAO;
import com.simplebank.model.AccountStatus;
import com.simplebank.model.AccountType;
import com.simplebank.model.BankAccount;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AccountDAOImpl implements AccountDAO {

    @Override
    public boolean save(BankAccount account) {
        String sql = """
                INSERT INTO accounts (
                    account_number,
                    customer_id,
                    account_type,
                    balance,
                    status,
                    created_at
                )
                VALUES (?, ?, ?, ?, ?, ?)
                """;

        try (
                Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(
                        sql,
                        Statement.RETURN_GENERATED_KEYS
                )
        ) {
            statement.setString(1, account.getAccountNumber());
            statement.setInt(2, account.getCustomerId());
            statement.setString(
                    3,
                    account.getAccountType().name()
            );
            statement.setBigDecimal(4, account.getBalance());
            statement.setString(
                    5,
                    account.getStatus().name()
            );
            statement.setDate(
                    6,
                    Date.valueOf(account.getCreatedAt())
            );

            int affectedRows = statement.executeUpdate();

            if (affectedRows == 0) {
                return false;
            }

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    account.setId(generatedKeys.getInt(1));
                }
            }

            return true;

        } catch (SQLException exception) {
            throw new RuntimeException(
                    "Failed to save account.",
                    exception
            );
        }
    }

    @Override
    public Optional<BankAccount> findById(int id) {
        String sql = """
                SELECT
                    id,
                    account_number,
                    customer_id,
                    account_type,
                    balance,
                    status,
                    created_at
                FROM accounts
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
                    return Optional.of(mapAccount(resultSet));
                }
            }

            return Optional.empty();

        } catch (SQLException exception) {
            throw new RuntimeException(
                    "Failed to find account by id.",
                    exception
            );
        }
    }

    @Override
    public Optional<BankAccount> findByAccountNumber(
            String accountNumber
    ) {
        String sql = """
                SELECT
                    id,
                    account_number,
                    customer_id,
                    account_type,
                    balance,
                    status,
                    created_at
                FROM accounts
                WHERE account_number = ?
                """;

        try (
                Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {
            statement.setString(1, accountNumber);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(mapAccount(resultSet));
                }
            }

            return Optional.empty();

        } catch (SQLException exception) {
            throw new RuntimeException(
                    "Failed to find account by account number.",
                    exception
            );
        }
    }

    @Override
    public List<BankAccount> findAll() {
        String sql = """
                SELECT
                    id,
                    account_number,
                    customer_id,
                    account_type,
                    balance,
                    status,
                    created_at
                FROM accounts
                ORDER BY id
                """;

        List<BankAccount> accounts = new ArrayList<>();

        try (
                Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement =
                        connection.prepareStatement(sql);
                ResultSet resultSet = statement.executeQuery()
        ) {
            while (resultSet.next()) {
                accounts.add(mapAccount(resultSet));
            }

            return accounts;

        } catch (SQLException exception) {
            throw new RuntimeException(
                    "Failed to load accounts.",
                    exception
            );
        }
    }

    @Override
    public List<BankAccount> findByCustomerId(int customerId) {
        String sql = """
                SELECT
                    id,
                    account_number,
                    customer_id,
                    account_type,
                    balance,
                    status,
                    created_at
                FROM accounts
                WHERE customer_id = ?
                ORDER BY id
                """;

        List<BankAccount> accounts = new ArrayList<>();

        try (
                Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {
            statement.setInt(1, customerId);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    accounts.add(mapAccount(resultSet));
                }
            }

            return accounts;

        } catch (SQLException exception) {
            throw new RuntimeException(
                    "Failed to find accounts by customer id.",
                    exception
            );
        }
    }

    @Override
    public boolean update(BankAccount account) {
        String sql = """
                UPDATE accounts
                SET
                    account_number = ?,
                    customer_id = ?,
                    account_type = ?,
                    balance = ?,
                    status = ?,
                    created_at = ?
                WHERE id = ?
                """;

        try (
                Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {
            statement.setString(1, account.getAccountNumber());
            statement.setInt(2, account.getCustomerId());
            statement.setString(
                    3,
                    account.getAccountType().name()
            );
            statement.setBigDecimal(4, account.getBalance());
            statement.setString(
                    5,
                    account.getStatus().name()
            );
            statement.setDate(
                    6,
                    Date.valueOf(account.getCreatedAt())
            );
            statement.setInt(7, account.getId());

            return statement.executeUpdate() > 0;

        } catch (SQLException exception) {
            throw new RuntimeException(
                    "Failed to update account.",
                    exception
            );
        }
    }

    @Override
    public boolean updateBalance(
            int accountId,
            BigDecimal newBalance
    ) {
        String sql = """
                UPDATE accounts
                SET balance = ?
                WHERE id = ?
                """;

        try (
                Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {
            statement.setBigDecimal(1, newBalance);
            statement.setInt(2, accountId);

            return statement.executeUpdate() > 0;

        } catch (SQLException exception) {
            throw new RuntimeException(
                    "Failed to update account balance.",
                    exception
            );
        }
    }

    @Override
    public boolean updateStatus(
            int accountId,
            AccountStatus status
    ) {
        String sql = """
                UPDATE accounts
                SET status = ?
                WHERE id = ?
                """;

        try (
                Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {
            statement.setString(1, status.name());
            statement.setInt(2, accountId);

            return statement.executeUpdate() > 0;

        } catch (SQLException exception) {
            throw new RuntimeException(
                    "Failed to update account status.",
                    exception
            );
        }
    }

    @Override
    public boolean deleteById(int id) {
        String sql = """
                DELETE FROM accounts
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
                    "Failed to delete account.",
                    exception
            );
        }
    }

    private BankAccount mapAccount(ResultSet resultSet)
            throws SQLException {

        return new BankAccount(
                resultSet.getInt("id"),
                resultSet.getString("account_number"),
                resultSet.getInt("customer_id"),
                AccountType.valueOf(
                        resultSet.getString("account_type")
                ),
                resultSet.getBigDecimal("balance"),
                AccountStatus.valueOf(
                        resultSet.getString("status")
                ),
                resultSet.getDate("created_at")
                        .toLocalDate()
        );
    }
}