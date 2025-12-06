package Undo;

import models.Contact;
import repository.ContactRepository;

/**
 * Represents the undo command for the "Update Contact" operation.
 * <p>
 * This class implements the {@link Command} interface to support the application's
 * Undo mechanism. It is responsible for reverting changes made to an existing contact.
 * </p>
 * <p>
 * <b>Mechanism:</b> When a contact is about to be updated, a snapshot of its <i>current state</i>
 * (before modification) is captured and stored in {@code oldContact}. If the undo operation
 * is triggered, the {@link #undo()} method forces an update on the database using this
 * old state, effectively discarding the new changes.
 * </p>
 *
 * @author Bora
 * @version 1.0
 * @see Undo.Command
 * @see repository.ContactRepository
 */
public class UpdateContactCommand implements Command {

    /**
     * The repository instance used to perform database operations.
     */
    private final ContactRepository repo;

    /**
     * The snapshot of the contact's state <b>before</b> the update operation was performed.
     * <p>
     * Used to restore the contact to its previous values (Name, Phone, etc.).
     * </p>
     */
    private final Contact oldContact;

    /**
     * Constructs a new UpdateContactCommand.
     *
     * @param oldContact The contact object containing the data exactly as it was before the update.
     * @param repo       The repository to use for restoring the contact data.
     */
    public UpdateContactCommand(Contact oldContact, ContactRepository repo) {
        this.oldContact = oldContact;
        this.repo = repo;
    }

    /**
     * Reverses the "Update Contact" operation.
     * <p>
     * It calls the repository's update method passing the {@code oldContact} snapshot,
     * thereby overwriting the current database record with the previous values.
     * </p>
     */
    @Override
    public void undo() {
        repo.update(oldContact);
    }
}