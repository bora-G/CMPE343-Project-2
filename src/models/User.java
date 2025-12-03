package models;

import java.sql.Date;

import services.AuthService;

public abstract class User {

    private int userId;
    private String username;
    private String password_hash;
    private String name;
    private String surname;
    private String role;
    private Date created_at;
    private Date updated_at;

    public abstract void showUserMenu();

    public void changePassword() {
        AuthService authService = new AuthService();
        boolean success = authService.changePasswordWithPrompt(this);

        if (!success) {
            System.out.println("Password change failed.");
        }
    }
    private Double salary;

    public Double getSalary() {
        return salary;
    }

    public void setSalary(Double salary) {
        this.salary = salary;
    }

    public void logout() {
         System.out.println("Logging out...");
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public Date getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(Date updated_at) {
        this.updated_at = updated_at;
    }

    public Date getCreated_at() {
        return created_at;
    }

    public void setCreated_at(Date created_at) {
        this.created_at = created_at;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword_hash() {
        return password_hash;
    }

    public void setPassword_hash(String password_hash) {
        this.password_hash = password_hash;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}