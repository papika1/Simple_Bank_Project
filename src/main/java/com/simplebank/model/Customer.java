package com.simplebank.model;

import java.time.LocalDate;

public class Customer {

    private int id;
    private String fullName;
    private String email;
    private String phone;
    private LocalDate registrationDate;

    public Customer() {
    }

    public Customer(
            String fullName,
            String email,
            String phone,
            LocalDate registrationDate
    ) {
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.registrationDate = registrationDate;
    }

    public Customer(
            int id,
            String fullName,
            String email,
            String phone,
            LocalDate registrationDate
    ) {
        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.registrationDate = registrationDate;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public LocalDate getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(LocalDate registrationDate) {
        this.registrationDate = registrationDate;
    }

    @Override
    public String toString() {
        return "Customer{" +
                "id=" + id +
                ", fullName='" + fullName + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", registrationDate=" + registrationDate +
                '}';
    }
}
