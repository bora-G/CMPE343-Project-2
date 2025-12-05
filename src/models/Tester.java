package models;

import input.Input;
import services.ContactService;

/**
 * Represents a user with the "Tester" role in the system.
 * <p>
 * The Tester role has the most restricted level of access within the system's role hierarchy.
 * It provides <b>read-only</b> access to the contact database.
 * </p>
 * <p>
 * <b>Permissions defined in Project Specs:</b>
 * <ul>
 * <li><b>List:</b> View all contacts.</li>
 * <li><b>Search:</b> Perform single-field and multi-field searches.</li>
 * <li><b>Sort:</b> Sort contact lists by various fields.</li>
 * <li><b>System:</b> Change their own password and Logout.</li>
 * </ul>
 * <b>Restrictions:</b> Testers <b>cannot</b> Add, Update, or Delete contacts.
 * </p>
 *
 * @author [Group Members Names Here]
 * @version 1.0
 * @see models.User
 * @see services.ContactService
 */
public class Tester extends User {

    /**
     * Service instance to handle contact-related operations.
     */
    private final ContactService contactService;

    /**
     * Constructs a new Tester user.
     * <p>
     * Sets the role identifier to "Tester" and initializes the contact service.
     * </p>
     */
    public Tester() {
        setRole("Tester");
        this.contactService = new ContactService();
    }

    /**
     * Displays the interactive menu specific to the Tester role.
     * <p>
     * Provides a loop allowing the user to select read-only operations until they choose to logout.
     * The menu options correspond exactly to the permissions outlined in the project requirements table.
     * </p>
     */
    @Override
    public void showUserMenu() {
        while (true) {
            System.out.println();
            System.out.println("=== TESTER MENU ===");
            System.out.println("1) List all contacts");
            System.out.println("2) Search by single field");
            System.out.println("3) Search by multiple fields");
            System.out.println("4) Sort contacts");
            System.out.println("5) Change password");
            System.out.println("0) Logout");
            System.out.print("Your choice: ");

            String choice = Input.scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    listAllContacts();
                    break;
                case "2":
                    searchBySingleField();
                    break;
                case "3":
                    searchByMultipleFields();
                    break;
                case "4":
                    sortContacts();
                    break;
                case "5":
                    changePassword();
                    break;
                case "0":
                    logout();
                    return; 
                default:
                    System.out.println("Invalid choice, try again.");
            }
        }
    }


    /**
     * Delegates the "List all contacts" operation to the ContactService.
     */
    private void listAllContacts() {
      
        contactService.listAllContacts(); 
    }

    /**
     * Delegates the "Search by single field" operation to the ContactService.
     */
    private void searchBySingleField() {   

        contactService.searchBySingleField();
    }

    /**
     * Delegates the "Search by multiple fields" operation to the ContactService.
     */
    private void searchByMultipleFields() {

        contactService.searchByMultipleFields();  
    }

    /**
     * Delegates the "Sort contacts" operation to the ContactService.
     */
    private void sortContacts() {
      
        contactService.sortContacts();  
    }
}