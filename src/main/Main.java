package main;
import menu.MenuItems;
import menu.MenuUtils;
import static animations.DiscoPartyOnTheFloor.runGoodbyeSequence;
import static menu.MenuUtils.*;
import models.User;
import services.AuthService;
import input.Input;

public class Main {
    /**
     * Entry point of the Contact Manager System v2.0.
     * <p>
     * This method:
     * <ul>
     *   <li>Runs the startup animation sequence,</li>
     *   <li>Displays the main menu with options to log in or exit,</li>
     *   <li>Handles user input and validates the selected option,</li>
     *   <li>Performs user authentication via {@link AuthService},</li>
     *   <li>On successful login, transitions to the logged-in user's menu,</li>
     *   <li>On exit, plays the goodbye animation and terminates the program.</li>
     * </ul>
     *
     * @author Can
     * @param args command-line arguments (not used)
     */
    public static void main(String[] args) {

        AuthService authService = new AuthService();
        
        MenuItems.runStartupSequence();

        while (true) {
            clear(); 
            
            System.out.println(PURPLE + BOLD);
            System.out.println(spaces(20) + "      _..._");
            System.out.println(spaces(20) + "    .'     '.      CONTACT MANAGER");
            System.out.println(spaces(20) + "   /  _   _  \\     SYSTEM V2.0");
            System.out.println(spaces(20) + "   | (o)_(o) |");
            System.out.println(spaces(20) + "   '.   |   .'");
            System.out.println(spaces(20) + "     '.....'");
            System.out.println(RESET);

            printMenuHeader("MAIN MENU");
            printOption("1", "Login");
            printOption("0", "Exit");
            printPrompt();

            String choice = Input.scanner.nextLine().trim();

            if (choice.equals("0")) {
                runGoodbyeSequence();
                break;
            } else if (!choice.equals("1")) {
                printCentered("Invalid choice.", RED);
                sleep(1000);
                continue;
            }
            
            MenuItems.showTransition("Authenticating...");
            clear();
            printMenuHeader("USER LOGIN");
            
            User loggedIn = authService.loginWithPrompt();
            if (loggedIn == null) {
                System.out.println();
                printCentered("Login failed! User not found or wrong password.", RED);
                sleep(1500);
                continue;
            }

            MenuItems.showTransition("Loading User Profile...");
            loggedIn.showUserMenu();
        }
    }
}