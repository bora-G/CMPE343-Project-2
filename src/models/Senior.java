package models;

import input.Input;
import services.ContactService;
import menu.MenuItems;
import static menu.MenuUtils.*;

public class Senior extends User {

    public Senior() {
        setRole("Senior");
    }

    @Override
    public void showUserMenu() {
        ContactService contactService = new ContactService();

        while (true) {
            clear();
            printMenuHeader("SENIOR DEV MENU");
            printCentered("User: " + getName() + " " + getSurname(), CYAN);
            System.out.println();

            printOption("1", "List all contacts");
            printOption("2", "Search by single field");
            printOption("3", "Search by multiple fields");
            printOption("4", "Sort contacts");
            printOption("5", "Add new contact");
            printOption("6", "Update contact");
            printOption("7", "Delete contact");
            printOption("8", "Change password");
            printOption("9", "Undo last operation");
            printOption("0", "Logout");
            
            printPrompt();

            String choice = Input.scanner.nextLine().trim();

            switch (choice) {
                case "1": contactService.listAllContacts(); break;
                case "2": contactService.searchBySingleField(); break;
                case "3": contactService.searchByMultipleFields(); break;
                case "4": contactService.sortContacts(); break;
                case "5": contactService.addContact(this); break;
                case "6": contactService.updateContact(this); break;
                case "7": contactService.deleteContact(this); break;
                case "8": changePassword(); break;
                case "9": contactService.undoLastOperation(); break;
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