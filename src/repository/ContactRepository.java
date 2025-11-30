package repository;

import database.DataBaseConnection;
import models.Contact;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

public class ContactRepository {

    private static final String BASE_SELECT = "SELECT contact_id, first_name, middle_name, last_name, nickname, " +
            "phone_primary, phone_secondary, email, linkedin_url, birth_date, created_at, updated_at " +
            "FROM contacts";

    public List<Contact> findAll() {
        List<Contact> contacts = new ArrayList<>();
        try (Connection connection = requireConnection();
                PreparedStatement statement = connection.prepareStatement(BASE_SELECT + " ORDER BY contact_id");
                ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                contacts.add(mapRow(resultSet));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to load contacts", e);
        }
        return contacts;
    }

    public Contact findById(int contactId) {
        try (Connection connection = requireConnection();
                PreparedStatement statement = connection.prepareStatement(BASE_SELECT + " WHERE contact_id = ?")) {
            statement.setInt(1, contactId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapRow(resultSet);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to load contact with id " + contactId, e);
        }
        return null;
    }

    public List<Contact> searchByField(String field, String value) {
        String sql = "SELECT * FROM contacts WHERE " + field + " LIKE ?";
        List<Contact> results = new ArrayList<>();

        try (Connection connection = requireConnection();
                PreparedStatement st = connection.prepareStatement(sql)) {

            st.setString(1, "%" + value + "%"); 
            ResultSet rs = st.executeQuery();

            while (rs.next()) {
                results.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Search failed for field: " + field, e);
        }

        return results;
    }

    public boolean insert(Contact contact) {
        String sql = "INSERT INTO contacts " +
                "(first_name, middle_name, last_name, nickname, phone_primary, phone_secondary, email, linkedin_url, birth_date) "
                +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection connection = requireConnection();
                PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            bindContact(statement, contact);
            int affected = statement.executeUpdate();
            if (affected > 0) {
                try (ResultSet keys = statement.getGeneratedKeys()) {
                    if (keys.next()) {
                        contact.setContactId(keys.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to insert contact", e);
        }
        return false;
    }

    public boolean update(Contact contact) {
        String sql = "UPDATE contacts SET first_name = ?, middle_name = ?, last_name = ?, nickname = ?, " +
                "phone_primary = ?, phone_secondary = ?, email = ?, linkedin_url = ?, birth_date = ? " +
                "WHERE contact_id = ?";
        try (Connection connection = requireConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            int index = bindContact(statement, contact);
            statement.setInt(index, contact.getContactId());
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update contact " + contact.getContactId(), e);
        }
    }

    public boolean delete(int contactId) {
        try (Connection connection = requireConnection();
                PreparedStatement statement = connection
                        .prepareStatement("DELETE FROM contacts WHERE contact_id = ?")) {
            statement.setInt(1, contactId);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete contact " + contactId, e);
        }
    }

    private int bindContact(PreparedStatement statement, Contact contact) throws SQLException {
        int index = 1;
        statement.setString(index++, contact.getFirstName());
        setNullableString(statement, index++, contact.getMiddleName());
        statement.setString(index++, contact.getLastName());
        setNullableString(statement, index++, contact.getNickname());
        statement.setString(index++, contact.getPhonePrimary());
        setNullableString(statement, index++, contact.getPhoneSecondary());
        setNullableString(statement, index++, contact.getEmail());
        setNullableString(statement, index++, contact.getLinkedinUrl());
        setNullableDate(statement, index++, contact.getBirthDate());
        return index;
    }

    private void setNullableString(PreparedStatement statement, int parameterIndex, String value) throws SQLException {
        if (value == null) {
            statement.setNull(parameterIndex, Types.VARCHAR);
        } else {
            statement.setString(parameterIndex, value);
        }
    }

    private void setNullableDate(PreparedStatement statement, int parameterIndex, Date value) throws SQLException {
        if (value == null) {
            statement.setNull(parameterIndex, Types.DATE);
        } else {
            statement.setDate(parameterIndex, value);
        }
    }

    private Contact mapRow(ResultSet resultSet) throws SQLException {
        Contact contact = new Contact();
        contact.setContactId(resultSet.getInt("contact_id"));
        contact.setFirstName(resultSet.getString("first_name"));
        contact.setMiddleName(resultSet.getString("middle_name"));
        contact.setLastName(resultSet.getString("last_name"));
        contact.setNickname(resultSet.getString("nickname"));
        contact.setPhonePrimary(resultSet.getString("phone_primary"));
        contact.setPhoneSecondary(resultSet.getString("phone_secondary"));
        contact.setEmail(resultSet.getString("email"));
        contact.setLinkedinUrl(resultSet.getString("linkedin_url"));
        contact.setBirthDate(resultSet.getDate("birth_date"));
        contact.setCreatedAt(resultSet.getTimestamp("created_at"));
        contact.setUpdatedAt(resultSet.getTimestamp("updated_at"));
        return contact;
    }

    public List<Contact> searchByLinkedinPresence(boolean hasLinkedin) {
    String sql = BASE_SELECT;

    if (hasLinkedin) {
        
        sql += " WHERE linkedin_url IS NOT NULL AND linkedin_url <> ''";
    } else {
        
        sql += " WHERE linkedin_url IS NULL OR linkedin_url = ''";
    }

    List<Contact> results = new ArrayList<>();

    try (Connection connection = requireConnection();
         PreparedStatement st = connection.prepareStatement(sql);
         ResultSet rs = st.executeQuery()) {

        while (rs.next()) {
            results.add(mapRow(rs));
        }

    } catch (SQLException e) {
        throw new RuntimeException("Failed to search by LinkedIn presence", e);
    }

    return results;
}
    private Connection requireConnection() throws SQLException {
        Connection connection = DataBaseConnection.getConnection();
        if (connection == null) {
            throw new SQLException("Unable to obtain database connection");
        }
        return connection;
    }
}
