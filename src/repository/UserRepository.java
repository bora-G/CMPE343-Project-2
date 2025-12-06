package repository;

import database.DataBaseConnection;
import models.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Repository class responsible for database operations on the {@code users} table.
 * <p>
 * Provides methods to:
 * <ul>
 *     <li>Find users by username</li>
 *     <li>Check if a username exists</li>
 *     <li>Insert, update and delete users</li>
 *     <li>Update user passwords</li>
 *     <li>Load all users</li>
 * </ul>
 * All low-level JDBC logic is encapsulated here so that higher layers
 * can work with {@link User} objects.
 * </p>
 */
public class UserRepository {

    /**
     * Base SELECT clause used for most user queries.
     */
    private static final String BASE_SELECT =
            "SELECT user_id, username, password_hash, name, surname, role, salary, created_at FROM users";

    /**
     * Finds a user by its unique username (binary comparison).
     * <p>
     * Uses {@code BINARY username = ?} so the lookup is case-sensitive.
     * </p>
     * @author Mikail
     * @param username the username to search for
     * @return a {@link User} if found, otherwise {@code null}
     */
    public User findByUsername(String username) {
        try (Connection connection = requireConnection();
             PreparedStatement statement =
                     connection.prepareStatement(BASE_SELECT + " WHERE BINARY username = ?")) {
            statement.setString(1, username);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapRow(resultSet);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to load user with username " + username, e);
        }
        return null;
    }

/**
     * Checks whether a user exists with the given username.
     *
     * @param username the username to check
     * @return {@code true} if a user with this username exists, otherwise {@code false}
     */
    public boolean existsByUsername(String username) {
        return findByUsername(username) != null;
    }

    /**
     * Updates the password hash of a user.
     *@author Bora
     * @param userId          the id of the user whose password will be updated
     * @param newPasswordHash the new password hash value
     * @return {@code true} if at least one row was updated, otherwise {@code false}
     */
    public boolean updatePassword(int userId, String newPasswordHash) {
        String sql = "UPDATE users SET password_hash = ? WHERE user_id = ?";
        try (Connection connection = requireConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, newPasswordHash);
            statement.setInt(2, userId);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update password for user " + userId, e);
        }
    }

    /**
     * Retrieves all users from the database ordered by {@code user_id}.
     *@author Melek
     * @return a list of all users, never {@code null}
     */
    public List<User> findAll() {
        List<User> users = new ArrayList<>();
        try (Connection connection = requireConnection();
             PreparedStatement statement =
                     connection.prepareStatement(BASE_SELECT + " ORDER BY user_id");
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                users.add(mapRow(resultSet));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to load users", e);
        }
        return users;
    }

     /**
     * Inserts a new user into the {@code users} table.
     * <p>
     * On successful insert, the generated {@code user_id} is set
     * on the given {@link User} instance.
     * </p>
     * @author Can
     * @param user the user to insert
     * @return {@code true} if at least one row was inserted, otherwise {@code false}
     */
    public boolean insert(User user) {
        String sql = "INSERT INTO users (username, password_hash, name, surname, role) VALUES (?, ?, ?, ?, ?)";
        try (Connection connection = requireConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, user.getUsername());
            statement.setString(2, user.getPassword_hash());
            statement.setString(3, user.getName());
            statement.setString(4, user.getSurname());
            statement.setString(5, user.getRole());
            int affected = statement.executeUpdate();
            if (affected > 0) {
                try (ResultSet keys = statement.getGeneratedKeys()) {
                    if (keys.next()) {
                        user.setUserId(keys.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to insert user " + user.getUsername(), e);
        }
        return false;
    }

      /**
     * Deletes a user with the given id.
     * @author Mikail
     * @param userId the id of the user to delete
     * @return {@code true} if a row was deleted, otherwise {@code false}
     */
    public boolean delete(int userId) {
        try (Connection connection = requireConnection();
             PreparedStatement statement = connection.prepareStatement("DELETE FROM users WHERE user_id = ?")) {
            statement.setInt(1, userId);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete user " + userId, e);
        }
    }

     /**
     * Maps the current row of the given {@link ResultSet} to a concrete {@link User} instance.
     * <p>
     * Internally this method uses an inner {@link BasicUser} class extending {@link User},
     * because {@code User} itself is likely abstract or has abstract behavior
     * (e.g. {@link User#showUserMenu()}).
     * </p>
     * @author Melek
     * @param resultSet the result set positioned at a valid row
     * @return a populated {@link User} instance
     * @throws SQLException if reading from the result set fails
     */
    private User mapRow(ResultSet resultSet) throws SQLException {
        BasicUser user = new BasicUser();
        user.setUserId(resultSet.getInt("user_id"));
        user.setUsername(resultSet.getString("username"));
        user.setPassword_hash(resultSet.getString("password_hash"));
        user.setName(resultSet.getString("name"));
        user.setSurname(resultSet.getString("surname"));
        user.setRole(resultSet.getString("role"));
        user.setCreated_at(resultSet.getDate("created_at"));

        double salaryValue = resultSet.getDouble("salary");
        if (resultSet.wasNull()) {
            user.setSalary(null);        // User.salary tipi büyük ihtimalle Double
        } else {
            user.setSalary(salaryValue);
        }

        user.setCreated_at(resultSet.getDate("created_at"));
        return user;
    }

     /**
     * Ensures that a valid database connection is obtained.
     * @author Mikail
     * @return an open {@link Connection}
     * @throws SQLException if the connection cannot be created
     */
    private Connection requireConnection() throws SQLException {
        Connection connection = DataBaseConnection.getConnection();
        if (connection == null) {
            throw new SQLException("Unable to obtain database connection");
        }
        return connection;
    }

      /**
     * Updates all editable fields of a user, including salary.
     * @author Can
     * @param user the user containing updated values; its {@code userId} must be set
     * @return {@code true} if at least one row was updated, otherwise {@code false}
     */
    public boolean update(User user) {
        String sql = "UPDATE users SET username = ?, password_hash = ?, name = ?, surname = ?, role = ?, salary = ? WHERE user_id = ?";

        try (Connection conn = requireConnection();
             PreparedStatement st = conn.prepareStatement(sql)) {

            st.setString(1, user.getUsername());
            st.setString(2, user.getPassword_hash());
            st.setString(3, user.getName());
            st.setString(4, user.getSurname());
            st.setString(5, user.getRole());

            if (user.getSalary() != null) {
                st.setDouble(6, user.getSalary());
            } else {
                st.setNull(6, java.sql.Types.DOUBLE);
            }

            st.setInt(7, user.getUserId());

            return st.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new RuntimeException("Failed to update user " + user.getUserId(), e);
        }
    }

    
    /**
     * Simple concrete implementation of {@link User} used only by this repository.
     * <p>
     * The only purpose is to provide a non-abstract type so rows from the database
     * can be mapped into usable objects, without implementing any real menu logic.
     * @author Mikail
     * </p>
     */
    private static class BasicUser extends User {
        @Override
        public void showUserMenu() {

        }
    }
}
