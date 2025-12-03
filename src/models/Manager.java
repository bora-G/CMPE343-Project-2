package models;

import input.Input;
import services.ContactService;
import services.UserService;

public class Manager extends User {

    public Manager() {
        setRole("Manager");
    }

    @Override
    public void showUserMenu() {
        UserService userService = new UserService();
        ContactService contactService = new ContactService();

        while (true) {
            System.out.println("\n=== MANAGER MENU ===");
            System.out.println("Hello, " + getName() + " " + getSurname());
            System.out.println("1) List all users");
            System.out.println("2) Add new user");
            System.out.println("3) Update existing user");
            System.out.println("4) Delete/Fire user");
            System.out.println("5) Contact Statistics");
            System.out.println("6) Change password");
            System.out.println("0) Logout");
            System.out.print("Your choice: ");

            String choice = Input.scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    userService.listAllUsers(this);
                    break;
                case "2":
                    userService.addUser(this);
                    break;
                case "3":
                    userService.updateUser(this);
                    break;
                case "4":
                    userService.deleteUser(this);
                    break;
                case "5":
                    contactService.showStatistics();
                    break;
                case "6":
                    changePassword();
                    break;
                case "0":
                    logout();
                    return;
                default:
                    System.out.println("Invalid choice.");
            }
        }
    }
}
