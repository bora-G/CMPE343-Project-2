package repository;

import database.DataBaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import models.User;

/**
 * Handles all database interactions for the {@link User} entity.
 * <p>
 * This class implements the Data Access Object (DAO) pattern to abstract low-level
 * JDBC calls. It manages the "users" table, which contains authentication credentials
 * and role information.
 * </p>
 * <p>
 * Key responsibilities include:
 * <ul>
 * <li>Authenticating users by retrieving credentials by username.</li>
 * <li>Managing password updates (storing only encrypted hashes).</li>
 * <li>Handling CRUD operations (Create, Read, Update, Delete) for user management by Managers.</li>
 * </ul>
 * </p>
 *
 * @author [Group Members Names Here]
 * @version 1.0
 * @see models.User
 * @see database.DataBaseConnection
 */
public class UserRepository {

    /**
     * Base SQL query for selecting user fields.
     */
    private static final String BASE_SELECT =
            "SELECT user_id, username, password_hash, name, surname, role, created_at FROM users";

    /**
     * Finds a user by their username.
     * <p>
     * Used primarily during the login process to retrieve the stored password hash
     * and user role.
     * </p>
     *
     * @param username The username to search for.
     * @return A {@link User} object if found, or {@code null} if no user exists with that username.
     * @throws RuntimeException If a database access error occurs.
     */
    public User findByUsername(String username) {
        try (Connection connection = requireConnection();
             PreparedStatement statement =
                     connection.prepareStatement(BASE_SELECT + " WHERE username = ?")) {
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
     * Updates the password hash for a specific user.
     * <p>
     * <b>Note:</b> The password must be hashed <i>before</i> calling this method
     * to ensure security compliance.
     * </p>
     *
     * @param userId          The unique ID of the user.
     * @param newPasswordHash The new encrypted password string.
     * @return {@code true} if the password was updated successfully; {@code false} otherwise.
     * @throws RuntimeException If a database access error occurs.
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
     * Retrieves all users currently registered in the database.
     *
     * @return A list of all {@link User} objects, sorted by user ID.
     * @throws RuntimeException If a database access error occurs.
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
     * Inserts a new user record into the database.
     * <p>
     * Retrieves the auto-generated primary key (user_id) and assigns it to the
     * passed {@code user} object upon success.
     * </p>
     *
     * @param user The user object containing the details (username, hash, name, surname, role) to insert.
     * @return {@code true} if the insertion was successful; {@code false} otherwise.
     * @throws RuntimeException If a database access error occurs.
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
     * Deletes a user record from the database.
     *
     * @param userId The unique ID of the user to delete.
     * @return {@code true} if the deletion was successful; {@code false} otherwise.
     * @throws RuntimeException If a database access error occurs.
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
     * Maps a row from the ResultSet to a User object.
     * <p>
     * Uses a concrete {@link BasicUser} implementation to instantiate the object,
     * as the specific role subclass might not be determined at this low level.
     * </p>
     *
     * @param resultSet The SQL result set positioned at the current row.
     * @return A populated User object.
     * @throws SQLException If a database access error occurs.
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
        return user;
    }

    /**
     * Helper method to obtain a database connection.
     *
     * @return A valid {@link Connection} object.
     * @throws SQLException If the connection cannot be established.
     */
    private Connection requireConnection() throws SQLException {
        Connection connection = DataBaseConnection.getConnection();
        if (connection == null) {
            throw new SQLException("Unable to obtain database connection");
        }
        return connection;
    }

    /**
     * Updates an existing user's details (Username, Name, Surname, Role, etc.).
     *
     * @param user The user object containing updated information.
     * @return {@code true} if the update was successful; {@code false} otherwise.
     * @throws RuntimeException If a database access error occurs.
     */
    public boolean update(User user) {
        String sql = "UPDATE users SET username = ?, password_hash = ?, name = ?, surname = ?, role = ? WHERE user_id = ?";

        try (Connection conn = requireConnection();
             PreparedStatement st = conn.prepareStatement(sql)) {

            st.setString(1, user.getUsername());
            st.setString(2, user.getPassword_hash());
            st.setString(3, user.getName());
            st.setString(4, user.getSurname());
            st.setString(5, user.getRole());
            st.setInt(6, user.getUserId());

            return st.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new RuntimeException("Failed to update user " + user.getUserId(), e);
        }
    }

    /**
     * A concrete implementation of the abstract User class used for data mapping
     * within the repository.
     */
    private static class BasicUser extends User {
        @Override
        public void showUserMenu() {
            // No-op for data transfer object
        }
    }
}