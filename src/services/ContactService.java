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

    /** Repository responsible for all contact-related database operations. */
    private final ContactRepository contactRepository;

    /** Manages undoable operations for contact modifications. */
    private final UndoManager undoManager;

    /** Regular expression used for validating name fields. */
    private static final String NAME_REGEX = "^[a-zA-ZÇĞİÖŞÜçğıöşü]+$";

    /**
     * Initializes the ContactService with a new ContactRepository and UndoManager.
     * This service handles all high-level operations related to contacts,
     * including CRUD and undo functionality.
     */
    public ContactService() {
        this.contactRepository = new ContactRepository();
        this.undoManager = new UndoManager();
    }

    /**
     * Retrieves all contacts from the repository and prints them to the console.
     *
     * @return a list containing all contacts stored in the system
     */
    public List<Contact> listAllContacts() {
        List<Contact> contacts = contactRepository.findAll();
        printResults(contacts);
        return contacts;
    }

    /**
     * Reverts the most recent undoable operation performed on contacts.
     * This method delegates the undo logic to the {@link UndoManager}.
     * If no operations are available to undo, nothing happens.
     */
    public void undoLastOperation() {
        undoManager.undoLast();
    }

    /**
     * Allows the user to search for contacts based on a single selected field.
     * Displays a menu of searchable attributes (e.g., name, phone, email, dates),
     * requests user input, and performs the corresponding repository query.
     *
     * Supported fields include:
     * - Contact ID
     * - First / Middle / Last Name
     * - Nickname
     * - Primary & Secondary Phone Numbers
     * - Email (partial or domain search)
     * - LinkedIn presence
     * - Birth, creation, and update dates
     *
     * The method prints the search results and returns them as a list.
     *
     * @return a list of contacts matching the selected search criteria.
     */
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

    /**
     * Allows the user to perform a multi-field search by adding multiple filters sequentially.
     * The user may choose from several fields (name, phone, email, nickname) and input
     * partial text values. Each filter is stored and combined to perform a multi-criteria
     * database query.
     *
     * <p>The method works interactively:
     * <ul>
     *     <li>User selects one field at a time.</li>
     *     <li>User enters a value that should be contained in the selected field.</li>
     *     <li>Filters accumulate in a map until the user selects "0" to execute the search.</li>
     * </ul>
     *
     * After collecting all criteria, the method delegates the query to
     * {@link ContactRepository#searchByMultipleCriteria(Map)} and prints the results.
     *
     * @return a list of contacts matching all specified filters;
     *         an empty list if no filters were selected or no matches were found.
     */
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

    /**
     * Allows the user to sort contacts based on a selected field and sort direction.
     * The method displays a menu of sortable fields (e.g., names, phone numbers,
     * dates), asks the user for the sorting criterion, and then requests whether the
     * sort should be ascending or descending.
     *
     * <p>Sorting fields include:
     * <ul>
     *     <li>Contact ID</li>
     *     <li>First / Middle / Last Name</li>
     *     <li>Nickname</li>
     *     <li>Primary & Secondary Phone Numbers</li>
     *     <li>Email</li>
     *     <li>LinkedIn URL</li>
     *     <li>Birth / Creation / Update Dates</li>
     * </ul>
     *
     * After determining the sort preferences, the method delegates to
     * {@link ContactRepository#findAllSorted(String, String)} and prints the results.
     *
     * @return a sorted list of contacts, or an empty list if an invalid selection was made.
     */
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

    /**
     * Interactively creates and saves a new contact using console input.
     * <p>
     * Only users whose role contains {@code "Senior"} or {@code "Manager"} are
     * allowed to add contacts. If the acting user does not have sufficient
     * privileges, the method prints an access denied message and returns {@code false}.
     * </p>
     *
     * <p>The method guides the user through entering and validating:</p>
     * <ul>
     *     <li>First and last name (required, letters only, cancellable with 'Q')</li>
     *     <li>Primary phone number (required, 10 digits, unique)</li>
     *     <li>Secondary phone number (optional, 10 digits, unique, skippable)</li>
     *     <li>Middle name (optional, letters only)</li>
     *     <li>Nickname (required, unique)</li>
     *     <li>Email (required, validated format, unique)</li>
     *     <li>LinkedIn URL (optional, must be a valid LinkedIn link if provided, unique)</li>
     *     <li>Birth date (optional)</li>
     * </ul>
     *
     * <p>The user can cancel the entire operation at various steps by entering 'Q',
     * in which case no contact is created and {@code false} is returned.</p>
     *
     * <p>On successful insertion into the repository, an {@link AddContactCommand}
     * is pushed to the {@link UndoManager} to allow undoing this operation later.</p>
     *
     * @param actingUser the user attempting to add a new contact; used for role-based access control
     * @return {@code true} if the contact is successfully added; {@code false} if
     *         access is denied, the operation is canceled by the user, or insertion fails
     */
    public boolean addContact(User actingUser) {
        String role = actingUser.getRole();
        if (!role.contains("Senior") && !role.contains("Manager")) {
            System.out.println("!!! ACCESS DENIED: Only Senior Developers and Managers can add contacts.");
            return false;
        }

        System.out.println("\n=== Add New Contact ===");
        Contact newContact = new Contact();

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

    /**
     * Creates and returns a deep copy of the given {@link Contact} object.
     * <p>
     * This method manually copies all primitive and immutable fields from the original
     * contact into a new {@code Contact} instance. It is primarily used for undo/redo
     * operations to ensure that modifications do not affect the stored reference of
     * the original contact.
     * </p>
     *
     * @param c the contact to copy; must not be null
     * @return a new {@code Contact} instance with all fields duplicated from the original
     */
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

    /**
     * Interactively updates an existing contact selected by its ID.
     * <p>
     * The method first checks the role of the acting user and denies access if the
     * role contains {@code "Tester"}, as testers are not allowed to modify contacts.
     * </p>
     *
     * <p>Workflow:</p>
     * <ol>
     *     <li>Prompts for a contact ID (or 'Q' to cancel).</li>
     *     <li>Loads the corresponding contact from the repository.</li>
     *     <li>Takes a snapshot of the original contact using {@link #copyContact(Contact)}
     *         for potential undo operations.</li>
     *     <li>For each editable field (names, nickname, phones, email, LinkedIn URL, birth date),
     *         the user may:
     *         <ul>
     *             <li>Press Enter to keep the current value,</li>
     *             <li>Enter a new valid value, or</li>
     *             <li>Type 'Q' to cancel the entire update.</li>
     *         </ul>
     *     </li>
     *     <li>Performs validation and uniqueness checks for fields like nickname,
     *         phone numbers, email, and LinkedIn URL.</li>
     *     <li>If the update succeeds, an {@link UpdateContactCommand} is pushed to
     *         the {@link UndoManager} for undo support.</li>
     * </ol>
     *
     * @param actingUser the user attempting to update the contact; used for role checks
     * @return {@code true} if the contact is successfully updated;
     *         {@code false} if access is denied, input is invalid, update is cancelled,
     *         or the repository update fails
     */
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

    /**
     * Deletes an existing contact selected by its ID.
     * <p>
     * Only users whose role contains {@code "Senior"} or {@code "Manager"} are
     * allowed to delete contacts. If the acting user does not have sufficient
     * privileges, the method prints an access denied message and returns {@code false}.
     * </p>
     *
     * <p>Workflow:</p>
     * <ol>
     *     <li>Prompts for a contact ID (or 'Q' to cancel).</li>
     *     <li>Loads the contact from the repository; if not found, aborts.</li>
     *     <li>Shows a warning and asks the user to type {@code "YES"} to confirm.</li>
     *     <li>If confirmed, deletes the contact and pushes a {@link DeleteContactCommand}
     *         to the {@link UndoManager} for undo support.</li>
     * </ol>
     *
     * @param actingUser the user attempting to delete the contact; used for role-based access control
     * @return {@code true} if the contact is successfully deleted; {@code false} if
     *         access is denied, the contact is not found, the operation is cancelled,
     *         or the delete operation fails
     */
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

    /**
     * Prints a formatted list of contacts to the console.
     * <p>
     * Each contact is rendered using {@link #formatContact(Contact)} and separated
     * by a visual divider. If the list is {@code null} or empty, a
     * {@code "No records found."} message is displayed instead.
     * </p>
     *
     * @param contacts the list of contacts to print; may be {@code null} or empty
     */
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

    /**
     * Searches contacts by a single string-based field using a partial match.
     * <p>
     * The user is prompted to enter a value (or part of it) for the given display
     * name, which is then used to search the specified database field via
     * {@link ContactRepository#searchByField(String, String)}.
     * </p>
     *
     * @param dbField     the name of the database column to search in (e.g. {@code "first_name"})
     * @param displayName the human-readable name of the field shown in the prompt
     * @return a list of contacts whose given field contains the entered value;
     *         an empty list if no value is entered
     */
    private List<Contact> searchByStringField(String dbField, String displayName) {
        System.out.print("Enter " + displayName + " (or part of it): ");
        String val = Input.scanner.nextLine().trim();
        if (val.isEmpty()) return new ArrayList<>();
        return contactRepository.searchByField(dbField, val);
    }

    /**
     * Prompts the user for a 10-digit phone number and validates the input.
     * <p>
     * The user is repeatedly asked to enter a phone number until a valid value
     * is provided or the operation is cancelled. A valid phone number:
     * </p>
     * <ul>
     *     <li>Contains exactly 10 digits</li>
     *     <li>Includes digits only (no spaces, symbols, or letters)</li>
     * </ul>
     *
     * <p>Entering {@code 'Q'} (in any case) cancels the input and causes this method
     * to return {@code null}.</p>
     *
     * @param scanner the {@link Scanner} used to read input from the console
     * @param message the prompt message displayed to the user
     * @return a valid 10-digit phone number as a {@link String}, or {@code null} if the user cancels with 'Q'
     */
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

    /**
     * Searches contacts by a date-based field (such as birth date or creation date)
     * using different matching modes.
     * <p>
     * The user can choose how specific the date filter should be:
     * </p>
     * <ul>
     *     <li>Exact date (full YYYY-MM-DD)</li>
     *     <li>Day + Month (any year)</li>
     *     <li>Day only (any month/year)</li>
     *     <li>Month only (any day/year)</li>
     *     <li>Year only (any day/month)</li>
     * </ul>
     *
     * <p>Depending on the selected mode, the method constructs a pattern string
     * (using underscores for wildcard parts) and delegates the search to
     * {@link ContactRepository#searchByField(String, String)}.</p>
     *
     * @param fieldName the name of the date field in the database (e.g. {@code "birth_date"})
     * @param label     a human-readable label used in prompts (e.g. {@code "Birth Date"})
     * @return a list of contacts whose specified date field matches the chosen criteria;
     *         an empty list if the user cancels or no results are found
     */
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

    /**
     * Calculates and displays various statistics about all contacts in the system.
     * <p>
     * The method loads all contacts from the repository and prints:
     * </p>
     * <ul>
     *     <li>Total number of contacts</li>
     *     <li>Number of contacts with a LinkedIn URL</li>
     *     <li>Average age of contacts (for those with a birthdate)</li>
     *     <li>Youngest contact (by birthdate)</li>
     *     <li>Oldest contact (by birthdate)</li>
     *     <li>Most recently created contact (by {@code created_at})</li>
     *     <li>Oldest (first created) contact (by {@code created_at})</li>
     * </ul>
     *
     * <p>Contacts without birthdays or creation timestamps are ignored for
     * the corresponding calculations. The details of selected contacts are printed
     * using {@link #formatShort(Contact, boolean, boolean, LocalDate)}.</p>
     */
    public void showStatistics() {
        List<Contact> contacts = contactRepository.findAll();

        System.out.println("\n=== CONTACT STATISTICS ===");
        System.out.println("Total contacts: " + contacts.size());

        int withLinkedIn = 0;


        Contact youngest = null;
        Contact oldest = null;

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

    /**
     * Produces a short, customizable text representation of a contact.
     * <p>
     * The output includes the contact's basic fields (name, phone, email), and
     * optionally age and creation timestamp depending on the boolean flags.
     * Useful for statistics summaries and compact listings.
     * </p>
     *
     * @param c                the contact to format
     * @param includeAge       whether the age should be included in the output
     * @param includeCreatedAt whether the creation timestamp should be included
     * @param today            the date used to calculate age
     * @return a formatted string representing the contact in short form
     */
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

    /**
     * Produces a full, detailed multi-line representation of a contact,
     * including personal information, phone numbers, email, LinkedIn,
     * and timestamps.
     * <p>
     * This method is used for full listings such as search results and
     * contact detail views.
     * </p>
     *
     * @param c the contact to format
     * @return a formatted multi-line string containing all contact details
     */

    private String formatContact(Contact c) {
        return "Contact " + c.getContactId() + "\n" + "----------------------------\n" + "First Name     : " + safe(c.getFirstName()) + "\n" + "Middle Name    : " + safe(c.getMiddleName()) + "\n" + "Last Name      : " + safe(c.getLastName()) + "\n" + "Nickname       : " + safe(c.getNickname()) + "\n" + "\n" + "Primary Phone  : " + safe(c.getPhonePrimary()) + "\n" + "Secondary Phone: " + safe(c.getPhoneSecondary()) + "\n" + "\n" + "E-mail         : " + safe(c.getEmail()) + "\n" + "LinkedIn       : " + safe(c.getLinkedinUrl()) + "\n" + "\n" + "Birth Date     : " + safe(c.getBirthDate()) + "\n" + "Created At     : " + safe(c.getCreatedAt()) + "\n" + "Updated At     : " + safe(c.getUpdatedAt()) + "\n";
    }

    /**
     * Safely converts an object to a string.
     * Returns an empty string if the object is {@code null}.
     *
     * @param o the object to convert
     * @return the object's string representation, or an empty string if null
     */
    private String safe(Object o) {

        return (o == null) ? "" : o.toString();
    }

    /**
     * Validates whether a LinkedIn URL is acceptable.
     * <p>
     * Rules:
     * <ul>
     *     <li>Blank or null values are considered valid (optional field)</li>
     *     <li>Non-blank values must start with
     *         {@code https://www.linkedin.com/}</li>
     * </ul>
     *
     * @param linkedin the LinkedIn URL to validate
     * @return true if valid or optional; false if invalid
     */
    private boolean safeLinkedIn(String linkedin) {
        if (linkedin == null || linkedin.isBlank()) {
            return true;
        }
        return linkedin.startsWith("https://www.linkedin.com/");
    }

    /**
     * Validates the structure of an email address.
     * <p>
     * Rules checked:
     * </p>
     * <ul>
     *     <li>Must not be null or blank</li>
     *     <li>Must contain exactly one '@'</li>
     *     <li>Must contain at least one '.' after '@'</li>
     *     <li>Domain part must have a dot (e.g., gmail.com)</li>
     *     <li>May include only alphanumeric characters or . _ % + - @</li>
     * </ul>
     *
     * @param email the email string to validate
     * @return true if the email is valid; false otherwise
     */
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

    /**
     * Checks whether a phone number (primary or secondary) is unique across all contacts.
     * <p>
     * A phone number is considered unique if no other contact (except the one with
     * {@code currentId}, if provided) has the same primary or secondary phone value.
     * </p>
     *
     * <p>This is used during add/update operations to prevent duplicate phone numbers
     * across different contacts.</p>
     *
     * @param phone      the phone number to check
     * @param currentId  the ID of the contact being updated, or {@code null} if adding a new contact
     * @return {@code true} if the phone number is unique or blank; {@code false} if another contact already uses it
     */
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

    /**
     * Checks whether an email address is unique across all contacts.
     * <p>
     * The email is considered unique if no other contact (except the one with
     * {@code currentId}, if updating) has the same email value.
     * </p>
     *
     * @param email      the email value to check
     * @param currentId  the ID of the contact being updated, or {@code null} when adding a new one
     * @return {@code true} if the email is unique or blank; {@code false} if already used by another contact
     */
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

    /**
     * Checks whether a LinkedIn URL is unique across all contacts.
     * <p>
     * Blank URLs are considered valid, and uniqueness is checked only for non-empty values.
     * The method ensures that no other contact (except the one with {@code currentId})
     * has the same LinkedIn link.
     * </p>
     *
     * @param url        the LinkedIn URL to validate
     * @param currentId  the ID of the contact being updated, or {@code null} when adding a new one
     * @return {@code true} if the URL is unique or blank; {@code false} if already used by another contact
     */
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

    /**
     * Checks whether a nickname is unique across all contacts.
     * <p>
     * Nickname is a required unique identifier, so this method ensures that
     * no other contact uses the same nickname (except the current one during update).
     * </p>
     *
     * @param nickname   the nickname to check
     * @param currentId  the ID of the contact being updated, or {@code null} when adding a new contact
     * @return {@code true} if the nickname is unique or blank; {@code false} if another contact already uses it
     */
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
