package models;

import input.Input;
import services.ContactService;

/**
 * Represents a user with the "Junior Developer" role in the system.
 * <p>
 * According to the project specifications, the Junior Developer role has an intermediate
 * level of access. They possess all permissions of a Tester (Read-only access), plus the
 * ability to <b>update</b> existing contact records.
 * </p>
 * <p>
 * <b>Permissions defined in Project Specs:</b>
 * <ul>
 * <li>List all contacts, Search (Single/Multi-field), and Sort results.</li>
 * <li>Change own password and Logout.</li>
 * <li><b>Update</b> existing contact information.</li>
 * </ul>
 * <b>Restrictions:</b> This role strictly cannot Add or Delete contacts.
 * </p>
 *
 * @author [Group Members Names Here]
 * @version 1.0
 * @see models.User
 * @see models.Tester
 * @see models.Senior
 */
public class Junior extends User {

    /**
     * Constructs a new Junior user and sets the role identifier to "Junior".
     */
    public Junior() {
        setRole("Junior");
    }

    /**
     * Displays the interactive menu specific to the Junior Developer role.
     * <p>
     * This menu provides options to:
     * <ol>
     * <li>List all contacts.</li>
     * <li>Search contacts by a single field.</li>
     * <li>Search contacts by multiple fields.</li>
     * <li>Sort contacts.</li>
     * <li>Update an existing contact (Unique permission start point).</li>
     * <li>Change password.</li>
     * <li>Undo the last operation (specifically updates).</li>
     * <li>Logout.</li>
     * </ol>
     * </p>
     */
    @Override
    public void showUserMenu() {

        ContactService contactService = new ContactService();

        while (true) {
            System.out.println("\n=== JUNIOR MENU ===");
            System.out.println("1) List all contacts");
            System.out.println("2) Search by single field");
            System.out.println("3) Search by multiple fields");
            System.out.println("4) Sort contacts");
            System.out.println("5) Update existing contact");
            System.out.println("6) Change password");
            System.out.println("7) Undo last operation");
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
                    contactService.updateContact(this);
                    break;

                case "6":
                    changePassword();
                    break;

                case "7":
                    contactService.undoLastOperation();
                    break;

                case "0":
                    logout();
                    return;

                default:
                    System.out.println("Invalid choice, try again.");
            }
        }
    }
}