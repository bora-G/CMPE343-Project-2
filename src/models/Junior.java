package models;

import input.Input;
import services.ContactService;

public class Junior extends User {

    public Junior() {
        setRole("Junior");
    }

    @Override
    public void showUserMenu() {

        ContactService contactService = new ContactService();

        while (true) {
            System.out.println("\n=== JUNIOR MENU ===");
            System.out.println("1) List all contacts");
            System.out.println("2) Search by single field");
            System.out.println("3) Search by multiple fields");
            System.out.println("4) Sort contacts");
            System.out.println("5) Update existing contact");
            System.out.println("6) Change password");
            System.out.println("7) Undo last operation");
            System.out.println("0) Logout");
            System.out.print("Your choice: ");

            String choice = Input.scanner.nextLine().trim();

            switch (choice) {

                case "1":
                    contactService.listAllContacts();
                    break;

                case "2":
                    contactService.searchBySingleField();
                    break;

                case "3":
                    contactService.searchByMultipleFields();
                    break;

                case "4":
                    contactService.sortContacts();
                    break;

                case "5":
                    contactService.updateContact(this);
                    break;

                case "6":
                    changePassword();
                    break;

                case "7":
                    contactService.undoLastOperation();
                    break;

                case "0":
                    logout();
                    return;

                default:
                    System.out.println("Invalid choice, try again.");
            }
        }
    }
}