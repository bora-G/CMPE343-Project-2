package services;

import input.Input;
import models.*;
import repository.UserRepository;

import services.AuthService;

import Undo.UndoManager;
import Undo.AddUserCommand;
import Undo.UpdateUserCommand;
import Undo.DeleteUserCommand;

import java.util.List;

public class UserService {

    /**
     * Repository responsible for performing all database operations
     * related to User entities.
     */
    private final UserRepository userRepository;

    /**
     * Manages undoable actions performed within the UserService.
     * Stores operations that can later be reversed by the user.
     */
    private final UndoManager undoManager;

    /**
     * Regular expression used to validate name fields.
     * <p>
     * Allows only alphabetic characters, including Turkish letters:
     * a-z, A-Z, ğ, ü, ş, ö, ç, ı, İ, Ğ, Ü, Ş, Ö, Ç.
     * </p>
     */
    private static final String NAME_REGEX = "^[a-zA-ZğüşöçıİĞÜŞÖÇ]+$";

    /**
     * Creates a new instance of UserService and initializes the required
     * repository and undo manager components.
     *
     * <p>This constructor prepares the service for performing user-related
     * operations such as creating, updating, deleting users, and managing
     * undoable actions.</p>
     */
    public UserService() {
        this.userRepository = new UserRepository();
        this.undoManager = new UndoManager();
    }

    /**
     * Retrieves and prints a formatted list of all users in the system.
     * <p>
     * Only users with the Manager role are allowed to execute this method.
     * If the acting user does not have Manager privileges, access is denied
     * and the method returns {@code null}.
     * </p>
     *
     * @param actingUser the user attempting to perform the operation; must have Manager role
     * @return a list of all users if the caller is a Manager, otherwise {@code null}
     */
    public List<User> listAllUsers(User actingUser) {
        if (!isManager(actingUser)) {
            System.out.println("Access Denied: Only Managers can list users.");
            return null;
        }

        List<User> users = userRepository.findAll();
        System.out.println("\n--- User List ---");
        System.out.printf("%-5s %-15s %-15s %-15s %-10s%n", "ID", "Username", "Name", "Surname", "Role");
        for (User user : users) {
            System.out.printf("%-5d %-15s %-15s %-15s %-10s%n",
                    user.getUserId(),
                    user.getUsername(),
                    user.getName(),
                    user.getSurname(),
                    user.getRole());
        }
        System.out.println("-----------------");
        return users;
    }

