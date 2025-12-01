package com.cmpe343.project2.dao;

import com.cmpe343.project2.db.DbConnection;
import com.cmpe343.project2.model.Contact;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ContactDao {

    private static final List<String> SORTABLE_FIELDS = List.of(
            "first_name", "last_name", "phone_primary", "email", "birth_date", "contact_id"
    );

    private static String resolveSortField(String requestedField) {
        if (requestedField == null) {
            return "contact_id";
        }
        String lower = requestedField.toLowerCase();
        for (String f : SORTABLE_FIELDS) {
            if (f.equalsIgnoreCase(lower)) {
                return f;
            }
        }
        return "contact_id";
    }

    private static String resolveSortDirection(String dir) {
        if (dir == null) return "ASC";
        if (dir.equalsIgnoreCase("DESC")) return "DESC";
        return "ASC";
    }

    public List<Contact> listAll(String sortField, String sortDirection) throws SQLException {
        String sortColumn = resolveSortField(sortField);
        String direction = resolveSortDirection(sortDirection);

        String sql = "SELECT contact_id, first_name, middle_name, last_name, nickname, " +
                "phone_primary, phone_secondary, email, linkedin_url, birth_date " +
                "FROM contacts ORDER BY " + sortColumn + " " + direction;

        try (Connection conn = DbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            List<Contact> list = new ArrayList<>();
            while (rs.next()) {
                list.add(mapRow(rs));
            }
            return list;
        }
    }

    /**
     * criteria:
     *   key   = column name (first_name, last_name, phone_primary, email, ...)
     *   value = substring to search (LIKE %value%)
     */
    public List<Contact> searchByFields(Map<String, String> criteria,
                                        String sortField,
                                        String sortDirection) throws SQLException {

        String sortColumn = resolveSortField(sortField);
        String direction = resolveSortDirection(sortDirection);

        StringBuilder sql = new StringBuilder(
                "SELECT contact_id, first_name, middle_name, last_name, nickname, " +
                        "phone_primary, phone_secondary, email, linkedin_url, birth_date " +
                        "FROM contacts "
        );

        List<String> whereParts = new ArrayList<>();
        List<String> values = new ArrayList<>();

        for (Map.Entry<String, String> entry : criteria.entrySet()) {
            String column = entry.getKey();
            String value = entry.getValue();

            if (value == null || value.isBlank()) {
                continue;
            }

            switch (column) {
                case "first_name":
                case "last_name":
                case "phone_primary":
                case "phone_secondary":
                case "email":
                case "linkedin_url":
                case "nickname":
                    whereParts.add(column + " LIKE ?");
                    values.add("%" + value + "%");
                    break;
                case "birth_date_month":
                    whereParts.add("MONTH(birth_date) = ?");
                    values.add(value);
                    break;
                default:
                    // ignore unknown fields
            }
        }

        if (!whereParts.isEmpty()) {
            sql.append(" WHERE ");
            sql.append(String.join(" AND ", whereParts));
        }

        sql.append(" ORDER BY ").append(sortColumn).append(" ").append(direction);

        try (Connection conn = DbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {

            int index = 1;
            for (int i = 0; i < values.size(); i++) {
                String v = values.get(i);
                if (whereParts.get(i).contains("MONTH(")) {
                    ps.setInt(index, Integer.parseInt(v));
                } else {
                    ps.setString(index, v);
                }
                index++;
            }

            try (ResultSet rs = ps.executeQuery()) {
                List<Contact> list = new ArrayList<>();
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
                return list;
            }
        }
    }

    private Contact mapRow(ResultSet rs) throws SQLException {
        Contact c = new Contact();
        c.setContactId(rs.getInt("contact_id"));
        c.setFirstName(rs.getString("first_name"));
        c.setMiddleName(rs.getString("middle_name"));
        c.setLastName(rs.getString("last_name"));
        c.setNickname(rs.getString("nickname"));
        c.setPhonePrimary(rs.getString("phone_primary"));
        c.setPhoneSecondary(rs.getString("phone_secondary"));
        c.setEmail(rs.getString("email"));
        c.setLinkedinUrl(rs.getString("linkedin_url"));
        Date birth = rs.getDate("birth_date");
        if (birth != null) {
            c.setBirthDate(birth.toLocalDate());
        }
        return c;
    }
}
