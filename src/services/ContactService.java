package services;

import models.Contact;
import models.User;
import repository.ContactRepository;
import input.Input;

import input.MenuInput;
import input.DateInput;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.time.LocalDate;

public class ContactService {

    private final ContactRepository contactRepository;

    public ContactService() {
        this.contactRepository = new ContactRepository();
    }

    public List<Contact> listAllContacts() {
        // TODO: contactRepository.findAll() çağır
        List<Contact> contacts = contactRepository.findAll();

        for (int i = 0; i < contacts.size(); i++) {
            System.out.printf("%s", contacts.get(i));
            System.err.println();

        }
        return null;
    }

    public List<Contact> searchBySingleField() {

        List<Contact> results = new ArrayList<>();

        System.out.println("Which field do you want to search in?");
        System.out.println("1- Contact ID");
        System.out.println("2- First Name");
        System.out.println("3- Middle Name");
        System.out.println("4- Last Name");
        System.out.println("5- Nickname");
        System.out.println("6- Primary Phone Number");
        System.out.println("7- Secondary Phone Number");
        System.out.println("8- E-mail (by domain)");
        System.out.println("9- LinkedIn (exists / not)");
        System.out.println("10- Birth Date");
        System.out.println("11- Creation Date");
        System.out.println("12- Update Date");

        Integer choice = MenuInput.readMenuChoice(1, 12, "Select field");
        if (choice == null) {
            return results;
        }

        switch (choice) {

            case 1: {
                System.out.print("Enter Contact ID (or Q to go back): ");
                String input = Input.scanner.nextLine().trim();
                if (input.equalsIgnoreCase("q")) {
                    return results;
                }
                try {
                    int id = Integer.parseInt(input);
                    Contact found = contactRepository.findById(id);
                    if (found != null) {
                        results.add(found);
                    } else {
                        System.out.println("No contact found with ID: " + id);
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Invalid ID.");
                }
                break;
            }

            case 2: {
                System.out.print("Enter first name (or part of it) or Q to go back: ");
                String name = Input.scanner.nextLine().trim();
                if (name.equalsIgnoreCase("q"))
                    return results;
                results = contactRepository.searchByField("first_name", name);
                break;
            }

            case 3: {
                System.out.print("Enter middle name (or part of it) or Q to go back: ");
                String middle = Input.scanner.nextLine().trim();
                if (middle.equalsIgnoreCase("q"))
                    return results;
                results = contactRepository.searchByField("middle_name", middle);
                break;
            }

            case 4: {
                System.out.print("Enter last name (or part of it) or Q to go back: ");
                String last = Input.scanner.nextLine().trim();
                if (last.equalsIgnoreCase("q"))
                    return results;
                results = contactRepository.searchByField("last_name", last);
                break;
            }

            case 5: {
                System.out.print("Enter nickname (or part of it) or Q to go back: ");
                String nick = Input.scanner.nextLine().trim();
                if (nick.equalsIgnoreCase("q"))
                    return results;
                results = contactRepository.searchByField("nickname", nick);
                break;
            }

            case 6: {
                String phone1 = askPhoneNumber(Input.scanner, "Enter 10-digit primary phone (or Q to go back): ");
                if (phone1 == null)
                    return results;
                results = contactRepository.searchByField("phone_primary", phone1);
                break;
            }

            case 7: {
                String phone2 = askPhoneNumber(Input.scanner, "Enter 10-digit secondary phone (or Q to go back): ");
                if (phone2 == null)
                    return results;
                results = contactRepository.searchByField("phone_secondary", phone2);
                break;
            }

            case 8: {
                String domain = askEmailDomain(Input.scanner);
                if (domain == null)
                    return results;
                results = contactRepository.searchByField("email", domain);
                break;
            }

            case 9: {
                System.out.println("1- Has LinkedIn");
                System.out.println("2- No LinkedIn");
                Integer opt = MenuInput.readMenuChoice(1, 2, "LinkedIn");
                if (opt == null)
                    return results;
                boolean hasLinkedIn = (opt == 1);
                results = contactRepository.searchByLinkedinPresence(hasLinkedIn);
                break;
            }

            case 10:
                results = searchByDateField("birth_date", "birth date");
                break;

            case 11:
                results = searchByDateField("created_at", "creation date");
                break;

            case 12:
                results = searchByDateField("updated_at", "update date");
                break;

            default:
                System.out.println("Invalid choice.");
                return results;
        }

        if (results.isEmpty()) {
            System.out.println("No contacts found for the given criteria.");
        } else {
            for (Contact c : results) {
                System.out.println(c);
                System.out.println();
            }
        }

        return results;
    }

    public List<Contact> searchByMultipleFields() {
        // TODO: birden fazla kriter al, repository'e pasla
        return null;
    }

    public List<Contact> sortContacts() {
        // TODO: hangi kolona göre + ASC/DESC al, repository'e pasla
        return null;
    }

    public boolean addContact(User actingUser) {
        // TODO: Senior/Manager gibi yetkili kullanıcı mı kontrol et,
        // sonra contactRepository.insert(...)
        return false;
    }

    public boolean updateContact(User actingUser) {
        // TODO: Junior üstü rollere izin ver, contactRepository.update(...)
        return false;
    }

    public boolean deleteContact(User actingUser) {
        // TODO: sadece Senior/Manager'a izin ver, contactRepository.delete(...)
        return false;
    }

    public String askPhoneNumber(Scanner scanner, String message) {
        while (true) {
            System.out.print(message);
            String input = scanner.nextLine().trim();

            if (input.matches("^[0-9]{10}$")) {
                return input;
            }

            System.out.println("Invalid phone number!");
            System.out.println("Phone number must be 10 digits and not start with 0 or area code (+90 , +1).");
            System.out.println("Example: 5551234567\n");
        }
    }

    private String askEmailDomain(Scanner scanner) {
        while (true) {
            System.out.print("Enter email domain (examples: gmail.com, stu.khas.edu.tr): ");
            String input = scanner.nextLine().trim().toLowerCase();

            if (input.startsWith("@")) {
                input = input.substring(1);
            }

            if (!input.contains(".")) {
                System.out.println("Invalid domain: must contain at least one '.' (dot).");
                System.out.println("Examples: gmail.com, stu.khas.edu.tr\n");
                continue;
            }

            if (!input.matches("^[a-z0-9.-]+$")) {
                System.out.println("Invalid characters in domain!");
                System.out.println("Allowed: letters, digits, '.', '-'\n");
                continue;
            }

            if (!input.startsWith("@"))
                input = "@" + input;
            return input;
        }
    }

    private List<Contact> searchByDateField(String fieldName, String label) {
        List<Contact> results = new ArrayList<>();

        System.out.println("How do you want to search " + label + "?");
        System.out.println("1- Exact date");
        System.out.println("2- Day + Month (any year)");
        System.out.println("3- Day only (any month/year)");
        System.out.println("4- Month only (any day/year)");
        System.out.println("5- Year only (any day/month)");

        Integer mode = MenuInput.readMenuChoice(1, 5, "Select");
        if (mode == null)
            return results;

        switch (mode) {

            case 1: { 
                LocalDate date = DateInput.readDate("Enter " + label);
                if (date == null)
                    return results;

                String value = date.toString(); 
                results = contactRepository.searchByField(fieldName, value);
                break;
            }

            case 2: { 
                Integer day = readDayFromUser();
                if (day == null)
                    return results;

                Integer month = readMonthFromUser();
                if (month == null)
                    return results;

                String dd = String.format("%02d", day);
                String mm = String.format("%02d", month);

                String pattern = "____-" + mm + "-" + dd;
                results = contactRepository.searchByField(fieldName, pattern);
                break;
            }

            case 3: {
                Integer day = readDayFromUser();
                if (day == null)
                    return results;

                String dd = String.format("%02d", day);
                String pattern = "____-__-" + dd;
                results = contactRepository.searchByField(fieldName, pattern);
                break;
            }
            case 4: {
                Integer month = readMonthFromUser();
                if (month == null)
                    return results;

                String mm = String.format("%02d", month);
                String pattern = "____-" + mm + "-__";
                results = contactRepository.searchByField(fieldName, pattern);
                break;
            }
            case 5: {
                Integer year = readYearFromUser();
                if (year == null)
                    return results;

                String yy = String.format("%04d", year);
                String pattern = yy + "__-__";
                results = contactRepository.searchByField(fieldName, pattern);
                break;
            }
        }

        return results;
    }

    private Integer readDayFromUser() {
        while (true) {
            System.out.print("Enter day (1-31) or Q to go back: ");
            String input = Input.scanner.nextLine().trim();

            if (input.equalsIgnoreCase("q"))
                return null;

            try {
                int day = Integer.parseInt(input);
                if (day >= 1 && day <= 31) {
                    return day;
                }
                System.out.println("Day must be between 1 and 31.");
            } catch (NumberFormatException e) {
                System.out.println("Invalid day.");
            }
        }
    }

    private Integer readMonthFromUser() {
        while (true) {
            System.out.print("Enter month (1-12) or Q to go back: ");
            String input = Input.scanner.nextLine().trim();

            if (input.equalsIgnoreCase("q"))
                return null;

            try {
                int month = Integer.parseInt(input);
                if (month >= 1 && month <= 12) {
                    return month;
                }
                System.out.println("Month must be between 1 and 12.");
            } catch (NumberFormatException e) {
                System.out.println("Invalid month.");
            }
        }
    }

      private Integer readYearFromUser() {
        while (true) {
            System.out.print("Enter year (0- "+LocalDate.now().getYear()+" ) or Q to go back: ");
            String input = Input.scanner.nextLine().trim();

            if (input.equalsIgnoreCase("q"))
                return null;

            try {
                int day = Integer.parseInt(input);
                if (day >= 0 && day <= LocalDate.now().getYear()) {
                    return day;
                }
                System.out.println("Day must be between 0 and "+LocalDate.now().getYear()+".");
            } catch (NumberFormatException e) {
                System.out.println("Invalid year.");
            }
        }
    }

}
