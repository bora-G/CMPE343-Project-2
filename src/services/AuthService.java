package services;

import models.Junior;
import models.Manager;
import models.Senior;
import models.Tester;
import models.User;
import repository.UserRepository;

import input.Input;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class AuthService {

    private final UserRepository userRepository;

    public AuthService() {
        this.userRepository = new UserRepository();
    }

    public User loginWithPrompt() {

        System.out.print("Please enter your username: ");
        String username = Input.scanner.nextLine();

        System.out.print("Please enter your password: ");
        String password = Input.scanner.nextLine();

        return login(username, password);
    }

    public User login(String username, String passwordPlainText) {
        if (username == null || passwordPlainText == null) {
            return null;
        }

        String hashedPassword = hashPassword(passwordPlainText);
        User persistedUser = userRepository.findByUsername(username);
        if (persistedUser == null) {
            return null;
        }

        if (persistedUser.getPassword_hash() == null ||
                !persistedUser.getPassword_hash().equals(hashedPassword)) {
            return null;
        }

        return mapToRoleSpecificUser(persistedUser);
    }

    private User mapToRoleSpecificUser(User persistedUser) {
        User roleUser = instantiateRoleUser(persistedUser.getRole());
        copyUserState(persistedUser, roleUser);
        return roleUser;
    }

    private User instantiateRoleUser(String role) {
        if (role == null) {
            return new Tester();
        }

        String r = role.toLowerCase().trim();

        if (r.contains("tester")) {
            return new Tester();
        } else if (r.contains("junior")) {
            return new Junior();
        } else if (r.contains("senior")) {
            return new Senior();
        } else if (r.contains("manager")) {
            return new Manager();
        } else {
            // tanınmayan rol → default Tester
            return new Tester();
        }
    }

    private void copyUserState(User source, User target) {
        target.setUserId(source.getUserId());
        target.setUsername(source.getUsername());
        target.setPassword_hash(source.getPassword_hash());
        target.setName(source.getName());
        target.setSurname(source.getSurname());
        target.setRole(source.getRole());
        target.setCreated_at(source.getCreated_at());
        target.setUpdated_at(source.getUpdated_at());
    }

public static String hashPassword(String passwordPlainText) {
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

    public boolean changePasswordWithPrompt(User user) {

        System.out.print("Enter your current password: ");
        String current = Input.scanner.nextLine();

        System.out.print("Enter your new password: ");
        String newPass = Input.scanner.nextLine();

        System.out.print("Confirm your new password: ");
        String confirm = Input.scanner.nextLine();

        if (!newPass.equals(confirm)) {
            System.out.println("New passwords do not match!");
            return false;
        }

        if (!isValidPassword(newPass)) {
            System.out.println("Password must be 2–32 characters, no spaces, only visible characters.");
            return false;
        }

        boolean success = changePassword(user, current, newPass);

        if (!success) {
            System.out.println("Current password is incorrect!");
        } else {
            System.out.println("Password updated successfully.");
        }

        return success;
    }

    public boolean changePassword(User user, String currentPassword, String newPassword) {
        String currentHash = hashPassword(currentPassword);

        if (!currentHash.equals(user.getPassword_hash())) {
            return false;
        }

        String newHash = hashPassword(newPassword);
        boolean success = userRepository.updatePassword(user.getUserId(), newHash);

        if (success)
            user.setPassword_hash(newHash);

        return success;
    }

    private boolean isValidPassword(String password) {
        if (password.isBlank())
            return false;

        return password.matches("^[\\p{Graph}]{2,32}$");
    }

}
