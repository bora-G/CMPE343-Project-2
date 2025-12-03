package Undo;

import models.Contact;
import repository.ContactRepository;

public class DeleteContactCommand implements Command {

    private final ContactRepository repo;
    private final Contact deletedContact;

    public DeleteContactCommand(Contact deletedContact, ContactRepository repo) {
        this.deletedContact = deletedContact;
        this.repo = repo;
    }

    @Override
    public void undo() {
        repo.insert(deletedContact);
    }
}
