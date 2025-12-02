package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DataBaseConnection {

    private static final String URL =
            "jdbc:mysql://localhost:3306/Group5" +
    "?useUnicode=true&characterEncoding=UTF-8&serverTimezone=UTC";

    private static final String USER = "root";       // kendi MySQL root kullanıcı adın
    private static final String PASSWORD = "1234";   // kendi şifren

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
