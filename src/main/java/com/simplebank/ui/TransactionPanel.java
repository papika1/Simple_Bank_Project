package com.simplebank.ui;

import com.simplebank.service.BankingService;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;

public class TransactionPanel extends JPanel {

    private final BankingService bankingService;

    private final JComboBox<String> typeComboBox;

    private final JTextField sourceAccountField;
    private final JTextField targetAccountField;
    private final JTextField amountField;
    private final JTextField descriptionField;

    public TransactionPanel(
            BankingService bankingService
    ) {
        this.bankingService = bankingService;

        setLayout(new BorderLayout());

        setBorder(BorderFactory.createEmptyBorder(
                40, 100, 40, 100
        ));

        JPanel formPanel =
                new JPanel(new GridLayout(
                        6, 2, 15, 15
                ));

        typeComboBox =
                new JComboBox<>(new String[]{
                        "DEPOSIT",
                        "WITHDRAWAL",
                        "TRANSFER"
                });

        sourceAccountField = new JTextField();
        targetAccountField = new JTextField();
        amountField = new JTextField();
        descriptionField = new JTextField();

        JButton executeButton =
                new JButton("Execute Transaction");

        formPanel.add(new JLabel("Transaction Type"));
        formPanel.add(typeComboBox);

        formPanel.add(new JLabel("Account ID"));
        formPanel.add(sourceAccountField);

        formPanel.add(new JLabel("Target Account ID"));
        formPanel.add(targetAccountField);

        formPanel.add(new JLabel("Amount"));
        formPanel.add(amountField);

        formPanel.add(new JLabel("Description"));
        formPanel.add(descriptionField);

        formPanel.add(new JLabel(""));
        formPanel.add(executeButton);

        add(
                formPanel,
                BorderLayout.NORTH
        );

        typeComboBox.addActionListener(
                event -> updateFields()
        );

        executeButton.addActionListener(
                event -> executeTransaction()
        );

        updateFields();
    }

    private void updateFields() {

        String type =
                (String) typeComboBox.getSelectedItem();

        targetAccountField.setEnabled(
                "TRANSFER".equals(type)
        );
    }

    private void executeTransaction() {

        try {

            int accountId =
                    Integer.parseInt(
                            sourceAccountField
                                    .getText()
                                    .trim()
                    );

            BigDecimal amount =
                    new BigDecimal(
                            amountField
                                    .getText()
                                    .trim()
                    );

            String description =
                    descriptionField
                            .getText()
                            .trim();

            String type =
                    (String)
                            typeComboBox.getSelectedItem();

            switch (type) {

                case "DEPOSIT" ->
                        bankingService.deposit(
                                accountId,
                                amount,
                                description
                        );

                case "WITHDRAWAL" ->
                        bankingService.withdraw(
                                accountId,
                                amount,
                                description
                        );

                case "TRANSFER" -> {

                    int targetAccountId =
                            Integer.parseInt(
                                    targetAccountField
                                            .getText()
                                            .trim()
                            );

                    bankingService.transfer(
                            accountId,
                            targetAccountId,
                            amount,
                            description
                    );
                }
            }

            clearFields();

            JOptionPane.showMessageDialog(
                    this,
                    "Transaction completed successfully."
            );

        } catch (Exception exception) {

            JOptionPane.showMessageDialog(
                    this,
                    exception.getMessage(),
                    "Transaction Failed",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void clearFields() {

        sourceAccountField.setText("");
        targetAccountField.setText("");
        amountField.setText("");
        descriptionField.setText("");
    }
}