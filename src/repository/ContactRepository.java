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
import java.util.Map; 

/**
 * Repository class responsible for performing CRUD and search operations
 * on the {@code contacts} table.
 * <p>
 * This class encapsulates all database interaction for {@link Contact}
 * entities: loading, inserting, updating, deleting and searching by
 * various criteria.
 * </p>
 */
public class ContactRepository {

    /**
     * Base SELECT statement used by several query methods to avoid repetition.
     */
    private static final String BASE_SELECT = "SELECT contact_id, first_name, middle_name, last_name, nickname, " +
            "phone_primary, phone_secondary, email, linkedin_url, birth_date, created_at, updated_at " +
            "FROM contacts";

     /**
     * Retrieves all contacts from the database ordered by {@code contact_id}.
     * @author Bora
     * @return list of all contacts, never {@code null}
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
     * Finds a single contact by its unique identifier.
     * @author Melek
     * @param contactId the primary key of the contact
     * @return the matching {@link Contact}, or {@code null} if not found
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
     * Performs an exact (case-sensitive, binary collation) search on a given field.
     * <p>
     * For example, this can be used to find an exact email or phone number match.
     * </p>
     * @author Can
     * @param field the column name to search on (must be a valid contacts column)
     * @param value the exact value to match
     * @return list of contacts where the given field exactly equals the given value
     */
public List<Contact> searchByFieldExact(String field, String value) {
    String sql =
        "SELECT contact_id, first_name, middle_name, last_name, nickname, " +
        "phone_primary, phone_secondary, email, linkedin_url, birth_date, created_at, updated_at " +
        "FROM contacts WHERE LOWER(" + field + ") = LOWER(?)";

    List<Contact> results = new ArrayList<>();

    try (Connection connection = requireConnection();
         PreparedStatement st = connection.prepareStatement(sql)) {

        st.setString(1, value);

        try (ResultSet rs = st.executeQuery()) {
            while (rs.next()) {
                results.add(mapRow(rs));
            }
        }
    } catch (SQLException e) {
        throw new RuntimeException("Exact search failed for field: " + field, e);
    }

    return results;
}


    /**
     * Inserts a new contact into the database.
     * <p>
     * If the insertion succeeds, the generated {@code contact_id} is
     * set on the given {@link Contact} instance.
     * </p>
     * @author Mikail
     * @param contact the contact to insert
     * @return {@code true} if at least one row was inserted, otherwise {@code false}
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
     * Updates an existing contact in the database.
     *
     * @param contact the contact containing updated data; its {@code contactId}
     *                must be set
     * @author Mikail
     * @return {@code true} if at least one row was updated, otherwise {@code false}
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
     * Deletes a contact by its identifier.
     * @author Melek
     * @param contactId the id of the contact to delete
     * @return {@code true} if a row was deleted, otherwise {@code false}
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
     * Performs a LIKE-based search on a specified field using binary collation.
     * <p>
     * This method is useful for partial matches, for example searching
     * for contacts whose first name contains a substring.
     * </p>
     * @author Can
     * @param field the column name to search on (must be a valid contacts column)
     * @param value the value used in the LIKE expression (wrapped with {@code %})
     * @return list of matching contacts
     */
public List<Contact> searchByField(String field, String value) {

    String sql =
        "SELECT contact_id, first_name, middle_name, last_name, nickname, " +
        "phone_primary, phone_secondary, email, linkedin_url, birth_date, created_at, updated_at " +
        "FROM contacts " +
        "WHERE BINARY LOWER(" + field + ") LIKE BINARY LOWER(?)";

    List<Contact> results = new ArrayList<>();

    try (Connection connection = requireConnection();
         PreparedStatement st = connection.prepareStatement(sql)) {

        // contains arama: %value%
        st.setString(1, "%" + value + "%");

        try (ResultSet rs = st.executeQuery()) {
            while (rs.next()) {
                results.add(mapRow(rs));
            }
        }
    } catch (SQLException e) {
        throw new RuntimeException("Search failed for field: " + field, e);
    }

    return results;
}