    /**
     * Interactively creates and adds a new user to the system.
     * <p>
     * This operation is restricted to users with the Manager role. The method
     * prompts the acting user to input all required fields (username, password,
     * name, surname, role, and optionally salary). Each field is validated, and
     * the acting user may cancel the process at any step by entering "Q".
     * </p>
     *
     * <p>
     * If the user is successfully created and inserted into the database, the
     * operation is recorded in the {@link UndoManager} as an undoable action.
     * </p>
     *
     * @param var1 the user attempting to add a new user; must have Manager privileges
     * @return {@code true} if the user was successfully added,
     *         {@code false} if cancelled or failed
     */
    public boolean addUser(User var1) {
        if (!this.isManager(var1)) {
            System.out.println("Access Denied: Only Managers can add users.");
            return false;
        } else {
            System.out.println("\n--- Add New User ---");

            String username;
            while (true) {
                System.out.print("Username (or Q to cancel): ");
                username = Input.scanner.nextLine().trim();

                if (username.equalsIgnoreCase("q")) {
                    System.out.println("Add user cancelled.");
                    return false;
                }

                if (username.isBlank()) {
                    System.out.println("Error: Username is required!");
                    continue;
                }

                if (userRepository.existsByUsername(username)) {
                    System.out.println("This username is already taken. Please choose another one.");
                    continue;
                }

                break;
            }

            String var3;
            while (true) {
                System.out.print("Password (or Q to cancel): ");
                var3 = Input.scanner.nextLine().trim();
                if (var3.equalsIgnoreCase("q")) {
                    System.out.println("User creation cancelled.");
                    return false;
                }
                if (var3.isEmpty()) {
                    System.out.println("Password cannot be empty.");
                } else {
                    break;
                }
            }

            String name;
            while (true) {
                System.out.print("Name (Required, letters only, or Q to cancel): ");
                name = Input.scanner.nextLine().trim();

                if (name.equalsIgnoreCase("q")) {
                    System.out.println("Add user cancelled.");
                    return false;
                }

                if (name.isBlank() || !name.matches(NAME_REGEX)) {
                    System.out.println("Invalid name. Only letters (Turkish characters allowed) are accepted.");
                    continue;
                }

                break;
            }

            String surname;
            while (true) {
                System.out.print("Surname (Required, letters only, or Q to cancel): ");
                surname = Input.scanner.nextLine().trim();

                if (surname.equalsIgnoreCase("q")) {
                    System.out.println("Add user cancelled.");
                    return false;
                }

                if (surname.isBlank() || !surname.matches(NAME_REGEX)) {
                    System.out.println("Invalid surname. Only letters (Turkish characters allowed) are accepted.");
                    continue;
                }

                break;
            }

            String var6;
            User var7;
            while (true) {
                System.out.print("Role (Tester, Junior, Senior, Manager, or Q to cancel): ");
                var6 = Input.scanner.nextLine().trim();
                if (var6.equalsIgnoreCase("q")) {
                    System.out.println("User creation cancelled.");
                    return false;
                }
                var7 = this.instantiateRoleUser(var6);
                if (var7 == null) {
                    System.out.println("Invalid role! Please enter one of: Tester, Junior, Senior, Manager.");
                } else {
                    var7.setRole(this.capitalize(var6));
                    break;
                }
            }

            Double var8 = this.readSalaryOrNull(
                    "Annual Salary (USD, 0 - 1,000,000, use . or , for decimals, leave empty or Q to skip): ");

            var7.setUsername(username);
            var7.setPassword_hash(AuthService.hashPassword(var3));
            var7.setName(name);
            var7.setSurname(surname);
            var7.setSalary(var8);

            if (this.userRepository.insert(var7)) {
                System.out.println("User added successfully.");
                this.undoManager.push(new AddUserCommand(var7.getUserId(), this.userRepository));
                return true;
            } else {
                System.out.println("Failed to add user.");
                return false;
            }
        }
    }

    /**
     * Creates and returns a shallow copy of the given {@link User} object.
     * <p>
     * The method copies all basic fields such as ID, username, password hash,
     * name, surname, role, salary, and creation timestamp. The returned object
     * is always instantiated as a {@link Manager}, regardless of the original
     * user's role.
     * </p>
     *
     * @param u the user to be copied; may be {@code null}
     * @return a new {@link User} object containing the copied data,
     *         or {@code null} if the input was null
     */
    private User copyUser(User u) {
        if (u == null)
            return null;
        User copy = new Manager();
        copy.setUserId(u.getUserId());
        copy.setUsername(u.getUsername());
        copy.setPassword_hash(u.getPassword_hash());
        copy.setName(u.getName());
        copy.setSurname(u.getSurname());
        copy.setRole(u.getRole());
        copy.setSalary(u.getSalary());
        copy.setCreated_at(u.getCreated_at());
        return copy;
    }

