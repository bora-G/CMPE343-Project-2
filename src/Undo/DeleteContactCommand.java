package Undo;

import models.Contact;
import repository.ContactRepository;

/**
 * Represents the undo command for the "Delete Contact" operation.
 * <p>
 * This class implements the {@link Command} interface to support the application's
 * Undo mechanism. It captures the full state of a contact object just before it is
 * removed from the database.
 * </p>
 * <p>
 * To reverse the deletion, the {@link #undo()} method re-inserts the preserved
 * contact record back into the repository.
 * </p>
 *
 * @author [Group Members Names Here]
 * @version 1.0
 * @see Undo.Command
 * @see repository.ContactRepository
 */
public class DeleteContactCommand implements Command {

    /**
     * The repository instance used to perform database operations.
     */
    private final ContactRepository repo;

    /**
     * The snapshot of the contact object that was deleted.
     * <p>
     * This object holds all the data (Name, Phone, ID, etc.) necessary to restore
     * the record exactly as it was.
     * </p>
     */
    private final Contact deletedContact;

    /**
     * Constructs a new DeleteContactCommand.
     *
     * @param deletedContact The contact object containing the data of the deleted record.
     * @param repo           The repository to use for re-inserting the contact during undo.
     */
    public DeleteContactCommand(Contact deletedContact, ContactRepository repo) {
        this.deletedContact = deletedContact;
        this.repo = repo;
    }

    /**
     * Reverses the "Delete Contact" operation by re-inserting the previously deleted contact
     * into the database.
     */
    @Override
    public void undo() {
        repo.insert(deletedContact);
    }
}