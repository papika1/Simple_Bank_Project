package com.simplebank.ui;

import com.simplebank.dao.UserDAO;
import com.simplebank.model.Customer;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;

public class CustomerPanel extends JPanel {

    private final UserDAO userDAO;

    private final DefaultTableModel tableModel;

    private final JTextField nameField;
    private final JTextField emailField;
    private final JTextField phoneField;

    public CustomerPanel(UserDAO userDAO) {
        this.userDAO = userDAO;

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(
                15, 15, 15, 15
        ));

        String[] columns = {
                "ID",
                "Full Name",
                "Email",
                "Phone",
                "Registration Date"
        };

        tableModel = new DefaultTableModel(columns, 0) {

            @Override
            public boolean isCellEditable(
                    int row,
                    int column
            ) {
                return false;
            }
        };

        JTable table = new JTable(tableModel);
        table.setRowHeight(25);

        add(
                new JScrollPane(table),
                BorderLayout.CENTER
        );

        JPanel formPanel = new JPanel(
                new GridLayout(2, 4, 10, 10)
        );

        nameField = new JTextField();
        emailField = new JTextField();
        phoneField = new JTextField();

        formPanel.add(new JLabel("Full Name"));
        formPanel.add(new JLabel("Email"));
        formPanel.add(new JLabel("Phone"));
        formPanel.add(new JLabel(""));

        formPanel.add(nameField);
        formPanel.add(emailField);
        formPanel.add(phoneField);

        JButton addButton =
                new JButton("Add Customer");

        formPanel.add(addButton);

        add(
                formPanel,
                BorderLayout.NORTH
        );

        JButton refreshButton =
                new JButton("Refresh");

        JPanel bottomPanel =
                new JPanel(new FlowLayout(FlowLayout.RIGHT));

        bottomPanel.add(refreshButton);

        add(
                bottomPanel,
                BorderLayout.SOUTH
        );

        addButton.addActionListener(
                event -> addCustomer()
        );

        refreshButton.addActionListener(
                event -> loadCustomers()
        );

        loadCustomers();
    }

    private void addCustomer() {

        String fullName =
                nameField.getText().trim();

        String email =
                emailField.getText().trim();

        String phone =
                phoneField.getText().trim();

        if (fullName.isEmpty() || email.isEmpty()) {

            JOptionPane.showMessageDialog(
                    this,
                    "Name and email are required.",
                    "Validation Error",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        Customer customer = new Customer(
                fullName,
                email,
                phone,
                LocalDate.now()
        );

        try {

            userDAO.save(customer);

            clearFields();
            loadCustomers();

            JOptionPane.showMessageDialog(
                    this,
                    "Customer created successfully."
            );

        } catch (RuntimeException exception) {

            showError(exception);
        }
    }

    private void loadCustomers() {

        tableModel.setRowCount(0);

        for (Customer customer : userDAO.findAll()) {

            tableModel.addRow(new Object[]{
                    customer.getId(),
                    customer.getFullName(),
                    customer.getEmail(),
                    customer.getPhone(),
                    customer.getRegistrationDate()
            });
        }
    }

    private void clearFields() {
        nameField.setText("");
        emailField.setText("");
        phoneField.setText("");
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