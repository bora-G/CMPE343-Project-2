package Undo;

import repository.ContactRepository;

public class AddContactCommand implements Command {

    private final ContactRepository repo;
    private final int contactId;

    public AddContactCommand(int contactId, ContactRepository repo) {
        this.repo = repo;
        this.contactId = contactId;
    }

    @Override
    public void undo() {
        repo.delete(contactId);
    }
}
