import menu.MenuItems;
import models.User;
import services.AuthService;

public class Main {
    public static void main(String[] args) {

        AuthService authService = new AuthService();

       // MenuItems.runStartupSequence();

        while (true) {
            System.out.println("\n=== MAIN MENU ===");
            System.out.println("1) Login");
            System.out.println("0) Exit");
            System.out.print("Your choice: ");

            String choice = input.Input.scanner.nextLine().trim();

            if (choice.equals("0")) {
                System.out.println("Goodbye!");
                break;
            } else if (!choice.equals("1")) {
                System.out.println("Invalid choice.");
                continue;
            }


            User loggedIn = authService.loginWithPrompt();
            if (loggedIn == null) {
                System.out.println("Login failed. Try again.");
                continue;
            }

            System.out.println("Welcome, " + loggedIn.getName() + " (" + loggedIn.getRole() + ")");


            loggedIn.showUserMenu();


        }
    }
}
