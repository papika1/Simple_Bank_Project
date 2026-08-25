package com.simplebank.ui;

import com.simplebank.dao.AccountDAO;
import com.simplebank.model.AccountStatus;
import com.simplebank.model.AccountType;
import com.simplebank.model.BankAccount;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.time.LocalDate;

public class AccountPanel extends JPanel {

    private final AccountDAO accountDAO;

    private final DefaultTableModel tableModel;
    private final JTable table;

    private final JTextField accountNumberField;
    private final JTextField customerIdField;

    private final JComboBox<AccountType> typeComboBox;

    public AccountPanel(AccountDAO accountDAO) {

        this.accountDAO = accountDAO;

        setLayout(new BorderLayout(10, 10));

        setBorder(BorderFactory.createEmptyBorder(
                15, 15, 15, 15
        ));

        String[] columns = {
                "ID",
                "Account Number",
                "Customer ID",
                "Type",
                "Balance",
                "Status",
                "Created"
        };

        tableModel =
                new DefaultTableModel(columns, 0) {

                    @Override
                    public boolean isCellEditable(
                            int row,
                            int column
                    ) {
                        return false;
                    }
                };

        table = new JTable(tableModel);
        table.setRowHeight(25);

        add(
                new JScrollPane(table),
                BorderLayout.CENTER
        );

        JPanel formPanel = new JPanel(
                new GridLayout(2, 4, 10, 10)
        );

        accountNumberField = new JTextField();
        customerIdField = new JTextField();

        typeComboBox =
                new JComboBox<>(AccountType.values());

        JButton createButton =
                new JButton("Create Account");

        formPanel.add(new JLabel("Account Number"));
        formPanel.add(new JLabel("Customer ID"));
        formPanel.add(new JLabel("Account Type"));
        formPanel.add(new JLabel(""));

        formPanel.add(accountNumberField);
        formPanel.add(customerIdField);
        formPanel.add(typeComboBox);
        formPanel.add(createButton);

        add(
                formPanel,
                BorderLayout.NORTH
        );

        JButton blockButton =
                new JButton("Block");

        JButton activateButton =
                new JButton("Activate");

        JButton refreshButton =
                new JButton("Refresh");

        JPanel buttonPanel =
                new JPanel(new FlowLayout(FlowLayout.RIGHT));

        buttonPanel.add(blockButton);
        buttonPanel.add(activateButton);
        buttonPanel.add(refreshButton);

        add(
                buttonPanel,
                BorderLayout.SOUTH
        );

        createButton.addActionListener(
                event -> createAccount()
        );

        blockButton.addActionListener(
                event -> changeStatus(AccountStatus.BLOCKED)
        );

        activateButton.addActionListener(
                event -> changeStatus(AccountStatus.ACTIVE)
        );

        refreshButton.addActionListener(
                event -> loadAccounts()
        );

        loadAccounts();
    }

    private void createAccount() {

        try {

            int customerId =
                    Integer.parseInt(
                            customerIdField.getText().trim()
                    );

            String accountNumber =
                    accountNumberField.getText().trim();

            if (accountNumber.isEmpty()) {

                JOptionPane.showMessageDialog(
                        this,
                        "Account number is required."
                );

                return;
            }

            BankAccount account =
                    new BankAccount(
                            accountNumber,
                            customerId,
                            (AccountType)
                                    typeComboBox.getSelectedItem(),
                            BigDecimal.ZERO,
                            AccountStatus.ACTIVE,
                            LocalDate.now()
                    );

            accountDAO.save(account);

            accountNumberField.setText("");
            customerIdField.setText("");

            loadAccounts();

            JOptionPane.showMessageDialog(
                    this,
                    "Account created successfully."
            );

        } catch (NumberFormatException exception) {

            JOptionPane.showMessageDialog(
                    this,
                    "Customer ID must be a number.",
                    "Validation Error",
                    JOptionPane.WARNING_MESSAGE
            );

        } catch (RuntimeException exception) {

            showError(exception);
        }
    }

    private void changeStatus(AccountStatus status) {

        int row = table.getSelectedRow();

        if (row == -1) {

            JOptionPane.showMessageDialog(
                    this,
                    "Select an account first."
            );

            return;
        }

        int accountId =
                (int) tableModel.getValueAt(row, 0);

        accountDAO.updateStatus(
                accountId,
                status
        );

        loadAccounts();
    }

    private void loadAccounts() {

        tableModel.setRowCount(0);

        for (BankAccount account :
                accountDAO.findAll()) {

            tableModel.addRow(new Object[]{
                    account.getId(),
                    account.getAccountNumber(),
                    account.getCustomerId(),
                    account.getAccountType(),
                    account.getBalance(),
                    account.getStatus(),
                    account.getCreatedAt()
            });
        }
    }

    private void showError(Exception exception) {

        JOptionPane.showMessageDialog(
                this,
                exception.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE
        );
    }
}