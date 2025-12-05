package models;

import input.Input;
import services.ContactService;

/**
 * Represents a user with the "Senior Developer" role in the system.
 * <p>
 * The Senior Developer role possesses full administrative privileges regarding
 * {@link models.Contact} management. In the hierarchy of developer roles (Tester < Junior < Senior),
 * this role grants the most extensive permissions on the contact database.
 * </p>
 * <p>
 * <b>Permissions defined in Project Specs:</b>
 * <ul>
 * <li><b>Read:</b> List all contacts, Search (Single/Multi-field), Sort.</li>
 * <li><b>Update:</b> Update existing contact details (inherited from Junior).</li>
 * <li><b>Create:</b> Add new contacts to the system (Exclusive to Senior & Manager).</li>
 * <li><b>Delete:</b> Remove existing contacts (Exclusive to Senior & Manager).</li>
 * <li><b>System:</b> Change password, Logout, and Undo operations.</li>
 * </ul>
 * </p>
 *
 * @author [Group Members Names Here]
 * @version 1.0
 * @see models.User
 * @see models.Junior
 * @see services.ContactService
 */
public class Senior extends User {

    /**
     * Constructs a new Senior user and sets the role identifier to "Senior".
     */
    public Senior() {
        setRole("Senior");
    }

    /**
     * Displays the interactive menu specific to the Senior Developer role.
     * <p>
     * This menu provides the full suite of operations available for contact management.
     * </p>
     * <p>
     * <b>Menu Options:</b>
     * <ol>
     * <li>List all contacts.</li>
     * <li>Search contacts by single field.</li>
     * <li>Search contacts by multiple fields.</li>
     * <li>Sort contacts.</li>
     * <li>Add new contact (Create).</li>
     * <li>Update existing contact (Update).</li>
     * <li>Delete contact (Delete).</li>
     * <li>Change password.</li>
     * <li>Undo last operation (Add, Update, or Delete).</li>
     * <li>Logout.</li>
     * </ol>
     * </p>
     */
    @Override
    public void showUserMenu() {

        ContactService contactService = new ContactService();

        while (true) {
            System.out.println("\n=== SENIOR MENU ===");
            System.out.println("1) List all contacts");
            System.out.println("2) Search by single field");
            System.out.println("3) Search by multiple fields");
            System.out.println("4) Sort contacts");
            System.out.println("5) Add new contact");
            System.out.println("6) Update contact");
            System.out.println("7) Delete contact");
            System.out.println("8) Change password");
            System.out.println("9) Undo last operation");
            System.out.println("0) Logout");
            System.out.print("Your choice: ");

            String choice = Input.scanner.nextLine().trim();

            switch (choice) {

                case "1":
                    contactService.listAllContacts();
                    break;

                case "2":
                    contactService.searchBySingleField();
                    break;

                case "3":
                    contactService.searchByMultipleFields();
                    break;

                case "4":
                    contactService.sortContacts();
                    break;

                case "5":
                    contactService.addContact(this);
                    break;

                case "6":
                    contactService.updateContact(this);
                    break;

                case "7":
                    contactService.deleteContact(this);
                    break;

                case "8":
                    changePassword();
                    break;

                case "9":
                    contactService.undoLastOperation();
                    break;

                case "0":
                    logout();
                    return;

                default:
                    System.out.println("Invalid choice.");
            }
        }
    }
}