import models.User;
import services.AuthService;

public class Main {
    public static void main(String[] args) {
        System.out.println("Welcome to CMPE343 Project #2");
        AuthService authService = new AuthService();

        while (true) {
            System.out.println("\n--- LOGIN ---");
            User user = authService.loginWithPrompt();

            if (user != null) {
                System.out.println("Login successful! Welcome, " + user.getName());
                user.showUserMenu();
            } else {
                System.out.println("Invalid credentials. Please try again.");
            }
        }
    }
}