    /**
     * Interactively updates an existing user in the system.
     * <p>
     * This operation is restricted to users with the Manager role. The method
     * first asks for the username of the user to be updated, then allows changing
     * the name, surname, role and salary. For each field, the current value is
     * shown and the user can:
     * </p>
     * <ul>
     *   <li>Press Enter to keep the existing value,</li>
     *   <li>Type a new value to update the field,</li>
     *   <li>Type {@code Q} at any prompt to cancel the entire update.</li>
     * </ul>
     *
     * <p>
     * If the update succeeds, the previous state of the user is stored in the
     * {@link UndoManager} as an {@link UpdateUserCommand} so the change can be undone later.
     * </p>
     *
     * @param var1 the user attempting to perform the update; must have Manager privileges
     * @return {@code true} if the user was successfully updated,
     *         {@code false} if cancelled, not found, not authorized, or update failed
     */
    public boolean updateUser(User var1) {
        if (!this.isManager(var1)) {
            System.out.println("Access Denied: Only Managers can update users.");
            return false;
        } else {
            System.out.println("\n--- Update User ---");
            System.out.print("Enter username of the user to update (or Q to cancel): ");
            String var2 = Input.scanner.nextLine().trim();
            if (var2.equalsIgnoreCase("q")) {
                System.out.println("Update cancelled.");
                return false;
            }

            User var3 = this.userRepository.findByUsername(var2);
            if (var3 == null) {
                System.out.println("User not found.");
                return false;
            } else {
                User var4 = this.copyUser(var3);
                System.out.println("Updating user: " + var3.getUsername());
                System.out.println("Press Enter to skip a field. Type Q to cancel update.");

                while (true) {
                    System.out.print("New Name (" + var3.getName() + "): ");
                    String var5 = Input.scanner.nextLine().trim();
                    if (var5.equalsIgnoreCase("q")) {
                        System.out.println("Update cancelled.");
                        return false;
                    }
                    if (var5.isEmpty()) {
                        break;
                    }
                    if (!var5.matches(NAME_REGEX)) {
                        System.out.println("Invalid name! Only letters are allowed (Turkish characters are ok).");
                    } else {
                        var3.setName(var5);
                        break;
                    }
                }

                while (true) {
                    System.out.print("New Surname (" + var3.getSurname() + "): ");
                    String var6 = Input.scanner.nextLine().trim();
                    if (var6.equalsIgnoreCase("q")) {
                        System.out.println("Update cancelled.");
                        return false;
                    }
                    if (var6.isEmpty()) {
                        break;
                    }
                    if (!var6.matches(NAME_REGEX)) {
                        System.out.println("Invalid surname! Only letters are allowed (Turkish characters are ok).");
                    } else {
                        var3.setSurname(var6);
                        break;
                    }
                }

                while (true) {
                    System.out.print("New Role (" + var3.getRole() + "): ");
                    String var7 = Input.scanner.nextLine().trim();
                    if (var7.equalsIgnoreCase("q")) {
                        System.out.println("Update cancelled.");
                        return false;
                    }
                    if (var7.isEmpty()) {
                        break;
                    }
                    if (this.isValidRole(var7)) {
                        var3.setRole(this.capitalize(var7));
                        break;
                    } else {
                        System.out.println("Invalid role. Valid roles: Tester, Junior, Senior, Manager.");
                    }
                }

                Double var8 = this.readSalaryOrNull(
                        "New Salary (" + var3.getSalary()
                                + ") - leave empty or Q to keep current: ");
                if (var8 != null) {
                    var3.setSalary(var8);
                }

                if (this.userRepository.update(var3)) {
                    System.out.println("User updated successfully.");
                    this.undoManager.push(new UpdateUserCommand(var4, this.userRepository));
                    return true;
                } else {
                    System.out.println("Failed to update user.");
                    return false;
                }
            }
        }
    }

