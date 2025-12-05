package Undo;

import repository.ContactRepository;

/**
 * Represents the undo command for the "Add Contact" operation.
 * <p>
 * This class implements the {@link Command} interface to support the application's
 * [cite_start]Undo mechanism[cite: 42]. When a contact is added, an instance of this class is created
 * to store the necessary information (Contact ID) to reverse that action.
 * </p>
 * <p>
 * The inverse of adding a contact is deleting it. Therefore, the {@link #undo()}
 * method calls the repository's delete method using the stored contact ID.
 * </p>
 *
 * @author [Group Members Names Here]
 * @version 1.0
 * @see Undo.Command
 * @see repository.ContactRepository
 */
public class AddContactCommand implements Command {

    /**
     * The repository instance used to perform database operations.
     */
    private final ContactRepository repo;

    /**
     * The unique identifier of the contact that was just added.
     */
    private final int contactId;

    /**
     * Constructs a new AddContactCommand.
     *
     * @param contactId The ID of the newly created contact.
     * @param repo      The repository to use for deleting the contact during undo.
     */
    public AddContactCommand(int contactId, ContactRepository repo) {
        this.repo = repo;
        this.contactId = contactId;
    }

    /**
     * Reverses the "Add Contact" operation by deleting the contact from the database.
     */
    @Override
    public void undo() {
        repo.delete(contactId);
    }
}