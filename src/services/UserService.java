package services;

import input.Input;
import models.*;
import repository.UserRepository;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

public class UserService {

    private final UserRepository userRepository;

    public UserService() {
        this.userRepository = new UserRepository();
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

        System.out.print("Password: ");
        String password = Input.scanner.nextLine();

        System.out.print("Name: ");
        String name = Input.scanner.nextLine();

        System.out.print("Surname: ");
        String surname = Input.scanner.nextLine();

        System.out.print("Role (Tester, Junior, Senior, Manager): ");
        String role = Input.scanner.nextLine();

        User newUser = instantiateRoleUser(role);
        if (newUser == null) {
            System.out.println("Invalid role! User creation cancelled.");
            return false;
        }

        newUser.setUsername(username);
        newUser.setPassword_hash(hashPassword(password));
        newUser.setName(name);
        newUser.setSurname(surname);

        if (userRepository.insert(newUser)) {
            System.out.println("User added successfully.");
            return true;
        } else {
            System.out.println("Failed to add user.");
            return false;
        }
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

        System.out.println("Updating user: " + userToUpdate.getUsername());
        System.out.println("Press Enter to skip a field.");

        System.out.print("New Name (" + userToUpdate.getName() + "): ");
        String name = Input.scanner.nextLine();
        if (!name.isEmpty()) userToUpdate.setName(name);

        System.out.print("New Surname (" + userToUpdate.getSurname() + "): ");
        String surname = Input.scanner.nextLine();
        if (!surname.isEmpty()) userToUpdate.setSurname(surname);

        System.out.print("New Role (" + userToUpdate.getRole() + "): ");
        String role = Input.scanner.nextLine();
        if (!role.isEmpty()) {
             if (isValidRole(role)) {
                 userToUpdate.setRole(capitalize(role));
             } else {
                 System.out.println("Invalid role. Role not updated.");
             }
        }

        if (userRepository.update(userToUpdate)) {
            System.out.println("User updated successfully.");
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

        System.out.print("Are you sure you want to delete " + username + "? (yes/no): ");
        String confirm = Input.scanner.nextLine();
        if (!confirm.equalsIgnoreCase("yes")) {
            System.out.println("Deletion cancelled.");
            return false;
        }

        if (userRepository.delete(user.getUserId())) {
            System.out.println("User deleted successfully.");
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
        if (role == null) return null;
        switch (role.toLowerCase()) {
            case "tester": return new Tester();
            case "junior": return new Junior();
            case "senior": return new Senior();
            case "manager": return new Manager();
            default: return null;
        }
    }
    
    private boolean isValidRole(String role) {
        if (role == null) return false;
        String r = role.toLowerCase();
        return r.equals("tester") || r.equals("junior") || r.equals("senior") || r.equals("manager");
    }

    private String capitalize(String str) {
        if (str == null || str.isEmpty()) return str;
        return str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase();
    }

    private String hashPassword(String passwordPlainText) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(passwordPlainText.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder(hash.length * 2);
            for (byte b : hash) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("Required hash algorithm missing", e);
        }
    }
}
