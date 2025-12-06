package models;
import menu.MenuUtils;
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
     * @author Melek
     * </ol>
     * </p>
     */
    @Override
    public void showUserMenu() {

        ContactService contactService = new ContactService();

        while (true) {
            MenuUtils.clear();
            MenuUtils.printMenuHeader("JUNIOR MENU");
            MenuUtils.printCentered("Welcome, " + getName(), MenuUtils.CYAN);
            System.out.println();
            MenuUtils.printOption("1", "List all contacts");
            MenuUtils.printOption("2", "Search by single field");
            MenuUtils.printOption("3", "Search by multiple fields");
            MenuUtils.printOption("4", "Sort contacts");
            MenuUtils.printOption("5", "Update existing contact");
            MenuUtils.printOption("6", "Change password");
            MenuUtils.printOption("7", "Undo last operation");
            MenuUtils.printOption("0", "Logout");
            MenuUtils.printPrompt();

            String choice = Input.scanner.nextLine().trim();
            switch (choice) {

                case "1":
                    contactService.listAllContacts();
                    MenuUtils.waitForEnter();
                    break;

                case "2":
                    contactService.searchBySingleField();
                    MenuUtils.waitForEnter();
                    break;

                case "3":
                    contactService.searchByMultipleFields();
                    MenuUtils.waitForEnter();
                    break;

                case "4":
                    contactService.sortContacts();
                    MenuUtils.waitForEnter();
                    break;

                case "5":
                    contactService.updateContact(this);
                    MenuUtils.waitForEnter();
                    break;

                case "6":
                    changePassword();
                    MenuUtils.waitForEnter();
                    break;

                case "7":
                    contactService.undoLastOperation();
                    MenuUtils.waitForEnter();
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