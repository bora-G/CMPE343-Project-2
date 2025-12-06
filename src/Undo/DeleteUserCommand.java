package Undo;

import models.User;
import repository.UserRepository;

/**
 * Represents the undo command for the "Delete User" operation.
 * <p>
 * This class implements the {@link Command} interface to support reversing the deletion
 * of a system user. Since deleting a user is a critical operation restricted to Managers,
 * this command provides a safety net by preserving the deleted user's state.
 * </p>
 * <p>
 * To reverse the deletion, the {@link #undo()} method re-inserts the preserved
 * {@link User} object back into the database using the repository.
 * </p>
 *
 * @author Bora
 * @version 1.0
 * @see Undo.Command
 * @see repository.UserRepository
 */
public class DeleteUserCommand implements Command {

    /**
     * The snapshot of the user object that was deleted.
     * <p>
     * Contains all user details (Username, Password Hash, Role, etc.) needed to restore the account.
     * </p>
     */
    private final User deletedUser;

    /**
     * The repository instance used to perform database operations.
     */
    private final UserRepository repo;

    /**
     * Constructs a new DeleteUserCommand.
     *
     * @param deletedUser The user object containing the data of the deleted record.
     * @param repo        The repository to use for re-inserting the user during undo.
     */
    public DeleteUserCommand(User deletedUser, UserRepository repo) {
        this.deletedUser = deletedUser;
        this.repo = repo;
    }

    /**
     * Reverses the "Delete User" operation by re-inserting the previously deleted user
     * into the database.
     */
    @Override
    public void undo() {
        repo.insert(deletedUser);
    }
}