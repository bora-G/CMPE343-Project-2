package repository;

import database.DataBaseConnection;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import models.Contact;

/**
 * Handles all database interactions for the {@link Contact} entity.
 * <p>
 * This class implements the Data Access Object (DAO) pattern to abstract the low-level
 * JDBC calls from the business logic. It provides methods for:
 * <ul>
 * <li>CRUD operations (Create, Read, Update, Delete) on the contacts table.</li>
 * <li>Flexible searching mechanism supporting both single-field and multi-field queries using wildcards (LIKE).</li>
 * <li>Sorting contacts dynamically by field and direction (ASC/DESC).</li>
 * </ul>
 * </p>
 *
 * @author [Group Members Names Here]
 * @version 1.0
 * @see models.Contact
 * @see database.DataBaseConnection
 */
public class ContactRepository {

    /**
     * Base SQL query for selecting all columns from the contacts table.
     */
    private static final String BASE_SELECT = "SELECT contact_id, first_name, middle_name, last_name, nickname, " +
            "phone_primary, phone_secondary, email, linkedin_url, birth_date, created_at, updated_at " +
            "FROM contacts";

    /**
     * Retrieves all contacts from the database.
     *
     * @return A list of all {@link Contact} objects, sorted by ID by default.
     * @throws RuntimeException If a database access error occurs.
     */
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

    /**
     * Finds a specific contact by their unique ID.
     *
     * @param contactId The ID of the contact to retrieve.
     * @return The {@link Contact} object if found, or {@code null} otherwise.
     * @throws RuntimeException If a database access error occurs.
     */
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

    /**
     * Inserts a new contact record into the database.
     * <p>
     * Retrieves the auto-generated primary key (contact_id) and sets it back to the contact object.
     * </p>
     *
     * @param contact The contact object to be inserted.
     * @return {@code true} if the insertion was successful; {@code false} otherwise.
     * @throws RuntimeException If a database access error occurs.
     */
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

    /**
     * Updates an existing contact record in the database.
     *
     * @param contact The contact object containing updated data.
     * @return {@code true} if the update was successful (one row affected); {@code false} otherwise.
     * @throws RuntimeException If a database access error occurs.
     */
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

    /**
     * Deletes a contact record from the database by ID.
     *
     * @param contactId The ID of the contact to delete.
     * @return {@code true} if the deletion was successful; {@code false} otherwise.
     * @throws RuntimeException If a database access error occurs.
     */
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

    /**
     * Searches for contacts where a specific field matches a value (partial match).
     *
     * @param field The database column name to search (e.g., "first_name").
     * @param value The value or substring to search for.
     * @return A list of matching contacts.
     * @throws RuntimeException If a database access error occurs.
     */
    public List<Contact> searchByField(String field, String value) {
        // Warning: Direct string concatenation for column names poses a SQL injection risk if not validated.
        // Assuming 'field' comes from a safe, controlled source (menu selection) as per Service logic.
        String sql = "SELECT * FROM contacts WHERE " + field + " COLLATE utf8mb4_bin LIKE ?";
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

    /**
     * Filters contacts based on the presence of a LinkedIn URL.
     *
     * @param hasLinkedin If {@code true}, returns contacts with a LinkedIn URL; otherwise, returns those without.
     * @return A list of matching contacts.
     * @throws RuntimeException If a database access error occurs.
     */
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

    /**
     * Searches for contacts matching multiple criteria simultaneously (AND logic).
     * <p>
     * Dynamically builds the SQL query based on the provided map of field-value pairs.
     * Supports partial matches (substrings) for all fields.
     * </p>
     *
     * @param criteria A map where keys are database column names and values are the search terms.
     * @return A list of contacts matching ALL criteria.
     * @throws RuntimeException If a database access error occurs.
     */
    public List<Contact> searchByMultipleCriteria(Map<String, String> criteria) {
        if (criteria == null || criteria.isEmpty()) {
            return new ArrayList<>();
        }

        StringBuilder sqlBuilder = new StringBuilder("SELECT * FROM contacts WHERE 1=1");
        List<Object> values = new ArrayList<>();

        for (Map.Entry<String, String> entry : criteria.entrySet()) {
            sqlBuilder.append(" AND ").append(entry.getKey()).append(" LIKE ?");
            values.add("%" + entry.getValue() + "%");
        }

        List<Contact> results = new ArrayList<>();
        try (Connection connection = requireConnection();
             PreparedStatement st = connection.prepareStatement(sqlBuilder.toString())) {

            for (int i = 0; i < values.size(); i++) {
                st.setObject(i + 1, values.get(i));
            }

            try (ResultSet rs = st.executeQuery()) {
                while (rs.next()) {
                    results.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Multi-field search failed", e);
        }
        return results;
    }

    /**
     * Retrieves all contacts sorted by a specific field and direction.
     *
     * @param sortField     The database column name to sort by (validated against an allowed list).
     * @param sortDirection The direction of sort ("ASC" or "DESC").
     * @return A list of sorted contacts.
     * @throws RuntimeException If a database access error occurs.
     */
    public List<Contact> findAllSorted(String sortField, String sortDirection) {
        List<String> allowedFields = List.of(
            "contact_id", "first_name", "middle_name", "last_name", "nickname",
            "phone_primary", "phone_secondary", "email", "linkedin_url",
            "birth_date", "created_at", "updated_at"
        );

        if (!allowedFields.contains(sortField)) {
            sortField = "first_name";
        }

        if (!"DESC".equalsIgnoreCase(sortDirection)) {
            sortDirection = "ASC";
        }

        String sql = BASE_SELECT + " ORDER BY " + sortField + " " + sortDirection;

        List<Contact> contacts = new ArrayList<>();
        try (Connection connection = requireConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                contacts.add(mapRow(resultSet));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to load sorted contacts", e);
        }
        return contacts;
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
     * Binds contact object fields to a PreparedStatement.
     *
     * @param statement The PreparedStatement to populate.
     * @param contact   The contact object containing the data.
     * @return The next available parameter index.
     * @throws SQLException If a database access error occurs.
     */
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

    /**
     * Sets a string parameter in a PreparedStatement, handling null values correctly.
     */
    private void setNullableString(PreparedStatement statement, int parameterIndex, String value) throws SQLException {
        if (value == null) {
            statement.setNull(parameterIndex, Types.VARCHAR);
        } else {
            statement.setString(parameterIndex, value);
        }
    }

    /**
     * Sets a date parameter in a PreparedStatement, handling null values correctly.
     */
    private void setNullableDate(PreparedStatement statement, int parameterIndex, Date value) throws SQLException {
        if (value == null) {
            statement.setNull(parameterIndex, Types.DATE);
        } else {
            statement.setDate(parameterIndex, value);
        }
    }

    /**
     * Maps a row from the ResultSet to a Contact object.
     */
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
}