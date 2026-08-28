package com.simplebank.main;

import com.simplebank.dao.AccountDAO;
import com.simplebank.dao.TransactionDAO;
import com.simplebank.dao.UserDAO;
import com.simplebank.dao.impl.AccountDAOImpl;
import com.simplebank.dao.impl.TransactionDAOImpl;
import com.simplebank.dao.impl.UserDAOImpl;
import com.simplebank.service.BankingService;
import com.simplebank.ui.MainFrame;

import javax.swing.SwingUtilities;

public class Main {

    public static void main(String[] args) {

    	
        SwingUtilities.invokeLater(() -> {

            UserDAO userDAO = new UserDAOImpl();
            AccountDAO accountDAO = new AccountDAOImpl();
            TransactionDAO transactionDAO = new TransactionDAOImpl();

            BankingService bankingService =
                    new BankingService(
                            accountDAO,
                            transactionDAO
                    );

            MainFrame mainFrame =
                    new MainFrame(
                            userDAO,
                            accountDAO,
                            transactionDAO,
                            bankingService
                    );

            mainFrame.setVisible(true);
        });
    }
}