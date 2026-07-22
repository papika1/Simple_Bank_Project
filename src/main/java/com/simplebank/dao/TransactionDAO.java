package com.simplebank.dao;

import com.simplebank.model.Transaction;
import com.simplebank.model.TransactionType;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface TransactionDAO {
	

    boolean save(Transaction transaction);

    Optional<Transaction> findById(int id);

    List<Transaction> findAll();

    List<Transaction> findByAccountId(int accountId);

    List<Transaction> findByType(TransactionType transactionType);

    List<Transaction> findByDateRange(
            LocalDateTime startDate,
            LocalDateTime endDate
    );
	
}
