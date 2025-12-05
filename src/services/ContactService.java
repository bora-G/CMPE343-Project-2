package services;

import models.Contact;
import models.User;
import repository.ContactRepository;
import input.Input;
import input.MenuInput;
import input.DateInput;

import java.time.Period;

import Undo.UndoManager;
import Undo.AddContactCommand;
import Undo.UpdateContactCommand;
import Undo.DeleteContactCommand;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Scanner;
import java.time.LocalDate;

public class ContactService {

    private final ContactRepository contactRepository;
    private final UndoManager undoManager;
    private static final String NAME_REGEX = "^[a-zA-ZÇĞİÖŞÜçğıöşü]+$";

    public ContactService() {
        this.contactRepository = new ContactRepository();
        this.undoManager = new UndoManager();
    }

    public List<Contact> listAllContacts() {
        List<Contact> contacts = contactRepository.findAll();
        printResults(contacts);
        return contacts;
    }

    public void undoLastOperation() {
        undoManager.undoLast();
    }

    public List<Contact> searchBySingleField() {
        List<Contact> results = new ArrayList<>();

        System.out.println("\n=== Search by Single Field ===");
        System.out.println("Which field do you want to search in?");
        System.out.println("1- Contact ID");
        System.out.println("2- First Name");
        System.out.println("3- Middle Name");
        System.out.println("4- Last Name");
        System.out.println("5- Nickname");
        System.out.println("6- Primary Phone Number");
        System.out.println("7- Secondary Phone Number");
        System.out.println("8- E-mail (by domain or part)");
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
                if (input.equalsIgnoreCase("q")) return results;
                try {
                    int id = Integer.parseInt(input);
                    Contact found = contactRepository.findById(id);
                    if (found != null) {
                        results.add(found);
                    } else {
                        System.out.println("No contact found with ID: " + id);
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Invalid ID format.");
                }
                break;
            }
            case 2:
                results = searchByStringField("first_name", "First Name");
                break;
            case 3:
                results = searchByStringField("middle_name", "Middle Name");
                break;
            case 4:
                results = searchByStringField("last_name", "Last Name");
                break;
            case 5:
                results = searchByStringField("nickname", "Nickname");
                break;
            case 6: {
                String phone = askPhoneNumber(Input.scanner, "Enter 10-digit primary phone (or Q to go back): ");
                if (phone == null) return results;
                results = contactRepository.searchByFieldExact("phone_primary", phone);
                break;
            }
            case 7: {
                String phone = askPhoneNumber(Input.scanner, "Enter 10-digit secondary phone (or Q to go back): ");
                if (phone == null) return results;
                results = contactRepository.searchByFieldExact("phone_secondary", phone);
                break;
            }
            case 8: {
                System.out.print("Enter email part or domain (e.g. gmail.com): ");
                String val = Input.scanner.nextLine().trim();
                if (!val.isEmpty()) results = contactRepository.searchByField("email", val);
                break;
            }
            case 9: {
                System.out.println("1- Has LinkedIn Account");
                System.out.println("2- No LinkedIn Account");
                Integer opt = MenuInput.readMenuChoice(1, 2, "Select");
                if (opt != null) {
                    results = contactRepository.searchByLinkedinPresence(opt == 1);
                }
                break;
            }
            case 10:
                results = searchByDateField("birth_date", "Birth Date");
                break;
            case 11:
                results = searchByDateField("created_at", "Creation Date");
                break;
            case 12:
                results = searchByDateField("updated_at", "Update Date");
                break;
        }