    /**
     * Deletes an existing user from the system after confirmation.
     * <p>
     * This operation is restricted to users with the Manager role. The acting user
     * is prompted to enter the username of the user to delete. The method checks:
     * </p>
     * <ul>
     *   <li>whether the target user exists,</li>
     *   <li>whether the acting user is not trying to delete themselves,</li>
     *   <li>whether the deletion is confirmed by typing {@code yes}.</li>
     * </ul>
     *
     * <p>
     * If the deletion succeeds, a snapshot of the deleted user is stored in the
     * {@link UndoManager} via a {@link DeleteUserCommand} so the operation can
     * be undone later.
     * </p>
     *
     * @param actingUser the user attempting to delete another user; must have Manager privileges
     * @return {@code true} if the user was successfully deleted,
     *         {@code false} if not authorized, cancelled, user not found, or deletion failed
     */
    public boolean deleteUser(User actingUser) {
        if (!isManager(actingUser)) {
            System.out.println("Access Denied: Only Managers can delete users.");
            return false;
        }

        System.out.println("\n--- Delete User ---");
        System.out.print("Enter username to delete: ");
        String username = Input.scanner.nextLine();

        User user = userRepository.findByUsername(username);
        if (user == null) {
            System.out.println("User not found.");
            return false;
        }

        if (user.getUsername().equals(actingUser.getUsername())) {
            System.out.println("You cannot delete yourself.");
            return false;
        }
        User deletedSnapshot = copyUser(user);

        System.out.print("Are you sure you want to delete " + username + "? (yes/no): ");
        String confirm = Input.scanner.nextLine();
        if (!confirm.equalsIgnoreCase("yes")) {
            System.out.println("Deletion cancelled.");
            return false;
        }

        if (userRepository.delete(user.getUserId())) {
            System.out.println("User deleted successfully.");
            undoManager.push(new DeleteUserCommand(deletedSnapshot, userRepository));
            return true;
        } else {
            System.out.println("Failed to delete user.");
            return false;
        }
    }

    /**
     * Checks whether the given user has the Manager role.
     *
     * @param user the user to check; may be {@code null}
     * @return {@code true} if the user exists and has role "Manager",
     *         {@code false} otherwise
     */
    private boolean isManager(User user) {

        return user != null && "Manager".equalsIgnoreCase(user.getRole());
    }

    /**
     * Creates a new {@link User} instance based on the provided role name.
     * <p>
     * Accepted roles: Tester, Junior, Senior, Manager (case-insensitive).
     * Returns {@code null} for invalid or unsupported role names.
     * </p>
     *
     * @param role the role name to instantiate; may be {@code null}
     * @return a new User instance matching the role,
     *         or {@code null} if the role is invalid
     */
    private User instantiateRoleUser(String role) {
        if (role == null)
            return null;
        switch(role.toLowerCase()) {
            case "tester":
                return new Tester();
            case "junior":
                return new Junior();
            case "senior":
                return new Senior();
            case "manager":
                return new Manager();
            default:
                return null;
        }
    }

    /**
     * Displays statistical information about all users in the system.
     * <p>
     * Only Managers are authorized to view statistics. The method counts how many
     * users belong to each role (Tester, Junior, Senior, Manager) and prints the results
     * in a formatted output.
     * </p>
     *
     * @param actingUser the user attempting to view statistics; must have Manager privileges
     */
    public void showUserStatistics(User actingUser) {
        if (actingUser == null || !"Manager".equalsIgnoreCase(actingUser.getRole())) {
            System.out.println("Access Denied: Only Managers can view user statistics.");
            return;
        }

        List<User> users = userRepository.findAll();

        System.out.println("\n=== USER STATISTICS ===");
        System.out.println("Total users: " + users.size());

        int testers = 0;
        int juniors = 0;
        int seniors = 0;
        int managers = 0;

        for (User u : users) {
            String role = u.getRole();
            if (role == null)
                continue;

            String r = role.toLowerCase().trim();

            if (r.contains("tester")) {
                testers++;
            } else if (r.contains("junior")) {
                juniors++;
            } else if (r.contains("senior")) {
                seniors++;
            } else if (r.contains("manager")) {
                managers++;
            }
        }

        System.out.println("Tester:  " + testers);
        System.out.println("Junior:  " + juniors);
        System.out.println("Senior:  " + seniors);
        System.out.println("Manager: " + managers);
        System.out.println("=========================\n");
    }

