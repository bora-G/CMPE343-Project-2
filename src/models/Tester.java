package models;

import input.Input;
import services.ContactService;
import menu.MenuItems;
import static menu.MenuUtils.*;

public class Tester extends User {

    private final ContactService contactService;

    public Tester() {
        setRole("Tester");
        this.contactService = new ContactService();
    }

    @Override
    public void showUserMenu() {
        while (true) {
            clear();
            printMenuHeader("TESTER DASHBOARD");
            printCentered("User: " + getName() + " " + getSurname(), CYAN);
            System.out.println(); 

            printOption("1", "List all contacts");
            printOption("2", "Search by single field");
            printOption("3", "Search by multiple fields");
            printOption("4", "Sort contacts");
            printOption("5", "Change password");
            printOption("0", "Logout");
            
            printPrompt();

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
                    changePassword();
                    break;
                case "0":
                    logout();
                    return; 
                default:
                    System.out.println(RED + "Invalid choice, try again." + RESET);
                    sleep(1000);
            }
            
            if(!choice.equals("0")) {
                System.out.println("\n" + YELLOW + "Press Enter to return to menu..." + RESET);
                Input.scanner.nextLine();
            }
        }
    }
    private void listAllContacts() { contactService.listAllContacts(); }
    private void searchBySingleField() { contactService.searchBySingleField(); }
    private void searchByMultipleFields() { contactService.searchByMultipleFields(); }
    private void sortContacts() { contactService.sortContacts(); }
}