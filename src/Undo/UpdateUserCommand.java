package Undo;

import models.User;
import repository.UserRepository;

/**
 * Represents the undo command for the "Update User" operation.
 * <p>
 * This class implements the {@link Command} interface to support the application's
 * Undo mechanism for administrative user management. It is responsible for reverting
 * modifications made to an existing user's account (e.g., changing roles, salary, or names).
 * </p>
 * <p>
 * <b>Mechanism:</b> Before a user record is updated in the database, a snapshot of its
 * state is captured and passed to this command. The {@link #undo()} method utilizes this
 * snapshot to overwrite the current database record, effectively restoring the user to
 * their previous state.
 * </p>
 *
 * @author Bora
 * @version 1.0
 * @see Undo.Command
 * @see repository.UserRepository
 */
public class UpdateUserCommand implements Command {

    /**
     * The snapshot of the user's state <b>before</b> the update operation was performed.
     * <p>
     * Used to restore the user's previous details.
     * </p>
     */
    private final User oldUser;

    /**
     * The repository instance used to perform database operations.
     */
    private final UserRepository repo;

    /**
     * Constructs a new UpdateUserCommand.
     *
     * @param oldUser The user object containing the data exactly as it was before the update.
     * @param repo    The repository to use for restoring the user data.
     */
    public UpdateUserCommand(User oldUser, UserRepository repo) {
        this.oldUser = oldUser;
        this.repo = repo;
    }

    /**
     * Reverses the "Update User" operation.
     * <p>
     * It calls the repository's update method passing the {@code oldUser} snapshot,
     * thereby restoring the user's information to its original state.
     * </p>
     */
    @Override
    public void undo() {
        repo.update(oldUser);
    }
}