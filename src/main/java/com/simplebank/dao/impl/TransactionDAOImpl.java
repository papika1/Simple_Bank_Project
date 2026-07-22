package com.simplebank.dao.impl;

import com.simplebank.config.DatabaseConnection;
import com.simplebank.dao.TransactionDAO;
import com.simplebank.model.Transaction;
import com.simplebank.model.TransactionType;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TransactionDAOImpl implements TransactionDAO {

    @Override
    public boolean save(Transaction transaction) {
        String sql = """
                INSERT INTO transactions (
                    account_id,
                    related_account_id,
                    transaction_type,
                    amount,
                    transaction_date,
                    description
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
            statement.setInt(1, transaction.getAccountId());

            if (transaction.getRelatedAccountId() == null) {
                statement.setNull(
                        2,
                        java.sql.Types.INTEGER
                );
            } else {
                statement.setInt(
                        2,
                        transaction.getRelatedAccountId()
                );
            }

            statement.setString(
                    3,
                    transaction.getTransactionType().name()
            );

            statement.setBigDecimal(
                    4,
                    transaction.getAmount()
            );

            statement.setTimestamp(
                    5,
                    Timestamp.valueOf(
                            transaction.getTransactionDate()
                    )
            );

            statement.setString(
                    6,
                    transaction.getDescription()
            );

            int affectedRows = statement.executeUpdate();

            if (affectedRows == 0) {
                return false;
            }

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    transaction.setId(
                            generatedKeys.getInt(1)
                    );
                }
            }

            return true;

        } catch (SQLException exception) {
            throw new RuntimeException(
                    "Failed to save transaction.",
                    exception
            );
        }
    }

    @Override
    public Optional<Transaction> findById(int id) {
        String sql = """
                SELECT
                    id,
                    account_id,
                    related_account_id,
                    transaction_type,
                    amount,
                    transaction_date,
                    description
                FROM transactions
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
                    return Optional.of(
                            mapTransaction(resultSet)
                    );
                }
            }

            return Optional.empty();

        } catch (SQLException exception) {
            throw new RuntimeException(
                    "Failed to find transaction by id.",
                    exception
            );
        }
    }

    @Override
    public List<Transaction> findAll() {
        String sql = """
                SELECT
                    id,
                    account_id,
                    related_account_id,
                    transaction_type,
                    amount,
                    transaction_date,
                    description
                FROM transactions
                ORDER BY transaction_date DESC
                """;

        return findTransactions(sql);
    }

    @Override
    public List<Transaction> findByAccountId(int accountId) {
        String sql = """
                SELECT
                    id,
                    account_id,
                    related_account_id,
                    transaction_type,
                    amount,
                    transaction_date,
                    description
                FROM transactions
                WHERE account_id = ?
                ORDER BY transaction_date DESC
                """;

        List<Transaction> transactions = new ArrayList<>();

        try (
                Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {
            statement.setInt(1, accountId);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    transactions.add(
                            mapTransaction(resultSet)
                    );
                }
            }

            return transactions;

        } catch (SQLException exception) {
            throw new RuntimeException(
                    "Failed to find transactions by account id.",
                    exception
            );
        }
    }

    @Override
    public List<Transaction> findByType(
            TransactionType transactionType
    ) {
        String sql = """
                SELECT
                    id,
                    account_id,
                    related_account_id,
                    transaction_type,
                    amount,
                    transaction_date,
                    description
                FROM transactions
                WHERE transaction_type = ?
                ORDER BY transaction_date DESC
                """;

        List<Transaction> transactions = new ArrayList<>();

        try (
                Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {
            statement.setString(
                    1,
                    transactionType.name()
            );

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    transactions.add(
                            mapTransaction(resultSet)
                    );
                }
            }

            return transactions;

        } catch (SQLException exception) {
            throw new RuntimeException(
                    "Failed to find transactions by type.",
                    exception
            );
        }
    }

    @Override
    public List<Transaction> findByDateRange(
            java.time.LocalDateTime startDate,
            java.time.LocalDateTime endDate
    ) {
        String sql = """
                SELECT
                    id,
                    account_id,
                    related_account_id,
                    transaction_type,
                    amount,
                    transaction_date,
                    description
                FROM transactions
                WHERE transaction_date BETWEEN ? AND ?
                ORDER BY transaction_date DESC
                """;

        List<Transaction> transactions = new ArrayList<>();

        try (
                Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {
            statement.setTimestamp(
                    1,
                    Timestamp.valueOf(startDate)
            );

            statement.setTimestamp(
                    2,
                    Timestamp.valueOf(endDate)
            );

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    transactions.add(
                            mapTransaction(resultSet)
                    );
                }
            }

            return transactions;

        } catch (SQLException exception) {
            throw new RuntimeException(
                    "Failed to find transactions by date range.",
                    exception
            );
        }
    }

    private List<Transaction> findTransactions(String sql) {
        List<Transaction> transactions = new ArrayList<>();

        try (
                Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement =
                        connection.prepareStatement(sql);
                ResultSet resultSet = statement.executeQuery()
        ) {
            while (resultSet.next()) {
                transactions.add(
                        mapTransaction(resultSet)
                );
            }

            return transactions;

        } catch (SQLException exception) {
            throw new RuntimeException(
                    "Failed to load transactions.",
                    exception
            );
        }
    }

    private Transaction mapTransaction(ResultSet resultSet)
            throws SQLException {

        int relatedAccountId =
                resultSet.getInt("related_account_id");

        Integer nullableRelatedAccountId =
                resultSet.wasNull()
                        ? null
                        : relatedAccountId;

        return new Transaction(
                resultSet.getInt("id"),
                resultSet.getInt("account_id"),
                nullableRelatedAccountId,
                TransactionType.valueOf(
                        resultSet.getString(
                                "transaction_type"
                        )
                ),
                resultSet.getBigDecimal("amount"),
                resultSet.getTimestamp(
                        "transaction_date"
                ).toLocalDateTime(),
                resultSet.getString("description")
        );
    }
}