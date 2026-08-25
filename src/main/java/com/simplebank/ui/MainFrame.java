package com.simplebank.ui;

import com.simplebank.dao.AccountDAO;
import com.simplebank.dao.TransactionDAO;
import com.simplebank.dao.UserDAO;
import com.simplebank.service.BankingService;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {

    public MainFrame(
            UserDAO userDAO,
            AccountDAO accountDAO,
            TransactionDAO transactionDAO,
            BankingService bankingService
    ) {
        setTitle("Simple Bank");
        setSize(950, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JTabbedPane tabbedPane = new JTabbedPane();

        tabbedPane.addTab(
                "Customers",
                new CustomerPanel(userDAO)
        );

        tabbedPane.addTab(
                "Accounts",
                new AccountPanel(accountDAO)
        );

        tabbedPane.addTab(
                "New Transaction",
                new TransactionPanel(bankingService)
        );

        tabbedPane.addTab(
                "Transaction History",
                new TransactionHistoryPanel(transactionDAO)
        );

        add(tabbedPane, BorderLayout.CENTER);
    }
}