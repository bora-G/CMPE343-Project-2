package services;

import models.Contact;
import models.User;
import repository.ContactRepository;
import input.Input;
import input.MenuInput;
import input.DateInput;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Scanner;
import java.time.LocalDate;

public class ContactService {

    private final ContactRepository contactRepository;

    public ContactService() {
        this.contactRepository = new ContactRepository();
    }

    public List<Contact> listAllContacts() {
        List<Contact> contacts = contactRepository.findAll();
        printResults(contacts);
        return contacts;
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
                results = contactRepository.searchByField("phone_primary", phone);
                break;
            }
            case 7: {
                String phone = askPhoneNumber(Input.scanner, "Enter 10-digit secondary phone (or Q to go back): ");
                if (phone == null) return results;
                results = contactRepository.searchByField("phone_secondary", phone);
                break;
            }
            case 8: {
                // Modified to allow partial search or domain search
                System.out.print("Enter email part or domain (e.g. gmail.com): ");
                String val = Input.scanner.nextLine().trim();
                if(!val.isEmpty()) results = contactRepository.searchByField("email", val);
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
                case 1: dbField = "first_name"; prompt = "First Name contains: "; break;
                case 2: dbField = "last_name"; prompt = "Last Name contains: "; break;
                case 3: dbField = "phone_primary"; prompt = "Phone contains: "; break;
                case 4: dbField = "email"; prompt = "Email contains: "; break;
                case 5: dbField = "nickname"; prompt = "Nickname contains: "; break;
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
            case 1: field = "contact_id"; break;
            case 2: field = "first_name"; break;
            case 3: field = "middle_name"; break;
            case 4: field = "last_name"; break;
            case 5: field = "nickname"; break;
            case 6: field = "phone_primary"; break;
            case 7: field = "phone_secondary"; break;
            case 8: field = "email"; break;
            case 9: field = "linkedin_url"; break;
            case 10: field = "birth_date"; break;
            case 11: field = "created_at"; break;
            case 12: field = "updated_at"; break;
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
        // PERMISSION CHECK: Only Senior Developer and Manager
        String role = actingUser.getRole();
        if (!role.contains("Senior") && !role.contains("Manager")) {
            System.out.println("!!! ACCESS DENIED: Only Senior Developers and Managers can add contacts.");
            return false;
        }

        System.out.println("\n=== Add New Contact ===");
        Contact newContact = new Contact();
        
        System.out.print("First Name (Required): ");
        String fName = Input.scanner.nextLine().trim();
        if (fName.isEmpty()) {
            System.out.println("Error: First Name is required!"); 
            return false;
        }
        newContact.setFirstName(fName);
        
        System.out.print("Last Name (Required): ");
        String lName = Input.scanner.nextLine().trim();
        if (lName.isEmpty()) {
            System.out.println("Error: Last Name is required!");
            return false;
        }
        newContact.setLastName(lName);
        
        String phone = askPhoneNumber(Input.scanner, "Primary Phone (10 digits): ");
        if(phone == null) return false; // Aborted
        newContact.setPhonePrimary(phone);
        
        System.out.print("Middle Name (Optional): ");
        String middle = Input.scanner.nextLine().trim();
        if(!middle.isEmpty()) newContact.setMiddleName(middle);
        
        System.out.print("Nickname (Optional): ");
        String nick = Input.scanner.nextLine().trim();
        if(!nick.isEmpty()) newContact.setNickname(nick);

        System.out.print("Email (Optional): ");
        String email = Input.scanner.nextLine().trim();
        if(!email.isEmpty()) newContact.setEmail(email);

        System.out.print("LinkedIn URL (Optional): ");
        String linked = Input.scanner.nextLine().trim();
        if(!linked.isEmpty()) newContact.setLinkedinUrl(linked);

        LocalDate bday = DateInput.readDate("Birth Date");
        if(bday != null) {
            newContact.setBirthDate(java.sql.Date.valueOf(bday));
        }

        boolean success = contactRepository.insert(newContact);
        if (success) {
            System.out.println("SUCCESS: New contact added. ID: " + newContact.getContactId());
        } else {
            System.out.println("ERROR: Could not add contact.");
        }
        return success;
    }

