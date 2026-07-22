package com.simplebank.dao;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import com.simplebank.model.AccountStatus;
import com.simplebank.model.BankAccount;

public interface AccountDAO {
	

    boolean save(BankAccount account);

    Optional<BankAccount> findById(int id);

    Optional<BankAccount> findByAccountNumber(String accountNumber);

    List<BankAccount> findAll();

    List<BankAccount> findByCustomerId(int customerId);

    boolean update(BankAccount account);

    boolean updateBalance(int accountId, BigDecimal newBalance);

    boolean updateStatus(int accountId, AccountStatus status);

    boolean deleteById(int id);
	
}
