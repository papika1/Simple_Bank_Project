# Simple Bank

A desktop banking application built with **Java**, **Swing**, **JDBC**, **MySQL**, **Maven**, and **JUnit 5**.

The goal of this project is to demonstrate junior-level Java backend and desktop application skills through a small banking system with customers, bank accounts, transactions, database access, validation, exception handling, and automated tests.

---

## Features

### Customer management
- Create new customers
- List all customers
- Search customers by ID or email
- Update customer data
- Delete customers when no dependent records exist

### Account management
- Create bank accounts for existing customers
- Support `CURRENT` and `SAVINGS` account types
- Store account balance
- Set account status to:
  - `ACTIVE`
  - `BLOCKED`
  - `CLOSED`
- List all accounts
- Search accounts by ID or account number
- List all accounts belonging to a customer
- Update balance and account status

### Banking operations
- Deposit money
- Withdraw money
- Transfer money between accounts
- Check account balance
- Validate account status before transactions
- Reject invalid or negative amounts
- Reject withdrawals when the balance is insufficient
- Reject transfers to the same account

### Transaction history
Supported transaction types:

- `DEPOSIT`
- `WITHDRAWAL`
- `TRANSFER_IN`
- `TRANSFER_OUT`

The application stores:
- account ID
- related account ID when applicable
- transaction type
- amount
- date and time
- description

### Desktop GUI
The application uses Java Swing and contains the following views:

- Customers
- Accounts
- New Transaction
- Transaction History

---

## Technologies

- Java 21
- Maven
- Java Swing
- JDBC
- MySQL
- JUnit 5
- Eclipse IDE

---

## Project Structure

```text
simple-bank/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com.simplebank/
│   │   │       ├── config/
│   │   │       │   └── DatabaseConnection.java
│   │   │       │
│   │   │       ├── dao/
│   │   │       │   ├── UserDAO.java
│   │   │       │   ├── AccountDAO.java
│   │   │       │   └── TransactionDAO.java
│   │   │       │
│   │   │       ├── dao.impl/
│   │   │       │   ├── UserDAOImpl.java
│   │   │       │   ├── AccountDAOImpl.java
│   │   │       │   └── TransactionDAOImpl.java
│   │   │       │
│   │   │       ├── exception/
│   │   │       │   ├── AccountNotFoundException.java
│   │   │       │   ├── AccountUnavailableException.java
│   │   │       │   ├── InsufficientBalanceException.java
│   │   │       │   └── InvalidAmountException.java
│   │   │       │
│   │   │       ├── main/
│   │   │       │   └── Main.java
│   │   │       │
│   │   │       ├── model/
│   │   │       │   ├── Customer.java
│   │   │       │   ├── BankAccount.java
│   │   │       │   ├── Transaction.java
│   │   │       │   ├── AccountType.java
│   │   │       │   └── TransactionType.java
│   │   │       │
│   │   │       ├── service/
│   │   │       │   └── BankingService.java
│   │   │       │
│   │   │       └── ui/
│   │   │           ├── MainFrame.java
│   │   │           ├── CustomerPanel.java
│   │   │           ├── AccountPanel.java
│   │   │           ├── TransactionPanel.java
│   │   │           └── TransactionHistoryPanel.java
│   │   │
│   │   └── resources/
│   │
│   └── test/
│       ├── java/
│       │   └── com.simplebank/
│       │       ├── config/
│       │       │   └── TestDatabaseConnection.java
│       │       ├── dao.impl/
│       │       │   ├── UserDAOImplTest.java
│       │       │   ├── AccountDAOImplTest.java
│       │       │   └── TransactionDAOImplTest.java
│       │       └── service/
│       │           └── BankingServiceTest.java
│       │
│       └── resources/
│           └── config-test.properties
│
├── database/
│   ├── schema.sql
│   └── test_schema.sql
│
├── config.example.properties
├── config-test.example.properties
├── .gitignore
├── pom.xml
└── README.md
```

---

## Architecture

The application follows a simple layered structure.

```text
Swing UI
   ↓
Service Layer
   ↓
DAO Interfaces
   ↓
DAO Implementations
   ↓
JDBC
   ↓
MySQL
```

### Model layer

The model classes represent application data.

#### `Customer`
Represents a bank customer.

