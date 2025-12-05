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

    /**
     * Initializes the AuthService and sets up the UserRepository instance.
     */
    public AuthService() {
        this.userRepository = new UserRepository();
    }

    /**
     * Prompts the user to enter their username and password via console input.
     * After receiving the credentials, it forwards them to the {@link #login(String, String)}
     * method for authentication.
     *
     * @return the authenticated User if the credentials are correct,
     *         otherwise null.
     */
    public User loginWithPrompt() {

        System.out.print("Please enter your username: ");
        String username = Input.scanner.nextLine();

        System.out.print("Please enter your password: ");
        String password = Input.scanner.nextLine();

        return login(username, password);
    }

    /**
     * Attempts to authenticate a user using the provided username and plain text password.
     * <p>
     * The method hashes the given password, retrieves the stored user from the database,
     * and compares the stored hashed password with the newly hashed input.
     * If the user exists and the password matches, the method returns a role-specific
     * User object; otherwise, it returns {@code null}.
     *
     * @param username the username of the user attempting to log in; must not be null
     * @param passwordPlainText the plain text password entered by the user; must not be null
     * @return a fully mapped, role-specific {@link User} if authentication is successful;
     *         {@code null} if the username does not exist or the password is incorrect
     */
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

    /**
     * Converts a persisted generic User object into its corresponding
     * role-specific subclass (Tester, Junior, Senior, or Manager).
     * <p>
     * The method first creates a new instance of the correct role-based User
     * using {@link #instantiateRoleUser(String)}, then copies all shared fields
     * from the persisted user into this new role-specific instance.
     *
     * @param persistedUser the user object fetched from the database, containing all stored fields
     * @return a new User instance whose type matches the user's role
     */
    private User mapToRoleSpecificUser(User persistedUser) {
        User roleUser = instantiateRoleUser(persistedUser.getRole());
        copyUserState(persistedUser, roleUser);
        return roleUser;
    }

    /**
     * Creates and returns a new User subclass instance based on the given role.
     * <p>
     * If the role string contains keywords such as "tester", "junior", "senior",
     * or "manager", the corresponding subclass is instantiated.
     * If the role is null or unrecognized, the default returned instance is {@link Tester}.
     *
     * @param role the role string stored in the database for the user
     * @return a newly created User instance matching the specified role,
     *         or {@link Tester} if the role is null or unknown
     */
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
            return new Tester();
        }
    }

    /**
     * Copies all shared user attributes from the source User object
     * into the target User object.
     * <p>
     * This method is used when converting a generic persisted User into a
     * role-specific subclass instance, ensuring that all common fields
     * (ID, username, password hash, name, surname, role, timestamps)
     * remain consistent.
     *
     * @param source the original User object holding the current field values
     * @param target the User object into which values will be copied
     */
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

    /**
     * Hashes the provided plain text password using the SHA-256 algorithm.
     * <p>
     * The password is encoded in UTF-8, hashed with SHA-256, and returned
     * as a lowercase hexadecimal string.
     * If the hashing algorithm is not available (which should not happen
     * in standard Java), an {@link IllegalStateException} is thrown.
     *
     * @param passwordPlainText the raw password entered by the user
     * @return the SHA-256 hashed representation of the password as a hex string
     * @throws IllegalStateException if the SHA-256 algorithm is not supported by the JVM
     */
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

    /**
     * Interactively prompts the user to change their password through console input.
     * <p>
     * The method asks for the current password, the new password, and its confirmation.
     * It validates the new password format using {@link #isValidPassword(String)}, checks that
     * both new password entries match, and then delegates the actual update to
     * {@link #changePassword(User, String, String)}.
     *
     * <p>Informative messages are printed to the console depending on whether validation or
     * password update succeeds or fails.
     *
     * @param user the currently logged-in user requesting a password change
     * @return {@code true} if the password was successfully changed,
     *         {@code false} otherwise
     */
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

    /**
     * Updates the password of the specified user by verifying the current password
     * and replacing it with a new hashed password.
     * <p>
     * The method hashes the provided current password, compares it with the stored
     * password hash, and if they match, hashes the new password and attempts to update
     * it in the database using {@code userRepository.updatePassword()}.
     *
     * <p>If the update in the repository succeeds, the in-memory User object's password
     * hash is also updated to remain consistent.
     *
     * @param user the user whose password will be updated
     * @param currentPassword the user's current plain text password
     * @param newPassword the new plain text password to be stored after hashing
     * @return {@code true} if the password was successfully changed,
     *         {@code false} if the current password is incorrect or the update fails
     */
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

    /**
     * Validates whether the given password meets the application's password rules.
     * <p>
     * A valid password must:
     * <ul>
     *   <li>Contain only visible (non-whitespace) characters — based on {@code \p{Graph}}</li>
     *   <li>Be between 2 and 32 characters long</li>
     *   <li>Not be blank</li>
     * </ul>
     * The validation is performed using the regular expression {@code ^[\p{Graph}]{2,32}$}.
     *
     * @param password the plain text password to validate
     * @return {@code true} if the password matches the required format,
     *         {@code false} otherwise
     */
    private boolean isValidPassword(String password) {
        if (password.isBlank())
            return false;

        return password.matches("^[\\p{Graph}]{2,32}$");
    }

}
