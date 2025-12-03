package Undo;

import models.Contact;
import repository.ContactRepository;

public class UpdateContactCommand implements Command {

    private final ContactRepository repo;
    private final Contact oldContact;

    public UpdateContactCommand(Contact oldContact, ContactRepository repo) {
        this.oldContact = oldContact;
        this.repo = repo;
    }

    @Override
    public void undo() {
        repo.update(oldContact);
    }
}
