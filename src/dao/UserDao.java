package com.cmpe343.project2.dao;

import com.cmpe343.project2.db.DbConnection;
import com.cmpe343.project2.model.Role;
import com.cmpe343.project2.model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDao {

    public User findByUsername(String username) throws SQLException {
        String sql = "SELECT user_id, username, password_hash, name, surname, role " +
                     "FROM users WHERE username = ?";

        try (Connection conn = DbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }

                User user = new User();
                user.setUserId(rs.getInt("user_id"));
                user.setUsername(rs.getString("username"));
                user.setPasswordHash(rs.getString("password_hash"));
                user.setName(rs.getString("name"));
                user.setSurname(rs.getString("surname"));
                user.setRole(Role.fromString(rs.getString("role")));
                return user;
            }
        }
    }

    public void updatePasswordHash(int userId, String newPasswordHash) throws SQLException {
        String sql = "UPDATE users SET password_hash = ? WHERE user_id = ?";

        try (Connection conn = DbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, newPasswordHash);
            ps.setInt(2, userId);
            ps.executeUpdate();
        }
    }
}
