package com.example.project_phase_two;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Database_connection {
    private static final String URL = "jdbc:mysql://localhost:3306/student_registration_system";
    private static final String USER = "root";
    private static final String PASSWORD = ""; // If you set a MySQL password, put it here

    public static Connection getConnection() {
        try {
            Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("✅ Connected to MySQL database.");
            return conn;
        } catch (SQLException e) {
            System.out.println("❌ Connection failed: " + e.getMessage());
            return null;
        }
    }
}
