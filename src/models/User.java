package models;

import java.sql.Date;
import services.AuthService;

/**
 * Abstract base class representing a system user.
 * <p>
 * This class serves as the foundation for the Role-Based Access Control (RBAC) system.
 * It encapsulates common attributes shared by all users (such as username, password hash,
 * and personal details) as required by the database schema.
 * </p>
 * <p>
 * <b>OOP Principles Applied:</b>
 * <ul>
 * <li><b>Abstraction:</b> Defines the abstract method {@link #showUserMenu()} which forces
 * specific role implementations to define their own interaction logic.</li>
 * <li><b>Encapsulation:</b> Protects user data through private fields and public getters/setters.</li>
 * <li><b>Inheritance:</b> Serves as the parent for {@link Tester}, {@link Junior}, {@link Senior}, and {@link Manager}.</li>
 * </ul>
 * </p>
 *
 * @author [Group Members Names Here]
 * @version 1.0
 * @see models.Tester
 * @see models.Junior
 * @see models.Senior
 * @see models.Manager
 */
public abstract class User {

    private int userId;
    private String username;
    /**
     * Stores the password in an encrypted (hashed) format, never in plain text.
     */
    private String password_hash;
    private String name;
    private String surname;
    private String role;
    private Date created_at;
    private Date updated_at;
    private Double salary;

    /**
     * Abstract method that must be implemented by all subclasses to display their
     * specific role-based menu.
     * <p>
     * This method is the key to the system's polymorphism; the {@link services.AuthService}
     * returns a generic {@code User} object, but calling this method triggers the
     * specific behavior of the actual subclass (e.g., Manager menu vs. Tester menu).
     * </p>
     */
    public abstract void showUserMenu();

    /**
     * Initiates the password change process for the current user.
     * <p>
     * Delegates the logic to the {@link services.AuthService}. This is a concrete method
     * available to all user roles.
     * </p>
     */
    public void changePassword() {
        AuthService authService = new AuthService();
        boolean success = authService.changePasswordWithPrompt(this);

        if (!success) {
            System.out.println("Password change failed.");
        }
    }

    /**
     * Gets the user's salary.
     *
     * @return The salary amount.
     */
    public Double getSalary() {
        return salary;
    }

    /**
     * Sets the user's salary.
     *
     * @param salary The salary amount to set.
     */
    public void setSalary(Double salary) {
        this.salary = salary;
    }

    /**
     * Logs the user out of the system.
     * <p>
     * Displays a logout message. In a console application, this typically results
     * in returning to the main login loop.
     * </p>
     */
    public void logout() {
         System.out.println("Logging out...");
    }

    /**
     * Gets the unique user ID.
     * @return The user ID.
     */
    public int getUserId() {
        return userId;
    }

    /**
     * Sets the unique user ID.
     * @param userId The user ID to set.
     */
    public void setUserId(int userId) {
        this.userId = userId;
    }

    /**
     * Gets the timestamp of the last update to the user record.
     * @return The update date.
     */
    public Date getUpdated_at() {
        return updated_at;
    }

    /**
     * Sets the timestamp of the last update.
     * @param updated_at The update date to set.
     */
    public void setUpdated_at(Date updated_at) {
        this.updated_at = updated_at;
    }

    /**
     * Gets the timestamp of when the user record was created.
     * @return The creation date.
     */
    public Date getCreated_at() {
        return created_at;
    }

    /**
     * Sets the timestamp of creation.
     * @param created_at The creation date to set.
     */
    public void setCreated_at(Date created_at) {
        this.created_at = created_at;
    }

    /**
     * Gets the role name of the user (e.g., "Manager", "Tester").
     * @return The role string.
     */
    public String getRole() {
        return role;
    }

    /**
     * Sets the role name of the user.
     * @param role The role string to set.
     */
    public void setRole(String role) {
        this.role = role;
    }

    /**
     * Gets the surname of the user.
     * @return The surname.
     */
    public String getSurname() {
        return surname;
    }

    /**
     * Sets the surname of the user.
     * @param surname The surname to set.
     */
    public void setSurname(String surname) {
        this.surname = surname;
    }

    /**
     * Gets the first name of the user.
     * @return The first name.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the first name of the user.
     * @param name The first name to set.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the encrypted password hash.
     * @return The password hash.
     */
    public String getPassword_hash() {
        return password_hash;
    }

    /**
     * Sets the encrypted password hash.
     * @param password_hash The hash to set.
     */
    public void setPassword_hash(String password_hash) {
        this.password_hash = password_hash;
    }

    /**
     * Gets the username used for login.
     * @return The username.
     */
    public String getUsername() {
        return username;
    }

    /**
     * Sets the username.
     * @param username The username to set.
     */
    public void setUsername(String username) {
        this.username = username;
    }
}