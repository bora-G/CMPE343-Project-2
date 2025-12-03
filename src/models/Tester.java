package models;

import input.Input;
import services.ContactService;

public class Tester extends User {

    private final ContactService contactService;

    public Tester() {
        setRole("Tester");
        this.contactService = new ContactService();
    }

    @Override
    public void showUserMenu() {
        while (true) {
            System.out.println();
            System.out.println("=== TESTER MENU ===");
            System.out.println("1) List all contacts");
            System.out.println("2) Search by single field");
            System.out.println("3) Search by multiple fields");
            System.out.println("4) Sort contacts");
            System.out.println("5) Change password");
            System.out.println("0) Logout");
            System.out.print("Your choice: ");

            String choice = Input.scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    listAllContacts();
                    break;
                case "2":
                    searchBySingleField();
                    break;
                case "3":
                    searchByMultipleFields();
                    break;
                case "4":
                    sortContacts();
                    break;
                case "5":
                    changePassword();
                    break;
                case "0":
                    logout();
                    return; 
                default:
                    System.out.println("Invalid choice, try again.");
            }
        }
    }


    private void listAllContacts() {
      
        contactService.listAllContacts(); 
    }

    private void searchBySingleField() {   

        contactService.searchBySingleField();
    }

    private void searchByMultipleFields() {

        contactService.searchByMultipleFields();  
    }

    private void sortContacts() {
      
        contactService.sortContacts();  
    }
}