    /**
     * Searches contacts based on whether they have a LinkedIn URL or not.
     *
     * @param hasLinkedin if {@code true}, returns only contacts with a non-empty
     *                    LinkedIn URL; if {@code false}, returns contacts with no
     *                    LinkedIn URL
     * @author Mikail
     * @return list of contacts matching the LinkedIn presence condition
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
     * Performs a multi-field search using a map of criteria.
     * <p>
     * Each entry in the map represents:
     * <ul>
     *     <li>key   → column name</li>
     *     <li>value → search text, used in a {@code LIKE '%value%'} clause</li>
     * </ul>
     * All criteria are combined with {@code AND}.
     * </p>
     * @author Can
     * @param criteria map of field names to search values
     * @return list of contacts matching all criteria; empty list if criteria is
     *         {@code null} or empty
     */
public List<Contact> searchByMultipleCriteria(Map<String, String> criteria) {
    if (criteria == null || criteria.isEmpty()) {
        return new ArrayList<>();
    }

    StringBuilder sqlBuilder = new StringBuilder(
        "SELECT contact_id, first_name, middle_name, last_name, nickname, " +
        "phone_primary, phone_secondary, email, linkedin_url, birth_date, created_at, updated_at " +
        "FROM contacts WHERE 1=1"
    );

    List<Object> values = new ArrayList<>();

    for (Map.Entry<String, String> entry : criteria.entrySet()) {
        sqlBuilder.append(" AND BINARY LOWER(")
                  .append(entry.getKey())
                  .append(") LIKE BINARY LOWER(?)");
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
     * Returns all contacts sorted by a given column and direction.
     * <p>
     * Only a predefined list of allowed sort fields is accepted to avoid
     * SQL injection. If an invalid field is provided, sorting falls back
     * to {@code first_name}. Direction defaults to {@code ASC} unless
     * {@code DESC} is explicitly requested.
     * </p>
     * @author Can
     * @param sortField     column name to sort by
     * @param sortDirection {@code "ASC"} or {@code "DESC"} (case-insensitive)
     * @return sorted list of contacts
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
     * Ensures a non-null database connection is obtained.
     * @author Bora
     * @return an active {@link Connection}
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
     * Binds the common contact fields to the given prepared statement.
     * @author Melek
     * @param statement the statement to bind parameters to
     * @param contact   the contact whose fields will be bound
     * @return the next parameter index after the last bound field
     * @throws SQLException if a JDBC error occurs
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
     * Sets a nullable string parameter on a prepared statement.
     * @author Mikail
     * @param statement      the statement
     * @param parameterIndex the parameter index (1-based)
     * @param value          the string value or {@code null}
     * @throws SQLException if a JDBC error occurs
     */
    private void setNullableString(PreparedStatement statement, int parameterIndex, String value) throws SQLException {
        if (value == null) {
            statement.setNull(parameterIndex, Types.VARCHAR);
        } else {
            statement.setString(parameterIndex, value);
        }
    }

      /**
     * Sets a nullable date parameter on a prepared statement.
     *
     * @param statement      the statement
     * @param parameterIndex the parameter index (1-based)
     * @param value          the date value or {@code null}
     * @throws SQLException if a JDBC error occurs
       * @author Mikail
     */
    private void setNullableDate(PreparedStatement statement, int parameterIndex, Date value) throws SQLException {
        if (value == null) {
            statement.setNull(parameterIndex, Types.DATE);
        } else {
            statement.setDate(parameterIndex, value);
        }
    }

     /**
     * Maps the current row of a {@link ResultSet} to a {@link Contact} object.
     *
     * @param resultSet the result set positioned at a valid row
     * @return a populated {@link Contact} instance
      * @author Melek
     * @throws SQLException if a JDBC error occurs while reading columns
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
