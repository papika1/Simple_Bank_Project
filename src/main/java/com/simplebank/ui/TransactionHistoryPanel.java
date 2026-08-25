package com.simplebank.ui;

import com.simplebank.dao.TransactionDAO;
import com.simplebank.model.Transaction;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class TransactionHistoryPanel extends JPanel {

    private final TransactionDAO transactionDAO;

    private final DefaultTableModel tableModel;

    private final JTextField accountIdField;

    public TransactionHistoryPanel(
            TransactionDAO transactionDAO
    ) {
        this.transactionDAO = transactionDAO;

        setLayout(new BorderLayout(10, 10));

        setBorder(BorderFactory.createEmptyBorder(
                15, 15, 15, 15
        ));

        String[] columns = {
                "ID",
                "Account ID",
                "Related Account",
                "Type",
                "Amount",
                "Date",
                "Description"
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

        JTable table =
                new JTable(tableModel);

        table.setRowHeight(25);

        add(
                new JScrollPane(table),
                BorderLayout.CENTER
        );

        JPanel filterPanel =
                new JPanel(new FlowLayout(FlowLayout.LEFT));

        accountIdField =
                new JTextField(10);

        JButton searchButton =
                new JButton("Search");

        JButton showAllButton =
                new JButton("Show All");

        filterPanel.add(new JLabel("Account ID"));
        filterPanel.add(accountIdField);
        filterPanel.add(searchButton);
        filterPanel.add(showAllButton);

        add(
                filterPanel,
                BorderLayout.NORTH
        );

        searchButton.addActionListener(
                event -> searchByAccount()
        );

        showAllButton.addActionListener(
                event -> loadAllTransactions()
        );

        loadAllTransactions();
    }

    private void searchByAccount() {

        try {

            int accountId =
                    Integer.parseInt(
                            accountIdField
                                    .getText()
                                    .trim()
                    );

            loadTransactions(
                    transactionDAO
                            .findByAccountId(accountId)
            );

        } catch (NumberFormatException exception) {

            JOptionPane.showMessageDialog(
                    this,
                    "Account ID must be a number."
            );
        }
    }

    private void loadAllTransactions() {

        loadTransactions(
                transactionDAO.findAll()
        );
    }

    private void loadTransactions(
            java.util.List<Transaction> transactions
    ) {

        tableModel.setRowCount(0);

        for (Transaction transaction :
                transactions) {

            tableModel.addRow(new Object[]{
                    transaction.getId(),
                    transaction.getAccountId(),
                    transaction.getRelatedAccountId(),
                    transaction.getTransactionType(),
                    transaction.getAmount(),
                    transaction.getTransactionDate(),
                    transaction.getDescription()
            });
        }
    }
}