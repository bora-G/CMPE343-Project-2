import services.ContactService;

public class Main {
    public static void main(String[] args) {
        ContactService cs = new ContactService();
        cs.listAllContacts();   
    }
}