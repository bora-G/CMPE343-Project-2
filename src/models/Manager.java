package models;

import input.Input;
import services.ContactService;
import services.UserService;
import menu.MenuUtils;  

/**
 * Represents a user with the "Manager" role, the administrative authority of the system.
 * <p>
 * Unlike Developer roles (Tester, Junior, Senior) which focus on managing the Contacts table,
 * the Manager's primary responsibility is managing the <b>Users</b> (employees) of the system.
 * </p>
 * <p>
 * <b>Permissions defined in Project Specs:</b>
 * <ul>
 * <li><b>User Management:</b> List, Add (Employ), Update, and Delete (Fire) users.</li>
 * <li><b>Statistics:</b> View statistical data regarding contacts and user distribution.</li>
 * <li><b>Reports:</b> View salary reports.</li>
 * <li><b>System:</b> Change password, Undo administrative actions, and Logout.</li>
 * </ul>
 * </p>
 *
 * @author [Group Members Names Here]
 * @version 1.0
 * @see models.User
 * @see services.UserService
 * @see services.ContactService
 */
public class Manager extends User {

    /**
     * Constructs a new Manager user and sets the role identifier to "Manager".
     */
    public Manager() {
        setRole("Manager");
    }

    /**
     * Displays the interactive administrative menu specific to the Manager role.
     * <p>
     * This menu differs significantly from other roles as it interacts primarily with
     * the {@link UserService} to manage the workforce.
     * </p>
     * <p>
     * <b>Menu Options:</b>
     * <ol>
     * <li>List all users.</li>
     * <li>Add new user (Hire).</li>
     * <li>Update existing user details.</li>
     * <li>Delete/Fire user.</li>
     * <li>View Contact Statistics (e.g., total count, linkedIn usage).</li>
     * <li>View User Statistics (role distribution).</li>
     * <li>Show Salary Report.</li>
     * <li>Change Password.</li>
     * <li>Undo last user operation (e.g., undo firing a user).</li>
     * <li>Logout.</li>
     * @author Bora
     * </ol>
     * </p>
     */
    @Override
    public void showUserMenu() {
        UserService userService = new UserService();
        ContactService contactService = new ContactService();

        while (true) {
            MenuUtils.clear(); 
            MenuUtils.printMenuHeader("MANAGER MENU"); 
            MenuUtils.printCentered("Hello, " + getName() + " " + getSurname(), MenuUtils.CYAN);
            System.out.println();
            MenuUtils.printOption("1", "List all users");
            MenuUtils.printOption("2", "Add new user");
            MenuUtils.printOption("3", "Update existing user");
            MenuUtils.printOption("4", "Delete/Fire user");
            MenuUtils.printOption("5", "Contact Statistics");
            MenuUtils.printOption("6", "User Statistics");
            MenuUtils.printOption("7", "Show salary report");
            MenuUtils.printOption("8", "Change password");
            MenuUtils.printOption("9", "Undo last operation");
            MenuUtils.printOption("0", "Logout");

            MenuUtils.printPrompt(); 

            String choice = Input.scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    userService.listAllUsers(this);
                    MenuUtils.waitForEnter();
                    break;
                case "2":
                    userService.addUser(this);
                    MenuUtils.waitForEnter();
                    break;
                case "3":
                    userService.updateUser(this);
                    MenuUtils.waitForEnter();
                    break;
                case "4":
                    userService.deleteUser(this);
                    MenuUtils.waitForEnter();
                    break;
                case "5":
                    contactService.showStatistics();
                    MenuUtils.waitForEnter();
                    break;
                case "6":
                    userService.showUserStatistics(this);
                    MenuUtils.waitForEnter();
                    break;
                case "7":
                    userService.showSalaryReport(this);
                    MenuUtils.waitForEnter();
                    break;
                case "8":
                    changePassword();
                    MenuUtils.waitForEnter();
                    break;
                case "9":
                    userService.undoLastUserOperation();
                    MenuUtils.waitForEnter();
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