    public boolean updateContact(User actingUser) {
        // PERMISSION CHECK: Junior, Senior, and Manager (EXCEPT Tester)
        if (actingUser.getRole().contains("Tester")) {
            System.out.println("!!! ACCESS DENIED: Testers cannot update contacts.");
            return false;
        }

        System.out.print("Enter ID of the contact to update (or Q to cancel): ");
        String input = Input.scanner.nextLine();
        if(input.equalsIgnoreCase("q")) return false;

        try {
            int id = Integer.parseInt(input);
            Contact contact = contactRepository.findById(id);
            if(contact == null) {
                System.out.println("Contact not found with this ID.");
                return false;
            }

            System.out.println("Updating: " + contact.getFirstName() + " " + contact.getLastName());
            System.out.println("(Press Enter to keep current value)");

            System.out.print("First Name (" + contact.getFirstName() + "): ");
            String f = Input.scanner.nextLine().trim();
            if(!f.isEmpty()) contact.setFirstName(f);

            System.out.print("Last Name (" + contact.getLastName() + "): ");
            String l = Input.scanner.nextLine().trim();
            if(!l.isEmpty()) contact.setLastName(l);
            
            System.out.print("Phone (" + contact.getPhonePrimary() + "): ");
            String p = Input.scanner.nextLine().trim();
            if(!p.isEmpty()) {
                 if (p.matches("^[0-9]{10}$")) {
                     contact.setPhonePrimary(p);
                 } else {
                     System.out.println("Invalid format. Phone not updated.");
                 }
            }
            
            System.out.print("Email (" + (contact.getEmail() == null ? "none" : contact.getEmail()) + "): ");
            String e = Input.scanner.nextLine().trim();
            if(!e.isEmpty()) contact.setEmail(e);

            boolean success = contactRepository.update(contact);
            if(success) System.out.println("SUCCESS: Contact updated.");
            else System.out.println("ERROR: Update failed.");
            return success;

        } catch(NumberFormatException e) {
            System.out.println("Invalid ID format.");
            return false;
        }
    }

    public boolean deleteContact(User actingUser) {
        // PERMISSION CHECK: Only Senior and Manager
        String role = actingUser.getRole();
        if (!role.contains("Senior") && !role.contains("Manager")) {
            System.out.println("!!! ACCESS DENIED: Only Senior Developers and Managers can delete contacts.");
            return false;
        }

        System.out.print("Enter ID of the contact to DELETE (or Q to cancel): ");
        String input = Input.scanner.nextLine();
        if(input.equalsIgnoreCase("q")) return false;

        try {
            int id = Integer.parseInt(input);
            Contact contact = contactRepository.findById(id);
            if(contact == null) {
                System.out.println("Contact not found.");
                return false;
            }

            System.out.println("WARNING: You are about to delete " + contact.getFirstName() + " " + contact.getLastName());
            System.out.print("Are you sure? (Type 'YES' to confirm): ");
            String confirm = Input.scanner.nextLine().trim();
            
            if(confirm.equals("YES")) {
                boolean success = contactRepository.delete(id);
                if(success) System.out.println("SUCCESS: Contact deleted.");
                else System.out.println("ERROR: Delete failed.");
                return success;
            } else {
                System.out.println("Delete cancelled.");
            }
        } catch(NumberFormatException e) {
            System.out.println("Invalid ID format.");
        }
        return false;
    }

    // --- HELPER METHODS ---

