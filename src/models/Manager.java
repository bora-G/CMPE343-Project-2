package models;

import input.Input;
import services.ContactService;
import services.UserService;

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
     * </ol>
     * </p>
     */
    @Override
    public void showUserMenu() {
        UserService userService = new UserService();
        ContactService contactService = new ContactService();

        while (true) {
            System.out.println("\n=== MANAGER MENU ===");
            System.out.println("Hello, " + getName() + " " + getSurname());
            System.out.println("1) List all users");
            System.out.println("2) Add new user");
            System.out.println("3) Update existing user");
            System.out.println("4) Delete/Fire user");
            System.out.println("5) Contact Statistics");
            System.out.println("6) User Statistics");
            System.out.println("7) Show salary report");
            System.out.println("8) Change password");
            System.out.println("9) Undo last operation");
            System.out.println("0) Logout");
            System.out.print("Your choice: ");

            String choice = Input.scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    userService.listAllUsers(this);
                    break;
                case "2":
                    userService.addUser(this);
                    break;
                case "3":
                    userService.updateUser(this);
                    break;
                case "4":
                    userService.deleteUser(this);
                    break;
                case "5":
                    contactService.showStatistics();
                    break;
                case "6":
                    userService.showUserStatistics(this);
                    break;
                case "7":
                    userService.showSalaryReport(this);
                    break;
                case "8":
                    changePassword();
                    break;
                case "9":
                    userService.undoLastUserOperation();
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