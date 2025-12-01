package com.cmpe343.project2.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DbConnection {

    private static final String URL = "jdbc:mysql://localhost:3306/cmpe343";
    // Change USER and PASSWORD according to your own MySQL setup
    private static final String USER = "myuser@localhost";
    private static final String PASSWORD = "1234";

    private static Connection instance;

    private DbConnection() {
    }

    public static Connection getConnection() throws SQLException {
        if (instance == null || instance.isClosed()) {
            instance = DriverManager.getConnection(URL, USER, PASSWORD);
        }
        return instance;
    }
}
