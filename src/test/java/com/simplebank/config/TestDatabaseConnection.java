package com.simplebank.config;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public final class TestDatabaseConnection {

    private static final Properties properties = new Properties();

    static {
        try (FileInputStream input =
                     new FileInputStream("config-test.properties")) {

            properties.load(input);

        } catch (IOException exception) {
            throw new RuntimeException(
                    "Could not load test database configuration.",
                    exception
            );
        }
    }

    private TestDatabaseConnection() {
    }

    public static Connection getConnection()
            throws SQLException {

        return DriverManager.getConnection(
                properties.getProperty("db.url"),
                properties.getProperty("db.username"),
                properties.getProperty("db.password")
        );
    }
}