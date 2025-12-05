package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Provides a simple utility class for creating a connection to a MySQL database.
 * <p>
 * This class follows a static factory pattern: it cannot be instantiated and
 * instead exposes a single {@link #getConnection()} method that returns a
 * ready-to-use {@link Connection} object. Connection parameters such as URL,
 * username, and password are stored internally.
 * </p>
 *
 * <p><b>Usage example:</b></p>
 * <pre>{@code
 * Connection conn = DataBaseConnection.getConnection();
 * if (conn != null) {
 *     // Execute queries...
 * }
 * }</pre>
 *
 * <p>
 * The database is expected to be a MySQL instance running locally on port 3306.
 * SSL is disabled and public key retrieval is allowed for compatibility.
 * </p>
 */
public class DataBaseConnection {

      /** The name of the database schema to connect to. */
    public static final String DATABASE_NAME = "Group5";

    /** JDBC connection string including host, port, and schema name. */
    private static final String URL = "jdbc:mysql://localhost:3306/" + DATABASE_NAME + "?useSSL=false&allowPublicKeyRetrieval=true";

    /** MySQL username (default root for local setups). */
    private static final String USER = "root";    
    
    /** MySQL password for the given username. */
    private static final String PASSWORD = "1234";  

    /**
     * Private constructor to prevent instantiation.
     * This class is intended to be used statically only.
     */
    private DataBaseConnection() {}

    /**
     * Attempts to establish a connection to the MySQL database using the
     * predefined URL, username, and password.
     *
     * @return a valid {@link Connection} object if successful,
     *         or {@code null} if the connection attempt fails
     */
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

