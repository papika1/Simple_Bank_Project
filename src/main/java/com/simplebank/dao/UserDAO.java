package com.simplebank.dao;

import java.util.List;
import java.util.Optional;

import com.simplebank.model.Customer;

public interface UserDAO {
	

    boolean save(Customer customer);

    Optional<Customer> findById(int id);

    Optional<Customer> findByEmail(String email);

    List<Customer> findAll();

    boolean update(Customer customer);

    boolean deleteById(int id);
	
}