Main fields:
- `id`
- `fullName`
- `email`
- `phone`
- `registrationDate`

#### `BankAccount`
Represents a bank account.

Main fields:
- `id`
- `accountNumber`
- `customerId`
- `accountType`
- `balance`
- `status`
- `createdAt`

Money values are stored using `BigDecimal` instead of `double` to avoid floating-point precision issues.

#### `Transaction`
Represents a banking transaction.

Main fields:
- `id`
- `accountId`
- `relatedAccountId`
- `transactionType`
- `amount`
- `transactionDate`
- `description`

`relatedAccountId` is nullable because deposits and withdrawals do not need a second account.

---

## DAO Layer

DAO stands for **Data Access Object**.

The DAO layer is responsible for communication with the database.

### `UserDAO`
Provides customer-related database operations.

Examples:
- save customer
- find customer by ID
- find customer by email
- list all customers
- update customer
- delete customer

### `AccountDAO`
Provides account-related database operations.

Examples:
- save account
- find account by ID
- find account by account number
- list accounts
- list accounts by customer
- update balance
- update status

### `TransactionDAO`
Provides transaction-related database operations.

Examples:
- save transaction
- find transaction by ID
- list all transactions
- filter by account
- filter by transaction type
- filter by date range

The interfaces define what operations are available, while the `DAOImpl` classes contain the actual JDBC implementation.

Example:

```java
AccountDAO accountDAO = new AccountDAOImpl();
```

The variable uses the interface type, but the actual object is an `AccountDAOImpl`.

---

## Service Layer

The `BankingService` contains the business logic of the application.

It is responsible for:

- validating transaction amounts
- checking account existence
- checking account status
- calculating new balances
- preventing invalid transfers
- creating transaction records

Example deposit flow:

```text
Deposit request
   ↓
Validate amount
   ↓
Find account
   ↓
Check account status
   ↓
Calculate new balance
   ↓
Update account
   ↓
Save transaction
```

Example transfer flow:

```text
Transfer request
   ↓
Validate amount
   ↓
Validate source and target accounts
   ↓
Check account statuses
   ↓
Check source balance
   ↓
Decrease source balance
   ↓
Increase target balance
   ↓
Save TRANSFER_OUT
   ↓
Save TRANSFER_IN
```

---

## Custom Exceptions

The project uses custom runtime exceptions to represent business errors.

### `InvalidAmountException`
Thrown when the amount is zero, negative, or invalid.

### `InsufficientBalanceException`
Thrown when an account does not have enough money for a withdrawal or transfer.

### `AccountNotFoundException`
Thrown when the requested bank account does not exist.

### `AccountUnavailableException`
Thrown when an account is not in an active state.

These exceptions make business errors easier to understand and handle in the GUI.

---

## Database

The application uses MySQL.

The main database contains three tables:

```text
customers
accounts
transactions
```

### Relationship

```text
customers
   │
   └──< accounts
            │
            └──< transactions
```

One customer can have multiple accounts.

One account can have multiple transactions.

---

## Database Setup

Create the production database by running:

```text
database/schema.sql
```

Create the test database by running:

```text
database/test_schema.sql
```

Recommended database names:

```text
simple_bank
simple_bank_test
```

---

## Database Configuration

Real database credentials are intentionally not stored in Git.

Create a local file:

```text
config.properties
```

Example:

```properties
db.url=jdbc:mysql://localhost:3306/simple_bank
db.username=root
db.password=your_password
```

For tests, create:

```text
src/test/resources/config-test.properties
```

Example:

```properties
db.url=jdbc:mysql://localhost:3306/simple_bank_test
db.username=root
db.password=your_password
```

Template files can safely be committed:

```text
config.example.properties
config-test.example.properties
```

Real configuration files must remain ignored by Git.

Example `.gitignore` entries:

```gitignore
config.properties
src/test/resources/config-test.properties
target/
.classpath
.project
.settings/
```

---

## Security Note

Database passwords must never be committed to a public Git repository.

The repository should contain only example configuration files with placeholder credentials.

If a real password is accidentally committed, changing the file later is not enough because Git keeps commit history. The password should also be changed in MySQL.

---

## Running the Application

### 1. Clone the repository

```bash
git clone YOUR_REPOSITORY_URL
```

