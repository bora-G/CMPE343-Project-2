package com.cmpe343.project2.ui;

import com.cmpe343.project2.model.Contact;
import com.cmpe343.project2.model.User;
import com.cmpe343.project2.service.TesterService;

import java.sql.SQLException;
import java.util.*;

public class TesterMenu {

    private final TesterService testerService;
    private final User loggedUser;
    private final Scanner scanner;

    public TesterMenu(TesterService testerService, User loggedUser, Scanner scanner) {
        this.testerService = testerService;
        this.loggedUser = loggedUser;
        this.scanner = scanner;
    }

    public void showMenu() {
        boolean run = true;
        while (run) {
            printHeader();
            printOptions();

            int choice = readInt("Enter your choice: ");

            switch (choice) {
                case 1:
                    changePassword();
                    break;
                case 2:
                    listAllContacts();
                    break;
                case 3:
                    singleFieldSearch();
                    break;
                case 4:
                    multiFieldSearch();
                    break;
                case 5:
                    System.out.println("Logging out...");
                    run = false;
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private void printHeader() {
        System.out.println("=============================================");
        System.out.println("   Welcome, " + loggedUser.getFullName() + " (Tester)");
        System.out.println("=============================================");
    }

    private void printOptions() {
        System.out.println("1) Change Password");
        System.out.println("2) List All Contacts");
        System.out.println("3) Search by Single Field");
        System.out.println("4) Search by Multiple Fields");
        System.out.println("5) Logout");
    }

    private int readInt(String message) {
        while (true) {
            System.out.print(message);
            String input = scanner.nextLine();
            try {
                return Integer.parseInt(input.trim());
            } catch (NumberFormatException e) {
                System.out.println("Enter a valid number.");
            }
        }
    }

    private String readNonEmpty(String message) {
        while (true) {
            System.out.print(message);
            String input = scanner.nextLine().trim();
            if (!input.isEmpty()) return input;

            System.out.println("This field cannot be empty.");
        }
    }

    private void changePassword() {
        try {
            String oldPass = readNonEmpty("Old password: ");
            String newPass = readNonEmpty("New password: ");
            String newPassConfirm = readNonEmpty("Confirm new password: ");

            if (!newPass.equals(newPassConfirm)) {
                System.out.println("New passwords do not match.");
                return;
            }

            boolean updated = testerService.changePassword(loggedUser, oldPass, newPass);

            if (updated) {
                System.out.println("Password successfully updated.");
            } else {
                System.out.println("Old password is incorrect.");
            }

        } catch (SQLException e) {
            System.out.println("Error while updating password: " + e.getMessage());
        }
    }

    private void listAllContacts() {
        String sortField = askSortField();
        String sortDir = askSortDirection();

        try {
            List<Contact> contacts = testerService.listAllContacts(sortField, sortDir);
            printContacts(contacts);
        } catch (SQLException e) {
            System.out.println("Error listing contacts: " + e.getMessage());
        }
    }

    private void singleFieldSearch() {
        System.out.println("Select field to search:");
        System.out.println("1) First Name");
        System.out.println("2) Last Name");
        System.out.println("3) Primary Phone");
        System.out.println("4) Email");

        int choice = readInt("Your selection: ");

        String field;
        switch (choice) {
            case 1: field = "first_name"; break;
            case 2: field = "last_name"; break;
            case 3: field = "phone_primary"; break;
            case 4: field = "email"; break;
            default:
                System.out.println("Invalid selection.");
                return;
        }

        String value = readNonEmpty("Enter search value: ");
        String sortField = askSortField();
        String sortDir = askSortDirection();

        Map<String, String> criteria = new HashMap<>();
        criteria.put(field, value);

        try {
            List<Contact> contacts = testerService.searchContacts(criteria, sortField, sortDir);
            printContacts(contacts);
        } catch (SQLException e) {
            System.out.println("Error during search: " + e.getMessage());
        }
    }

    private void multiFieldSearch() {
        System.out.println("Enter values for each field (leave blank to skip):");

        System.out.print("First Name contains: ");
        String firstName = scanner.nextLine().trim();

        System.out.print("Last Name contains: ");
        String lastName = scanner.nextLine().trim();

        System.out.print("Primary Phone contains: ");
        String phone = scanner.nextLine().trim();

        System.out.print("Email contains: ");
        String email = scanner.nextLine().trim();

        System.out.print("Birth month (1–12): ");
        String month = scanner.nextLine().trim();

        Map<String, String> criteria = new HashMap<>();

        if (!firstName.isEmpty()) criteria.put("first_name", firstName);
        if (!lastName.isEmpty()) criteria.put("last_name", lastName);
        if (!phone.isEmpty()) criteria.put("phone_primary", phone);
        if (!email.isEmpty()) criteria.put("email", email);
        if (!month.isEmpty()) criteria.put("birth_date_month", month);

        if (criteria.isEmpty()) {
            System.out.println("You must enter at least one search field.");
            return;
        }

        String sortField = askSortField();
        String sortDir = askSortDirection();

        try {
            List<Contact> contacts = testerService.searchContacts(criteria, sortField, sortDir);
            printContacts(contacts);
        } catch (SQLException e) {
            System.out.println("Error during search: " + e.getMessage());
        }
    }

    private String askSortField() {
        System.out.println("Select sorting field:");
        System.out.println("1) Contact ID");
        System.out.println("2) First Name");
        System.out.println("3) Last Name");
        System.out.println("4) Primary Phone");
        System.out.println("5) Email");
        System.out.println("6) Birth Date");

        int choice = readInt("Your selection: ");

        switch (choice) {
            case 1: return "contact_id";
            case 2: return "first_name";
            case 3: return "last_name";
            case 4: return "phone_primary";
            case 5: return "email";
            case 6: return "birth_date";
            default:
                System.out.println("Invalid choice, defaulting to contact_id.");
                return "contact_id";
        }
    }

    private String askSortDirection() {
        System.out.print("Sort direction (ASC/DESC): ");
        String dir = scanner.nextLine().trim();

        if (dir.equalsIgnoreCase("DESC")) return "DESC";
        return "ASC";
    }

    private void printContacts(List<Contact> contacts) {
        if (contacts.isEmpty()) {
            System.out.println("No results found.");
            return;
        }

        System.out.println("------------------------------------------------------------");
        for (Contact c : contacts) {
            System.out.printf(
                "ID: %d | %s %s | Phone: %s | Email: %s | Birth: %s%n",
                c.getContactId(),
                safe(c.getFirstName()),
                safe(c.getLastName()),
                safe(c.getPhonePrimary()),
                safe(c.getEmail()),
                c.getBirthDate() != null ? c.getBirthDate() : "-"
            );
        }
        System.out.println("------------------------------------------------------------");
        System.out.println("Total: " + contacts.size() + " record(s).");
    }

    private String safe(String value) {
        return (value == null ? "-" : value);
    }
}
