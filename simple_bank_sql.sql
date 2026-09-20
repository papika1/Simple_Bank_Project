CREATE DATABASE IF NOT EXISTS simple_bank
CHARACTER SET utf8mb4
COLLATE utf8mb4_hungarian_ci;

USE simple_bank;

DROP TABLE IF EXISTS transactions;
DROP TABLE IF EXISTS accounts;
DROP TABLE IF EXISTS customers;


CREATE TABLE customers (
    id INT AUTO_INCREMENT PRIMARY KEY,

    full_name VARCHAR(100) NOT NULL,

    email VARCHAR(100) NOT NULL UNIQUE,

    phone VARCHAR(30),

    registration_date DATE NOT NULL,

    CONSTRAINT chk_customer_name
        CHECK (CHAR_LENGTH(TRIM(full_name)) >= 2)
);


CREATE TABLE accounts (
    id INT AUTO_INCREMENT PRIMARY KEY,

    account_number VARCHAR(30) NOT NULL UNIQUE,

    customer_id INT NOT NULL,

    account_type ENUM(
        'CURRENT',
        'SAVINGS'
    ) NOT NULL,

    balance DECIMAL(15, 2) NOT NULL DEFAULT 0.00,

    status ENUM(
        'ACTIVE',
        'BLOCKED',
        'CLOSED'
    ) NOT NULL DEFAULT 'ACTIVE',

    created_at DATE NOT NULL,

    CONSTRAINT fk_accounts_customer
        FOREIGN KEY (customer_id)
        REFERENCES customers(id)
        ON DELETE RESTRICT,

    CONSTRAINT chk_account_balance
        CHECK (balance >= 0)
);


CREATE TABLE transactions (
    id INT AUTO_INCREMENT PRIMARY KEY,

    account_id INT NOT NULL,

    related_account_id INT NULL,

    transaction_type ENUM(
        'DEPOSIT',
        'WITHDRAWAL',
        'TRANSFER_IN',
        'TRANSFER_OUT'
    ) NOT NULL,

    amount DECIMAL(15, 2) NOT NULL,

    transaction_date DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,

    description VARCHAR(255),

    CONSTRAINT fk_transactions_account
        FOREIGN KEY (account_id)
        REFERENCES accounts(id)
        ON UPDATE CASCADE
        ON DELETE RESTRICT,

    CONSTRAINT fk_transactions_related_account
        FOREIGN KEY (related_account_id)
        REFERENCES accounts(id)
        ON UPDATE CASCADE
        ON DELETE RESTRICT,

    CONSTRAINT chk_transaction_amount
        CHECK (amount > 0)


);


CREATE INDEX idx_accounts_customer_id
ON accounts(customer_id);

CREATE INDEX idx_transactions_account_id
ON transactions(account_id);

CREATE INDEX idx_transactions_date
ON transactions(transaction_date);

