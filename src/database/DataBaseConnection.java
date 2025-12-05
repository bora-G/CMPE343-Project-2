package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DataBaseConnection {
    public static final String DATABASE_NAME = "Group5";
    private static final String URL = "jdbc:mysql://localhost:3306/" + DATABASE_NAME + "?useSSL=false&allowPublicKeyRetrieval=true";

    private static final String USER = "group5";       
    private static final String PASSWORD = "12345678";  

    private DataBaseConnection() {}

    public static Connection getConnection() {
        try {
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (SQLException e) {
            System.out.println("Veritabanına bağlanılamadı!");
            e.printStackTrace();
            return null;
        }
    }
}
