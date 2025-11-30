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

public class UserRepository {

    private static final String BASE_SELECT =
            "SELECT user_id, username, password_hash, name, surname, role, created_at FROM users";

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

    public boolean delete(int userId) {
        try (Connection connection = requireConnection();
             PreparedStatement statement = connection.prepareStatement("DELETE FROM users WHERE user_id = ?")) {
            statement.setInt(1, userId);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete user " + userId, e);
        }
    }

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

    private Connection requireConnection() throws SQLException {
        Connection connection = DataBaseConnection.getConnection();
        if (connection == null) {
            throw new SQLException("Unable to obtain database connection");
        }
        return connection;
    }

    private static class BasicUser extends User {
        @Override
        public void showUserMenu() {
            // Intentionally empty, repository only needs a concrete subtype.
        }
    }
}
