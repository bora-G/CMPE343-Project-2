package com.cmpe343.project2;

import com.cmpe343.project2.dao.ContactDao;
import com.cmpe343.project2.dao.UserDao;
import com.cmpe343.project2.model.Role;
import com.cmpe343.project2.model.User;
import com.cmpe343.project2.security.PasswordHasher;
import com.cmpe343.project2.service.TesterService;
import com.cmpe343.project2.ui.TesterMenu;

import java.sql.SQLException;
import java.util.Scanner;

public class MainApp {

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);
        UserDao userDao = new UserDao();
        ContactDao contactDao = new ContactDao();

        while (true) {
            try {
                System.out.println("========= CMPE343 Contact Management =========");
                System.out.print("Username: ");
                String username = scanner.nextLine().trim();
                System.out.print("Password: ");
                String password = scanner.nextLine().trim();

                User user = userDao.findByUsername(username);
                if (user == null) {
                    System.out.println("User not found.");
                    continue;
                }

                String enteredHash = PasswordHasher.hash(password);
                if (!enteredHash.equals(user.getPasswordHash())) {
                    System.out.println("Incorrect password.");
                    continue;
                }

                System.out.println("Login successful. Your role: " + user.getRole());

                if (user.getRole() == Role.TESTER) {
                    TesterService testerService = new TesterService(userDao, contactDao);
                    TesterMenu testerMenu = new TesterMenu(testerService, user, scanner);
                    testerMenu.showMenu();
                } else {
                    System.out.println("This demo currently supports only the Tester role.");
                }

            } catch (SQLException e) {
                System.out.println("Database error: " + e.getMessage());
            }
        }
    }
}
