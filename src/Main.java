import menu.MenuItems;
import models.User;
import services.AuthService;

/**
 * Entry point of the Contact Management System application.
 * <p>
 * This class coordinates the startup animation sequence, displays the main
 * login/exit menu, authenticates the user, and then transfers control to the
 * appropriate role-specific menu based on the logged-in user's permissions.
 * </p>
 *
 * <h3>Application Flow:</h3>
 * <ol>
 *     <li>Run startup animations via {@link MenuItems#runStartupSequence()}</li>
 *     <li>Display the main menu with options:
 *          <ul>
 *              <li>1 → Login</li>
 *              <li>0 → Exit</li>
 *          </ul>
 *     </li>
 *     <li>Authenticate user using {@link AuthService#loginWithPrompt()}</li>
 *     <li>If authentication fails → prompt again</li>
 *     <li>If successful → redirect to user's role menu via {@link User#showUserMenu()}</li>
 *     <li>Loop until user exits</li>
 * </ol>
 *
 * <p>
 * The class uses a shared input scanner (see {@code input.Input.scanner})
 * for reading user input.
 * </p>
 */
public class Main {

    /**
     * Launches the program and manages the high-level application control flow.
     *
     * @param args command-line arguments (not used)
     */
    public static void main(String[] args) {

        AuthService authService = new AuthService();

        MenuItems.runStartupSequence();

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

