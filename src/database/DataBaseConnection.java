package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DataBaseConnection {

    private static final String URL =
            "jdbc:mysql://localhost:3306/Group16?useUnicode=true&characterEncoding=UTF-8&useSSL=false";

    private static final String USER = "root";       
    private static final String PASSWORD = "1234";  

    private DataBaseConnection() {}

    public static Connection getConnection() {
        try {
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (SQLException e) {
            System.out.println("❌ Database connection failed!");
            e.printStackTrace();
            return null;
        }
    }
}
