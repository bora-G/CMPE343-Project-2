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

    private static final String NAME_REGEX = "^[a-zA-ZğüşöçıİĞÜŞÖÇ]+$";

    private boolean isValidNameString(String value) {
        return value != null && value.matches(NAME_REGEX);
    }

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

    public boolean addUser(User var1) {
        if (!this.isManager(var1)) {
            System.out.println("Access Denied: Only Managers can add users.");
            return false;
        } else {
            System.out.println("\n--- Add New User ---");

            // USERNAME (Q ile çık)
            String var2;
            while (true) {
                System.out.print("Username (or Q to cancel): ");
                var2 = Input.scanner.nextLine().trim();
                if (var2.equalsIgnoreCase("q")) {
                    System.out.println("User creation cancelled.");
                    return false;
                }
                if (var2.isEmpty()) {
                    System.out.println("Username cannot be empty.");
                } else {
                    break;
                }
            }

            // PASSWORD (Q ile çık)
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

            // NAME (sadece harf, TR karakter serbest, Q ile çık)
            String var4;
            while (true) {
                System.out.print("Name (letters only, Turkish allowed, or Q to cancel): ");
                var4 = Input.scanner.nextLine().trim();
                if (var4.equalsIgnoreCase("q")) {
                    System.out.println("User creation cancelled.");
                    return false;
                }
                if (!this.isValidNameString(var4)) {
                    System.out.println("Invalid name! Only letters are allowed (Turkish characters are ok).");
                } else {
                    break;
                }
            }

            // SURNAME (sadece harf, TR karakter serbest, Q ile çık)
            String var5;
            while (true) {
                System.out.print("Surname (letters only, Turkish allowed, or Q to cancel): ");
                var5 = Input.scanner.nextLine().trim();
                if (var5.equalsIgnoreCase("q")) {
                    System.out.println("User creation cancelled.");
                    return false;
                }
                if (!this.isValidNameString(var5)) {
                    System.out.println("Invalid surname! Only letters are allowed (Turkish characters are ok).");
                } else {
                    break;
                }
            }

            // ROLE (zorunlu, sadece tester/junior/senior/manager, Q ile çık)
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
                    // Rol adını düzgün formatta set edelim
                    var7.setRole(this.capitalize(var6));
                    break;
                }
            }

            // SALARY (0–1_000_000, sadece sayı, boş veya Q = girme)
            Double var8 = this.readSalaryOrNull(
                    "Salary (USD, 0 - 1,000,000, use . or , for decimals, leave empty or Q to skip): ");

            var7.setUsername(var2);
            var7.setPassword_hash(AuthService.hashPassword(var3));
            var7.setName(var4);
            var7.setSurname(var5);
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

                // NAME (opsiyonel, ama girilirse valid olmalı; Q = cancel)
                while (true) {
                    System.out.print("New Name (" + var3.getName() + "): ");
                    String var5 = Input.scanner.nextLine().trim();
                    if (var5.equalsIgnoreCase("q")) {
                        System.out.println("Update cancelled.");
                        return false;
                    }
                    if (var5.isEmpty()) {
                        break; // değişiklik yok
                    }
                    if (!this.isValidNameString(var5)) {
                        System.out.println("Invalid name! Only letters are allowed (Turkish characters are ok).");
                    } else {
                        var3.setName(var5);
                        break;
                    }
                }

                // SURNAME (opsiyonel, ama girilirse valid; Q = cancel)
                while (true) {
                    System.out.print("New Surname (" + var3.getSurname() + "): ");
                    String var6 = Input.scanner.nextLine().trim();
                    if (var6.equalsIgnoreCase("q")) {
                        System.out.println("Update cancelled.");
                        return false;
                    }
                    if (var6.isEmpty()) {
                        break; // değişiklik yok
                    }
                    if (!this.isValidNameString(var6)) {
                        System.out.println("Invalid surname! Only letters are allowed (Turkish characters are ok).");
                    } else {
                        var3.setSurname(var6);
                        break;
                    }
                }

                // ROLE (opsiyonel ama girilirse valid rol olmalı; Q = cancel)
                while (true) {
                    System.out.print("New Role (" + var3.getRole() + "): ");
                    String var7 = Input.scanner.nextLine().trim();
                    if (var7.equalsIgnoreCase("q")) {
                        System.out.println("Update cancelled.");
                        return false;
                    }
                    if (var7.isEmpty()) {
                        break; // değişiklik yok
                    }
                    if (this.isValidRole(var7)) {
                        var3.setRole(this.capitalize(var7));
                        break;
                    } else {
                        System.out.println("Invalid role. Valid roles: Tester, Junior, Senior, Manager.");
                    }
                }

                // SALARY (opsiyonel, boş veya Q = mevcut kalsın)
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