        printResults(results);
        return results;
    }

    public List<Contact> searchByMultipleFields() {
        System.out.println("\n=== Multi-Field Search ===");
        System.out.println("Add filters one by one. Enter '0' to execute search.");

        Map<String, String> criteria = new HashMap<>();

        while (true) {
            if (!criteria.isEmpty()) {
                System.out.println("Current filters: " + criteria);
            }

            System.out.println("1- First Name");
            System.out.println("2- Last Name");
            System.out.println("3- Phone Number");
            System.out.println("4- Email");
            System.out.println("5- Nickname");
            System.out.println("0- EXECUTE SEARCH");

            Integer choice = MenuInput.readMenuChoice(0, 5, "Add Filter");
            if (choice == null || choice == 0) break;

            String dbField = "";
            String prompt = "";

            switch (choice) {
                case 1:
                    dbField = "first_name";
                    prompt = "First Name contains: ";
                    break;
                case 2:
                    dbField = "last_name";
                    prompt = "Last Name contains: ";
                    break;
                case 3:
                    dbField = "phone_primary";
                    prompt = "Phone contains: ";
                    break;
                case 4:
                    dbField = "email";
                    prompt = "Email contains: ";
                    break;
                case 5:
                    dbField = "nickname";
                    prompt = "Nickname contains: ";
                    break;
            }

            System.out.print(prompt);
            String val = Input.scanner.nextLine().trim();
            if (!val.isEmpty()) {
                criteria.put(dbField, val);
            }
        }

        if (criteria.isEmpty()) {
            System.out.println("No criteria selected. Returning to menu.");
            return new ArrayList<>();
        }

        List<Contact> results = contactRepository.searchByMultipleCriteria(criteria);
        printResults(results);
        return results;
    }

    public List<Contact> sortContacts() {
        System.out.println("\n=== Sort Contacts ===");
        System.out.println("Which field do you want to sort by?");
        System.out.println("1- Contact ID");
        System.out.println("2- First Name");
        System.out.println("3- Middle Name");
        System.out.println("4- Last Name");
        System.out.println("5- Nickname");
        System.out.println("6- Primary Phone Number");
        System.out.println("7- Secondary Phone Number");
        System.out.println("8- E-mail");
        System.out.println("9- LinkedIn URL");
        System.out.println("10- Birth Date");
        System.out.println("11- Creation Date");
        System.out.println("12- Update Date");

        Integer fieldChoice = MenuInput.readMenuChoice(1, 12, "Select field to sort by");
        if (fieldChoice == null) return new ArrayList<>();

        String field = "first_name"; // Default
        switch (fieldChoice) {
            case 1:
                field = "contact_id";
                break;
            case 2:
                field = "first_name";
                break;
            case 3:
                field = "middle_name";
                break;
            case 4:
                field = "last_name";
                break;
            case 5:
                field = "nickname";
                break;
            case 6:
                field = "phone_primary";
                break;
            case 7:
                field = "phone_secondary";
                break;
            case 8:
                field = "email";
                break;
            case 9:
                field = "linkedin_url";
                break;
            case 10:
                field = "birth_date";
                break;
            case 11:
                field = "created_at";
                break;
            case 12:
                field = "updated_at";
                break;
        }

        System.out.println("1- Ascending (A-Z / Oldest First)");
        System.out.println("2- Descending (Z-A / Newest First)");
        Integer dirChoice = MenuInput.readMenuChoice(1, 2, "Select direction");
        String dir = (dirChoice != null && dirChoice == 2) ? "DESC" : "ASC";

        List<Contact> results = contactRepository.findAllSorted(field, dir);
        printResults(results);
        return results;
    }

    public boolean addContact(User actingUser) {
        String role = actingUser.getRole();
        if (!role.contains("Senior") && !role.contains("Manager")) {
            System.out.println("!!! ACCESS DENIED: Only Senior Developers and Managers can add contacts.");
            return false;
        }

        System.out.println("\n=== Add New Contact ===");
        Contact newContact = new Contact();

        // FIRST NAME (REQUIRED, LETTERS ONLY, Q TO CANCEL)
        String fName;
        while (true) {
            System.out.print("First Name (Required, letters only, or Q to cancel): ");
            fName = Input.scanner.nextLine().trim();
            if (fName.equalsIgnoreCase("q")) {
                System.out.println("Add contact cancelled.");
                return false;
            }
            if (fName.isBlank() || !fName.matches(NAME_REGEX)) {
                System.out.println("Error: First Name is required and must contain only letters!");
                continue;
            }
            break;
        }
        newContact.setFirstName(fName);

        String lName;
        while (true) {
            System.out.print("Last Name (Required, letters only, or Q to cancel): ");
            lName = Input.scanner.nextLine().trim();
            if (lName.equalsIgnoreCase("q")) {
                System.out.println("Add contact cancelled.");
                return false;
            }
            if (lName.isBlank() || !lName.matches(NAME_REGEX)) {
                System.out.println("Error: Last Name is required and must contain only letters!");
                continue;
            }
            break;
        }
        newContact.setLastName(lName);

        String phone;
        while (true) {
            phone = askPhoneNumber(Input.scanner, "Primary Phone (10 digits, or Q to cancel): ");
            if (phone == null) {
                System.out.println("Add contact cancelled.");
                return false;
            }
            if (!isPhoneUnique(phone, null)) {
                System.out.println("This phone number is already used by another contact.");
                continue;
            }
            break;
        }
        newContact.setPhonePrimary(phone);

        System.out.print("Do you want to enter a Second Phone Number? (Y/N, or Q to cancel): ");
        String answersecondphone = Input.scanner.nextLine().trim();
        if (answersecondphone.equalsIgnoreCase("q")) {
            System.out.println("Add contact cancelled.");
            return false;
        }

        if (answersecondphone.equalsIgnoreCase("Y")) {
            while (true) {
                String phonesec = askPhoneNumber(Input.scanner, "Secondary Phone (10 digits, Optional, or Q to skip): ");
                if (phonesec == null) {
                    // Q: skip secondary phone (optional)
                    break;
                }
                if (!isPhoneUnique(phonesec, null)) {
                    System.out.println("This phone number is already used by another contact.");
                    continue;
                }
                newContact.setPhoneSecondary(phonesec);
                break;
            }
        }

        while (true) {
            System.out.print("Middle Name (Optional, letters only, or Q to cancel): ");
            String middle = Input.scanner.nextLine().trim();
            if (middle.equalsIgnoreCase("q")) {
                System.out.println("Add contact cancelled.");
                return false;
            }
            if (!middle.isBlank() && !middle.matches(NAME_REGEX)) {
                System.out.println("Enter a valid Middle Name (letters only)!");
                continue;
            }
            newContact.setMiddleName(middle);
            break;
        }

        String nick;
        while (true) {
            System.out.print("Nickname (Required, unique, or Q to cancel): ");
            nick = Input.scanner.nextLine().trim();

            if (nick.equalsIgnoreCase("q")) {
                System.out.println("Add contact cancelled.");
                return false;
            }

            if (nick.isBlank()) {
                System.out.println("Error: Nickname is required!");
                continue;
            }

            if (!isNicknameUnique(nick, null)) {
                System.out.println("This nickname is already used by another contact.");
                continue;
            }

            break;
        }
        newContact.setNickname(nick);

        String email;
        while (true) {
            System.out.print("Email (Required, or Q to cancel): ");
            email = Input.scanner.nextLine().trim();
            if (email.equalsIgnoreCase("q")) {
                System.out.println("Add contact cancelled.");
                return false;
            }
            if (!safeEmail(email)) {
                System.out.println("Invalid email format. Must contain '@' and '.' and be properly formed.");
                continue;
            }
            if (!isEmailUnique(email, null)) {
                System.out.println("This e-mail is already used by another contact.");
                continue;
            }
            break;
        }
        newContact.setEmail(email);

        while (true) {
            System.out.print("LinkedIn URL (Optional, or Q to cancel): ");
            String linked = Input.scanner.nextLine().trim();
            if (linked.equalsIgnoreCase("q")) {
                System.out.println("Add contact cancelled.");
                return false;
            }
            if (linked.isBlank()) {
                break;
            }
            if (!safeLinkedIn(linked)) {
                System.out.println("Link must be a LinkedIn link (https://www.linkedin.com/...).");
                continue;
            }
            if (!isLinkedinUnique(linked, null)) {
                System.out.println("This LinkedIn URL is already used by another contact.");
                continue;
            }
            newContact.setLinkedinUrl(linked);
            break;
        }

        LocalDate bday = DateInput.readDate("Birth Date");
        if (bday != null) {
            newContact.setBirthDate(java.sql.Date.valueOf(bday));
        }

        boolean success = contactRepository.insert(newContact);
        if (success) {
            System.out.println("SUCCESS: New contact added. ID: " + newContact.getContactId());
            undoManager.push(new AddContactCommand(newContact.getContactId(), contactRepository));
        } else {
            System.out.println("ERROR: Could not add contact.");
        }
        return success;
    }

    private Contact copyContact(Contact c) {
        Contact copy = new Contact();
        copy.setContactId(c.getContactId());
        copy.setFirstName(c.getFirstName());
        copy.setMiddleName(c.getMiddleName());
        copy.setLastName(c.getLastName());
        copy.setNickname(c.getNickname());
        copy.setPhonePrimary(c.getPhonePrimary());
        copy.setPhoneSecondary(c.getPhoneSecondary());
        copy.setEmail(c.getEmail());
        copy.setLinkedinUrl(c.getLinkedinUrl());
        copy.setBirthDate(c.getBirthDate());
        copy.setCreatedAt(c.getCreatedAt());
        copy.setUpdatedAt(c.getUpdatedAt());
        return copy;
    }

    public boolean updateContact(User actingUser) {
        if (actingUser.getRole().contains("Tester")) {
            System.out.println("!!! ACCESS DENIED: Testers cannot update contacts.");
            return false;
        }

        System.out.print("Enter ID of the contact to update (or Q to cancel): ");
        String input = Input.scanner.nextLine();
        if (input.equalsIgnoreCase("q")) return false;

        try {
            int id = Integer.parseInt(input);
            Contact contact = contactRepository.findById(id);
            if (contact == null) {
                System.out.println("Contact not found with this ID.");
                return false;
            }

            Contact oldSnapshot = copyContact(contact);

            System.out.println("Updating: " + safe(contact.getFirstName()) + " " + safe(contact.getLastName()));
            System.out.println("(Press Enter to keep current value, or Q to cancel)");
            System.out.println();

            while (true) {
                System.out.print("First Name (" + safe(contact.getFirstName()) + "): ");
                String f = Input.scanner.nextLine().trim();
                if (f.equalsIgnoreCase("q")) {
                    System.out.println("Update cancelled.");
                    return false;
                }
                if (f.isBlank()) {
                    break;
                }
                if (!f.matches(NAME_REGEX)) {
                    System.out.println("First Name must contain only letters.");
                    continue;
                }
                contact.setFirstName(f);
                break;
            }

            while (true) {
                System.out.print("Middle Name (" + safe(contact.getMiddleName()) + "): ");
                String m = Input.scanner.nextLine().trim();
                if (m.equalsIgnoreCase("q")) {
                    System.out.println("Update cancelled.");
                    return false;
                }
                if (!m.isBlank() && !m.matches(NAME_REGEX)) {
                    System.out.println("Middle Name must contain only letters.");
                    continue;
                }
                contact.setMiddleName(m);
                break;
            }

            while (true) {
                System.out.print("Last Name (" + safe(contact.getLastName()) + "): ");
                String l = Input.scanner.nextLine().trim();
                if (l.equalsIgnoreCase("q")) {
                    System.out.println("Update cancelled.");
                    return false;
                }
                if (l.isBlank()) {
                    break;
                }
                if (!l.matches(NAME_REGEX)) {
                    System.out.println("Last Name must contain only letters.");
                    continue;
                }
                contact.setLastName(l);
                break;
            }

            while (true) {
                System.out.print("Nickname (" + safe(contact.getNickname()) + "): ");
                String n = Input.scanner.nextLine().trim();

                if (n.equalsIgnoreCase("q")) {
                    System.out.println("Update cancelled.");
                    return false;
                }

                if (n.isBlank()) {
                    break;
                }

                if (!isNicknameUnique(n, contact.getContactId())) {
                    System.out.println("This nickname is already used by another contact.");
                    continue;
                }

                contact.setNickname(n);
                break;
            }

            while (true) {
                System.out.print("Primary Phone (" + safe(contact.getPhonePrimary()) + "): ");
                String p1 = Input.scanner.nextLine().trim();
                if (p1.equalsIgnoreCase("q")) {
                    System.out.println("Update cancelled.");
                    return false;
                }
                if (p1.isBlank()) {
                    break;
                }
                if (!p1.matches("^[0-9]{10}$")) {
                    System.out.println("Invalid format. Primary phone NOT updated. (Must be 10 digits)");
                    continue;
                }
                if (!isPhoneUnique(p1, contact.getContactId())) {
                    System.out.println("This phone number is already used by another contact.");
                    continue;
                }
                contact.setPhonePrimary(p1);
                break;
            }

            while (true) {
                System.out.print("Secondary Phone (" + safe(contact.getPhoneSecondary()) + "): ");
                String p2 = Input.scanner.nextLine().trim();
                if (p2.equalsIgnoreCase("q")) {
                    System.out.println("Update cancelled.");
                    return false;
                }
                if (p2.isBlank()) {
                    break;
                }
                if (!p2.matches("^[0-9]{10}$")) {
                    System.out.println("Invalid format. Secondary phone NOT updated. (Must be 10 digits)");
                    continue;
                }
                if (!isPhoneUnique(p2, contact.getContactId())) {
                    System.out.println("This phone number is already used by another contact.");
                    continue;
                }
                contact.setPhoneSecondary(p2);
                break;
            }

            while (true) {
                System.out.print("E-mail (" + safe(contact.getEmail()) + "): ");
                String newMail = Input.scanner.nextLine().trim();
                if (newMail.equalsIgnoreCase("q")) {
                    System.out.println("Update cancelled.");
                    return false;
                }
                if (newMail.isBlank()) {
                    break;
                }
                if (!safeEmail(newMail)) {
                    System.out.println("Invalid email format. Must contain '@' and '.'");
                    continue;
                }
                if (!isEmailUnique(newMail, contact.getContactId())) {
                    System.out.println("This e-mail is already used by another contact.");
                    continue;
                }
                contact.setEmail(newMail);
                break;
            }

            while (true) {
                System.out.print("LinkedIn URL (" + safe(contact.getLinkedinUrl()) + "): ");
                String linked = Input.scanner.nextLine().trim();
                if (linked.equalsIgnoreCase("q")) {
                    System.out.println("Update cancelled.");
                    return false;
                }
                if (linked.isBlank()) {
                    break;
                }
                if (!safeLinkedIn(linked)) {
                    System.out.println("Link must be a LinkedIn link.");
                    continue;
                }
                if (!isLinkedinUnique(linked, contact.getContactId())) {
                    System.out.println("This LinkedIn URL is already used by another contact.");
                    continue;
                }
                contact.setLinkedinUrl(linked);
                break;
            }

            System.out.println("Current Birth Date: " + safe(contact.getBirthDate()));
            System.out.print("Do you want to update Birth Date? (Y/N or Q to cancel): ");
            String bdChoice = Input.scanner.nextLine().trim();
            if (bdChoice.equalsIgnoreCase("q")) {
                System.out.println("Update cancelled.");
                return false;
            }
            if (bdChoice.equalsIgnoreCase("y")) {
                LocalDate bday = DateInput.readDate("New Birth Date");
                if (bday != null) {
                    contact.setBirthDate(java.sql.Date.valueOf(bday));
                }
            }

            boolean success = contactRepository.update(contact);
            if (success) {
                System.out.println("SUCCESS: Contact updated.");
                undoManager.push(new UpdateContactCommand(oldSnapshot, contactRepository));
            } else {
                System.out.println("ERROR: Update failed.");
            }
            return success;

        } catch (NumberFormatException e) {
            System.out.println("Invalid ID format.");
            return false;
        }
    }

    public boolean deleteContact(User actingUser) {
        String role = actingUser.getRole();
        if (!role.contains("Senior") && !role.contains("Manager")) {
            System.out.println("!!! ACCESS DENIED: Only Senior Developers and Managers can delete contacts.");
            return false;
        }

        System.out.print("Enter ID of the contact to DELETE (or Q to cancel): ");
        String input = Input.scanner.nextLine();
        if (input.equalsIgnoreCase("q")) return false;

        try {
            int id = Integer.parseInt(input);
            Contact contact = contactRepository.findById(id);
            if (contact == null) {
                System.out.println("Contact not found.");
                return false;
            }

            System.out.println("WARNING: You are about to delete " + contact.getFirstName() + " " + contact.getLastName());
            System.out.print("Are you sure? (Type 'YES' to confirm): ");
            String confirm = Input.scanner.nextLine().trim();

            if (confirm.equalsIgnoreCase("yes")) {
                boolean success = contactRepository.delete(id);
                if (success) {
                    System.out.println("SUCCESS: Contact deleted.");
                    undoManager.push(new DeleteContactCommand(contact, contactRepository));
                } else {
                    System.out.println("ERROR: Delete failed.");
                }
                return success;
            } else {
                System.out.println("Delete cancelled.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid ID format.");
        }
        return false;
    }

    private void printResults(List<Contact> contacts) {
        if (contacts == null || contacts.isEmpty()) {
            System.out.println("No records found.");
            return;
        }

        System.out.println("================================================================================");

        for (Contact c : contacts) {
            System.out.println(formatContact(c));
            System.out.println("--------------------------------------------------------------------------------");
        }

        System.out.println("TOTAL: " + contacts.size() + " record(s).");
        System.out.println("================================================================================");
    }

    private List<Contact> searchByStringField(String dbField, String displayName) {
        System.out.print("Enter " + displayName + " (or part of it): ");
        String val = Input.scanner.nextLine().trim();
        if (val.isEmpty()) return new ArrayList<>();
        return contactRepository.searchByField(dbField, val);
    }

    public String askPhoneNumber(Scanner scanner, String message) {
        while (true) {
            System.out.print(message);
            String input = scanner.nextLine().trim();

            if (input.equalsIgnoreCase("q")) return null;

            if (input.matches("^[0-9]{10}$")) {
                return input;
            }

            System.out.println("Invalid phone number!");
            System.out.println("Phone number must be 10 digits. Example: 5551234567");
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
        if (mode == null) return results;

        switch (mode) {
            case 1: {
                LocalDate date = DateInput.readDate("Enter " + label);
                if (date == null) return results;
                results = contactRepository.searchByField(fieldName, date.toString());
                break;
            }
            case 2: {
                Integer day = DateInput.readDay();
                if (day == null) return results;

                Integer month = DateInput.readMonth();
                if (month == null) return results;

                String dd = String.format("%02d", day);
                String mm = String.format("%02d", month);
                results = contactRepository.searchByField(fieldName, "____-" + mm + "-" + dd);
                break;
            }
            case 3: {
                Integer day = DateInput.readDay();
                if (day == null) return results;

                String dd = String.format("%02d", day);
                results = contactRepository.searchByField(fieldName, "____-__-" + dd);
                break;
            }
            case 4: {
                Integer month = DateInput.readMonth();
                if (month == null) return results;

                String mm = String.format("%02d", month);
                results = contactRepository.searchByField(fieldName, "____-" + mm + "-__");
                break;
            }
            case 5: {
                Integer year = DateInput.readYear();
                if (year == null) return results;

                String yy = String.format("%04d", year);
                results = contactRepository.searchByField(fieldName, yy + "-__-__");
                break;
            }
        }
        return results;
    }

    public void showStatistics() {
        List<Contact> contacts = contactRepository.findAll();

        System.out.println("\n=== CONTACT STATISTICS ===");
        System.out.println("Total contacts: " + contacts.size());

        int withLinkedIn = 0;


        Contact youngest = null;
        Contact oldest = null;

        // Yaş ortalaması için
        int ageCount = 0;
        int ageSum = 0;

        Contact newestCreated = null;
        Contact oldestCreated = null;

        LocalDate today = LocalDate.now();

        for (Contact c : contacts) {

            if (c.getLinkedinUrl() != null && !c.getLinkedinUrl().isBlank()) {
                withLinkedIn++;
            }


            if (c.getBirthDate() != null) {
                LocalDate birth = c.getBirthDate().toLocalDate();

                int age = Period.between(birth, today).getYears();
                ageSum += age;
                ageCount++;

                if (youngest == null || birth.isAfter(youngest.getBirthDate().toLocalDate())) {
                    youngest = c;
                }
                if (oldest == null || birth.isBefore(oldest.getBirthDate().toLocalDate())) {
                    oldest = c;
                }
            }

            if (c.getCreatedAt() != null) {
                if (newestCreated == null || c.getCreatedAt().after(newestCreated.getCreatedAt())) {
                    newestCreated = c;
                }
                if (oldestCreated == null || c.getCreatedAt().before(oldestCreated.getCreatedAt())) {
                    oldestCreated = c;
                }
            }
        }

        System.out.println("Contacts with LinkedIn : " + withLinkedIn);

        if (ageCount > 0) {
            double avgAge = (double) ageSum / ageCount;
            System.out.printf("Average age (with birth date): %.1f years%n", avgAge);
        } else {
            System.out.println("Average age: N/A (no birth dates)");
        }

        System.out.println("-------------------------------------");

        if (youngest != null) {
            System.out.println("Youngest contact (by age):");
            System.out.println(formatShort(youngest, true, false, today));
        } else {
            System.out.println("Youngest contact: N/A (no birth dates)");
        }

        System.out.println();

        if (oldest != null) {
            System.out.println("==========================\n");
            System.out.println("Oldest contact (by age):");
            System.out.println(formatShort(oldest, true, false, today));
        } else {
            System.out.println("==========================\n");
            System.out.println("Oldest contact: N/A (no birth dates)");
        }

        System.out.println();

        if (newestCreated != null) {
            System.out.println("==========================\n");
            System.out.println("\n");
            System.out.println("Most recently created contact:");
            System.out.println(formatShort(newestCreated, false, true, today));
        } else {
            System.out.println("==========================\n");
            System.out.println("\n");
            System.out.println("Most recently created contact: N/A (no created_at)");
        }

        System.out.println();

        if (oldestCreated != null) {
            System.out.println("==========================\n");
            System.out.println("\n");
            System.out.println("Oldest (first created) contact:");
            System.out.println(formatShort(oldestCreated, false, true, today));
        } else {
            System.out.println("Oldest (first created) contact: N/A (no created_at)");
        }

        System.out.println("==========================\n");
    }


    private String formatShort(Contact c, boolean includeAge, boolean includeCreatedAt, LocalDate today) {

        String first = safe(c.getFirstName());
        String last = safe(c.getLastName());
        String phone = safe(c.getPhonePrimary());
        String email = safe(c.getEmail());

        StringBuilder sb = new StringBuilder();

        sb.append("ID: ").append(c.getContactId()).append("\n");
        sb.append("Name       : ").append(first).append(" ").append(last).append("\n");
        sb.append("Phone(main): ").append(phone).append("\n");
        sb.append("E-mail     : ").append(email).append("\n");

        if (includeAge) {
            String ageStr = "N/A";
            if (c.getBirthDate() != null) {
                LocalDate birth = c.getBirthDate().toLocalDate();
                int age = Period.between(birth, today).getYears();
                ageStr = String.valueOf(age);
            }
            sb.append("Age        : ").append(ageStr).append("\n");
        }

        if (includeCreatedAt) {
            sb.append("Created at : ").append(safe(c.getCreatedAt())).append("\n");
        }

        return sb.toString();
    }

    private String formatContact(Contact c) {
        return "Contact " + c.getContactId() + "\n" + "----------------------------\n" + "First Name     : " + safe(c.getFirstName()) + "\n" + "Middle Name    : " + safe(c.getMiddleName()) + "\n" + "Last Name      : " + safe(c.getLastName()) + "\n" + "Nickname       : " + safe(c.getNickname()) + "\n" + "\n" + "Primary Phone  : " + safe(c.getPhonePrimary()) + "\n" + "Secondary Phone: " + safe(c.getPhoneSecondary()) + "\n" + "\n" + "E-mail         : " + safe(c.getEmail()) + "\n" + "LinkedIn       : " + safe(c.getLinkedinUrl()) + "\n" + "\n" + "Birth Date     : " + safe(c.getBirthDate()) + "\n" + "Created At     : " + safe(c.getCreatedAt()) + "\n" + "Updated At     : " + safe(c.getUpdatedAt()) + "\n";
    }

    private String safe(Object o) {
        return (o == null) ? "" : o.toString();
    }

    private boolean safeLinkedIn(String linkedin) {
        if (linkedin == null || linkedin.isBlank()) {
            return true;
        }
        return linkedin.startsWith("https://www.linkedin.com/");
    }

    private boolean safeEmail(String email) {
        if (email == null || email.isBlank()) return false;

        if (!email.contains("@")) return false;

        if (!email.contains(".")) return false;

        if (email.indexOf("@") != email.lastIndexOf("@")) return false;

        int at = email.indexOf("@");
        String domain = email.substring(at + 1);
        if (!domain.contains(".")) return false;

        if (!email.matches("^[A-Za-z0-9._%+-@]+$")) return false;

        return true;
    }


    private boolean isPhoneUnique(String phone, Integer currentId) {
        if (phone == null || phone.isBlank()) {
            return true;
        }

        List<Contact> prim = contactRepository.searchByFieldExact("phone_primary", phone);
        for (Contact c : prim) {
            if (currentId == null || c.getContactId() != currentId) {
                return false;
            }
        }

        List<Contact> sec = contactRepository.searchByFieldExact("phone_secondary", phone);
        for (Contact c : sec) {
            if (currentId == null || c.getContactId() != currentId) {
                return false;
            }
        }

        return true;
    }

    private boolean isEmailUnique(String email, Integer currentId) {
        if (email == null || email.isBlank()) {
            return true;
        }

        List<Contact> list = contactRepository.searchByFieldExact("email", email);
        for (Contact c : list) {
            if (currentId == null || c.getContactId() != currentId) {
                return false;
            }
        }
        return true;
    }

    private boolean isLinkedinUnique(String url, Integer currentId) {
        if (url == null || url.isBlank()) {
            return true;
        }

        List<Contact> list = contactRepository.searchByFieldExact("linkedin_url", url);
        for (Contact c : list) {
            if (currentId == null || c.getContactId() != currentId) {
                return false;
            }
        }
        return true;
    }

    private boolean isNicknameUnique(String nickname, Integer currentId) {
        if (nickname == null || nickname.isBlank()) {
            return true;
        }

        List<Contact> list = contactRepository.searchByFieldExact("nickname", nickname);
        for (Contact c : list) {
            if (currentId == null || c.getContactId() != currentId) {
                return false;
            }
        }
        return true;
    }
}