### 2. Open the project in Eclipse

Import as:

```text
Existing Maven Project
```

### 3. Update Maven

In Eclipse:

```text
Right click project
→ Maven
→ Update Project
```

### 4. Create the database

Run:

```text
database/schema.sql
```

### 5. Create `config.properties`

Use:

```text
config.example.properties
```

as a template.

### 6. Start the application

Run:

```text
com.simplebank.main.Main
```

as a Java Application.

---

## GUI Overview

### Customers tab
Allows the user to:
- list customers
- add new customers
- refresh customer data

### Accounts tab
Allows the user to:
- create accounts
- list accounts
- block accounts
- activate accounts

A new account can only be created for an existing customer.

### New Transaction tab
Allows the user to:
- deposit money
- withdraw money
- transfer money

The service layer performs the required validation before the database is updated.

### Transaction History tab
Allows the user to:
- list all transactions
- search transactions by account ID

---

## Testing

The project uses JUnit 5.

### Unit Tests

`BankingServiceTest` tests business logic without using the real database.

Typical test cases:

- deposit increases balance
- deposit creates a transaction
- zero amount is rejected
- negative amount is rejected
- withdrawal decreases balance
- withdrawal fails when balance is too low
- blocked account rejects transactions
- transfer changes both balances
- transfer creates two transaction records
- transfer to the same account is rejected

Fake DAO implementations are used in memory so the unit tests do not modify MySQL data.

### Integration Tests

DAO tests use the dedicated `simple_bank_test` database.

Examples:

- `UserDAOImplTest`
- `AccountDAOImplTest`
- `TransactionDAOImplTest`

These tests verify that JDBC and SQL operations work correctly with MySQL.

Run all tests with Maven:

```bash
mvn test
```

Or in Eclipse:

```text
Right click test class
→ Run As
→ JUnit Test
```

---

## Maven

The project is built with Maven.

Important dependencies:

- MySQL Connector/J
- JUnit Jupiter

Compile:

```bash
mvn clean compile
```

Run tests:

```bash
mvn test
```

Build:

```bash
mvn clean package
```

---

## Main Concepts Demonstrated

This project demonstrates:

- object-oriented programming
- POJO classes
- enums
- interfaces
- polymorphism
- dependency injection through constructors
- JDBC
- prepared statements
- SQL CRUD operations
- MySQL foreign keys
- Java collections
- `Optional`
- `BigDecimal`
- exception handling
- custom exceptions
- Swing GUI
- layered architecture
- Maven
- JUnit 5
- unit testing
- integration testing
- configuration management
- Git / GitHub

---

## Important Design Decisions

### Why use `BigDecimal`?

Money should not be represented with `double` because floating-point numbers can introduce precision errors.

Example:

```java
BigDecimal amount = new BigDecimal("1000.50");
```

### Why use DAO interfaces?

DAO interfaces separate the required database operations from their implementation.

```java
AccountDAO accountDAO = new AccountDAOImpl();
```

This makes the service layer depend on an abstraction instead of a specific implementation.

### Why use `Optional`?

Database searches may return no result.

Instead of returning `null`:

```java
Optional<BankAccount> findById(int id);
```

makes it explicit that an account may or may not exist.

### Why use a service layer?

The GUI should not contain banking business logic.

For example, the GUI asks:

```java
bankingService.withdraw(...);
```

The service decides whether the withdrawal is valid.

### Why keep transactions separate from the GUI?

The Swing layer should only collect user input and display results.

Database access belongs in DAO classes, and banking rules belong in `BankingService`.

---

## Future Improvements

Possible future improvements:

- JDBC transaction handling for transfers
- automatic account number generation
- stronger input validation
- transaction filtering by date and type
- customer editing
- account closing workflow
- improved GUI styling
- logging
- password-protected application users
- export transaction history to CSV
- additional JUnit test coverage

---

## Current Limitation

A money transfer currently performs multiple database operations.

A production-quality banking application should wrap the complete transfer operation in one JDBC transaction using:

```java
connection.setAutoCommit(false);
connection.commit();
connection.rollback();
```

This would ensure that either all transfer steps succeed or none of them are stored.

This is a planned improvement for the project.

---

## Author

Developed as a junior Java portfolio project.

---

## License

This project is intended for educational and portfolio purposes.
