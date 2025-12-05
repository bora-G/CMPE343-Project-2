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

    private final UserRepository userRepository;
    private final UndoManager undoManager;

    public UserService() {
        this.userRepository = new UserRepository();
        this.undoManager = new UndoManager();
    }

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

    public boolean addUser(User actingUser) {
        if (!isManager(actingUser)) {
            System.out.println("Access Denied: Only Managers can add users.");
            return false;
        }

        System.out.println("\n--- Add New User ---");
        System.out.print("Username: ");
        String username = Input.scanner.nextLine();
        while (username.isBlank()) {
            System.out.println("Username cannot be empty.");
            System.out.println("Please enter a valid username: ");
            username = Input.scanner.nextLine();
        }

        System.out.print("Password: ");
        String password = Input.scanner.nextLine();
        while (password.isBlank()) {
            System.err.println("Password cannot be empty!");
            System.err.println("Please enter a valid password: ");
            password = Input.scanner.nextLine();
        }

        System.out.print("Name: ");
        String name = Input.scanner.nextLine();
        while (name.isBlank()) 
        {

            System.out.println("Name cannot be empty.");
            System.out.println("Please enter a valid name: ");
            name = Input.scanner.nextLine();

        }

        System.out.print("Surname: ");
        String surname = Input.scanner.nextLine();
        while (surname.isBlank()) 
        {

            System.out.println("Surname cannot be empty.");
            System.out.println("Please enter a valid surname: ");
            surname = Input.scanner.nextLine();

        }

        System.out.print("Role (Tester, Junior, Senior, Manager): ");
        String role = Input.scanner.nextLine();

        User newUser = instantiateRoleUser(role);
        if (newUser == null) {
            System.out.println("Invalid role! User creation cancelled.");
            return false;
        }
        Double salary = readSalaryOrNull("Salary (USD, 0 - 1,000,000, use . or , for decimals, leave empty to skip): ");

        newUser.setUsername(username);
        newUser.setPassword_hash(AuthService.hashPassword(password));
        newUser.setName(name);
        newUser.setSurname(surname);
        newUser.setSalary(salary);

        if (userRepository.insert(newUser)) {
            System.out.println("User added successfully.");
            undoManager.push(new AddUserCommand(newUser.getUserId(), userRepository));
            return true;
        } else {
            System.out.println("Failed to add user.");
            return false;
        }
    }

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

    public boolean updateUser(User actingUser) {
        if (!isManager(actingUser)) {
            System.out.println("Access Denied: Only Managers can update users.");
            return false;
        }

        System.out.println("\n--- Update User ---");
        System.out.print("Enter username of the user to update: ");
        String targetUsername = Input.scanner.nextLine();

        User userToUpdate = userRepository.findByUsername(targetUsername);
        if (userToUpdate == null) {
            System.out.println("User not found.");
            return false;
        }

        User oldSnapshot = copyUser(userToUpdate);

        System.out.println("Updating user: " + userToUpdate.getUsername());
        System.out.println("Press Enter to skip a field.");

        System.out.print("New Name (" + userToUpdate.getName() + "): ");
        String name = Input.scanner.nextLine();
        if (!name.isBlank())
            userToUpdate.setName(name);

        System.out.print("New Surname (" + userToUpdate.getSurname() + "): ");
        String surname = Input.scanner.nextLine();
        if (!surname.isBlank())
            userToUpdate.setSurname(surname);

        System.out.print("New Role (" + userToUpdate.getRole() + "): ");
        String role = Input.scanner.nextLine();
        if (!role.isEmpty()) {
            if (isValidRole(role)) {
                userToUpdate.setRole(capitalize(role));
            } else {
                System.out.println("Invalid role. Role not updated.");
            }
        }
        System.out.print("New Salary (" + userToUpdate.getSalary() + ") - leave empty to keep current: ");
        Double newSalary = readSalaryOrNull("");
        if (newSalary != null) {
            userToUpdate.setSalary(newSalary);
        }

        if (userRepository.update(userToUpdate)) {
            System.out.println("User updated successfully.");

            undoManager.push(new UpdateUserCommand(oldSnapshot, userRepository));

            return true;
        } else {
            System.out.println("Failed to update user.");
            return false;
        }
    }

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

    private boolean isManager(User user) {
        return user != null && "Manager".equalsIgnoreCase(user.getRole());
    }

    private User instantiateRoleUser(String role) {
        if (role == null)
            return null;
        switch (role.toLowerCase()) {
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

    private Double readSalaryOrNull(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = Input.scanner.nextLine().trim();

            if (input.isEmpty()) {
                return null;
            }

            input = input.replace(',', '.');

            try {
                double value = Double.parseDouble(input);

                if (value < 0) {
                    System.out.println("Salary cannot be negative. Please enter 0 or a positive value.");
                    continue;
                }

                if (value > 1_000_000) {
                    System.out.println("Salary is too high. Please enter a value up to 1,000,000.");
                    continue;
                }

                return value;
            } catch (NumberFormatException ex) {
                System.out.println(
                        "Invalid number format. Please enter a valid numeric salary (examples: 2500 or 2500.50).");
            }
        }
    }

    private boolean isValidRole(String role) {
        if (role == null)
            return false;
        String r = role.toLowerCase();
        return r.equals("tester") || r.equals("junior") || r.equals("senior") || r.equals("manager");
    }

    private String capitalize(String str) {
        if (str == null || str.isEmpty())
            return str;
        return str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase();
    }

    public void undoLastUserOperation() {
        undoManager.undoLast();
    }
}