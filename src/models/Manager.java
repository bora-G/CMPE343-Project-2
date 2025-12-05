package models;

import input.Input;
import services.ContactService;
import services.UserService;
import menu.MenuItems;
import static menu.MenuUtils.*;

public class Manager extends User {

    public Manager() {
        setRole("Manager");
    }

    @Override
    public void showUserMenu() {
        UserService userService = new UserService();
        ContactService contactService = new ContactService();

        while (true) {
            clear();
            printMenuHeader("MANAGER PORTAL");
            printCentered("Boss: " + getName() + " " + getSurname(), PURPLE);
            System.out.println();

            printOption("1", "List all users");
            printOption("2", "Add new user");
            printOption("3", "Update existing user");
            printOption("4", "Delete/Fire user");
            printOption("5", "Contact Statistics");
            printOption("6", "User Statistics");
            printOption("7", "Show salary report");
            printOption("8", "Change password");
            printOption("9", "Undo last operation");
            printOption("0", "Logout");
            
            printPrompt();

            String choice = Input.scanner.nextLine().trim();

            switch (choice) {
                case "1": userService.listAllUsers(this); break;
                case "2": userService.addUser(this); break;
                case "3": userService.updateUser(this); break;
                case "4": userService.deleteUser(this); break;
                case "5": contactService.showStatistics(); break;
                case "6": userService.showUserStatistics(this); break;
                case "7": userService.showSalaryReport(this); break;
                case "8": changePassword(); break;
                case "9": userService.undoLastUserOperation(); break;
                case "0": logout(); return;
                default:
                    System.out.println(RED + "Invalid choice." + RESET);
                    sleep(800);
            }
            
            if(!choice.equals("0")) {
                System.out.println("\n" + YELLOW + "Press Enter to continue..." + RESET);
                Input.scanner.nextLine();
            }
        }
    }
}