    private void printResults(List<Contact> contacts) {
        if (contacts == null || contacts.isEmpty()) {
            System.out.println("No records found.");
            return;
        }
        System.out.println("--------------------------------------------------------------------------------");
        System.out.printf("%-5s %-15s %-15s %-15s %-20s%n", "ID", "First Name", "Last Name", "Phone", "Email");
        System.out.println("--------------------------------------------------------------------------------");
        for (Contact c : contacts) {
            System.out.printf("%-5d %-15s %-15s %-15s %-20s%n", 
                c.getContactId(), 
                c.getFirstName(), 
                c.getLastName(), 
                c.getPhonePrimary(),
                (c.getEmail() != null ? c.getEmail() : ""));
        }
        System.out.println("--------------------------------------------------------------------------------");
        System.out.println("Total: " + contacts.size() + " records.");
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

            if(input.equalsIgnoreCase("q")) return null;

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
                // Uses your existing readDate
                LocalDate date = DateInput.readDate("Enter " + label);
                if (date == null) return results;
                results = contactRepository.searchByField(fieldName, date.toString());
                break;
            }
            case 2: { 
                // Calls the NEW static method
                Integer day = DateInput.readDay();
                if (day == null) return results;
                
                // Calls the NEW static method
                Integer month = DateInput.readMonth();
                if (month == null) return results;
                
                String dd = String.format("%02d", day);
                String mm = String.format("%02d", month);
                // Pattern: ____-MM-DD
                results = contactRepository.searchByField(fieldName, "____-" + mm + "-" + dd);
                break;
            }
            case 3: {
                Integer day = DateInput.readDay();
                if (day == null) return results;
                
                String dd = String.format("%02d", day);
                // Pattern: ____-__-DD
                results = contactRepository.searchByField(fieldName, "____-__-" + dd);
                break;
            }
            case 4: {
                Integer month = DateInput.readMonth();
                if (month == null) return results;
                
                String mm = String.format("%02d", month);
                // Pattern: ____-MM-__
                results = contactRepository.searchByField(fieldName, "____-" + mm + "-__");
                break;
            }
            case 5: {
                Integer year = DateInput.readYear();
                if (year == null) return results;
                
                String yy = String.format("%04d", year);
                // Pattern: YYYY-__-__
                results = contactRepository.searchByField(fieldName, yy + "-__-__");
                break;
            }
        }
        return results;
    }
    public void showStatistics() {
        List<Contact> allContacts = contactRepository.findAll();
        if (allContacts.isEmpty()) {
            System.out.println("No contacts available for statistics.");
            return;
        }

        System.out.println("\n=== Contact Statistics ===");
        
        // 1. Total count
        System.out.println("Total Contacts: " + allContacts.size());

        // 2. LinkedIn Stats
        long withLinkedin = allContacts.stream()
                .filter(c -> c.getLinkedinUrl() != null && !c.getLinkedinUrl().isEmpty())
                .count();
        System.out.println("Contacts with LinkedIn: " + withLinkedin);
        System.out.println("Contacts without LinkedIn: " + (allContacts.size() - withLinkedin));

        // 3. Age Stats
        LocalDate now = LocalDate.now();
        int minAge = Integer.MAX_VALUE;
        int maxAge = Integer.MIN_VALUE;
        long totalAge = 0;
        int countWithAge = 0;

        Contact youngest = null;
        Contact oldest = null;

        for (Contact c : allContacts) {
            if (c.getBirthDate() != null) {
                LocalDate birth = c.getBirthDate().toLocalDate();
                int age = java.time.Period.between(birth, now).getYears();
                
                if (age < minAge) {
                    minAge = age;
                    youngest = c;
                }
                if (age > maxAge) {
                    maxAge = age;
                    oldest = c;
                }
                totalAge += age;
                countWithAge++;
            }
        }

        if (countWithAge > 0) {
            System.out.println("Youngest Contact: " + youngest.getFirstName() + " " + youngest.getLastName() + " (" + minAge + " years)");
            System.out.println("Oldest Contact: " + oldest.getFirstName() + " " + oldest.getLastName() + " (" + maxAge + " years)");
            System.out.printf("Average Age: %.1f years%n", (double) totalAge / countWithAge);
        } else {
            System.out.println("No birth dates available for age statistics.");
        }

        // 4. Name Frequency (Example: First Names)
        Map<String, Integer> nameFreq = new HashMap<>();
        for (Contact c : allContacts) {
            nameFreq.merge(c.getFirstName(), 1, Integer::sum);
        }
        
        System.out.println("\nMost Common First Names:");
        nameFreq.entrySet().stream()
                .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue()))
                .limit(5)
                .forEach(e -> System.out.println(" - " + e.getKey() + ": " + e.getValue()));
                
        System.out.println("==========================");
    }
}