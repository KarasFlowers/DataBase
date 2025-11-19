package com.campusdelivery.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnector {
    // IMPORTANT: Replace with your actual database URL, username, and password.
    // The GaussDB(for MySQL) connection string might be different.
    // Example for MySQL: "jdbc:mysql://<host>:<port>/<database>"
    private static final String URL = "jdbc:mysql://localhost:3306/campus_delivery";
    private static final String USER = "your_username"; // TODO: Replace
    private static final String PASSWORD = "your_password"; // TODO: Replace

    /**
     * Establishes a connection to the database.
     * @return A Connection object.
     * @throws SQLException if a database access error occurs.
     */
    public static Connection getConnection() throws SQLException {
        try {
            // This explicitly loads the MySQL driver.
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("Error: MySQL JDBC Driver not found.");
            throw new SQLException("MySQL JDBC Driver not found.", e);
        }
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    /**
     * A simple main method to test the database connection.
     */
    public static void main(String[] args) {
        try (Connection connection = getConnection()) {
            if (connection != null && !connection.isClosed()) {
                System.out.println("Successfully connected to the database!");
            } else {
                System.out.println("Failed to connect to the database.");
            }
        } catch (SQLException e) {
            System.err.println("Database connection failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
