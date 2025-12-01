package com.cmpe343.project2.service;

import com.cmpe343.project2.dao.ContactDao;
import com.cmpe343.project2.dao.UserDao;
import com.cmpe343.project2.model.Contact;
import com.cmpe343.project2.model.User;
import com.cmpe343.project2.security.PasswordHasher;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class TesterService {

    private final UserDao userDao;
    private final ContactDao contactDao;

    public TesterService(UserDao userDao, ContactDao contactDao) {
        this.userDao = userDao;
        this.contactDao = contactDao;
    }

    public boolean changePassword(User user, String oldPlainPassword, String newPlainPassword) throws SQLException {
        String oldHash = PasswordHasher.hash(oldPlainPassword);

        if (!oldHash.equals(user.getPasswordHash())) {
            return false;
        }

        String newHash = PasswordHasher.hash(newPlainPassword);
        userDao.updatePasswordHash(user.getUserId(), newHash);
        user.setPasswordHash(newHash);
        return true;
    }

    public List<Contact> listAllContacts(String sortField, String sortDirection) throws SQLException {
        return contactDao.listAll(sortField, sortDirection);
    }

    public List<Contact> searchContacts(Map<String, String> criteria,
      String sortField,
     String sortDirection) throws SQLException {
        return contactDao.searchByFields(criteria, sortField, sortDirection);
    }
}