    /**
     * Prints a formatted salary report for all users.
     * <p>
     * Only Managers are allowed to view the salary report. The report includes
     * each user's username, role, annual salary, and monthly salary. If a user's
     * salary is {@code null}, it is displayed as 0.
     * </p>
     *
     * @param actingUser the user requesting the salary report; must have Manager privileges
     */
    public void showSalaryReport(User actingUser) {
        if (!isManager(actingUser)) {
            System.out.println("Access Denied: Only Managers can see salary report.");
            return;
        }

        List<User> users = userRepository.findAll();
        System.out.println("\n=== USER SALARY REPORT (USD) ===");

        System.out.printf("%-15s %-15s %-10s %-10s%n", "Username", "Role", "Salary", "Monthly");

        for (User u : users) {
            Double salary = u.getSalary();
            System.out.printf("%-15s %-15s %-10.2f %-10.2f%n",
                    u.getUsername(),
                    u.getRole(),
                    salary == null ? 0 : salary,
                    salary == null ? 0 : salary / 12);
        }
        System.out.println("=================================");
    }

    /**
     * Reads a salary input from the console, validating the value and allowing skips.
     * <p>
     * The user may:
     * </p>
     * <ul>
     *   <li>Press Enter to skip salary input (returns {@code null})</li>
     *   <li>Type {@code Q} to skip salary input (returns {@code null})</li>
     *   <li>Enter a valid number between 0 and 1,000,000 (supports '.' or ',' decimals)</li>
     * </ul>
     *
     * <p>
     * The method ensures that the salary is within valid numeric bounds and handles
     * invalid formats gracefully.
     * </p>
     *
     * @param var1 the prompt message displayed before reading salary input
     * @return a valid salary as {@code Double}, or {@code null} if skipped
     */
    private Double readSalaryOrNull(String var1) {
        while (true) {
            System.out.print(var1);
            String var2 = Input.scanner.nextLine().trim();

            if (var2.equalsIgnoreCase("q")) {
                System.out.println("Salary input skipped.");
                return null;
            }

            if (var2.isEmpty()) {
                return null;
            }

            var2 = var2.replace(',', '.');

            try {
                double var3 = Double.parseDouble(var2);
                if (var3 < 0.0) {
                    System.out.println("Salary cannot be negative. Please enter 0 or a positive value.");
                } else if (var3 > 1_000_000.0) {
                    System.out.println("Salary is too high. Please enter a value up to 1,000,000.");
                } else {
                    return var3;
                }
            } catch (NumberFormatException var5) {
                System.out.println(
                        "Invalid number format. Please enter a valid numeric salary (examples: 2500 or 2500.50).");
            }
        }
    }

    /**
     * Checks whether the given role string represents a valid user role.
     * <p>
     * Valid roles (case-insensitive):
     * Tester, Junior, Senior, Manager.
     * </p>
     *
     * @param role the input role string to validate; may be {@code null}
     * @return {@code true} if the role is one of the supported values,
     *         {@code false} otherwise
     */
    private boolean isValidRole(String role) {
        if (role == null)
            return false;
        String r = role.toLowerCase();
        return r.equals("tester") || r.equals("junior") || r.equals("senior") || r.equals("manager");
    }

    /**
     * Capitalizes the first letter of the given string and lowercases the rest.
     * <p>
     * Example:
     * <ul>
     *   <li>"manager" → "Manager"</li>
     *   <li>"JUNIOR" → "Junior"</li>
     * </ul>
     * </p>
     *
     * @param str the string to transform; may be {@code null}
     * @return the capitalized form of the string,
     *         or the original value if {@code null} or empty
     */
    private String capitalize(String str) {
        if (str == null || str.isEmpty())
            return str;
        return str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase();
    }

    /**
     * Reverts the most recent user-related operation, if available.
     * <p>
     * This method delegates to the {@link UndoManager}, which maintains a stack
     * of undoable actions such as adding, updating, or deleting users.
     * If no undoable action exists, the call has no effect.
     * </p>
     */
    public void undoLastUserOperation() {

        undoManager.undoLast();
    }
}