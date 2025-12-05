package Undo;

import repository.UserRepository;

/**
 * Represents the undo command for the "Add User" operation.
 * <p>
 * This class implements the {@link Command} interface to facilitate the reversal
 * of user creation actions. Since adding a user is a restricted operation (Manager only),
 * this command ensures that if a user is added by mistake, the action can be immediately
 * reverted by deleting that specific user record from the database.
 * </p>
 *
 * @author [Group Members Names Here]
 * @version 1.0
 * @see Undo.Command
 * @see repository.UserRepository
 */
public class AddUserCommand implements Command {

    /**
     * The unique identifier of the user that was added.
     */
    private final int userId;

    /**
     * The repository instance used to perform database operations.
     */
    private final UserRepository repo;

    /**
     * Constructs a new AddUserCommand.
     *
     * @param userId The ID of the newly created user.
     * @param repo   The repository to use for deleting the user during undo.
     */
    public AddUserCommand(int userId, UserRepository repo) {
        this.userId = userId;
        this.repo = repo;
    }

    /**
     * Reverses the "Add User" operation by deleting the previously added user from the database.
     */
    @Override
    public void undo() {
        repo.delete(userId);
    }
}