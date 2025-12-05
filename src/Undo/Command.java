package Undo;

/**
 * The base interface for the Command Design Pattern used in the Undo mechanism.
 * <p>
 * This interface defines the contract for all reversible operations (such as adding,
 * updating, or deleting contacts/users). Any class implementing this interface
 * must provide logic to reverse its action via the {@link #undo()} method.
 * </p>
 * <p>
 * This structure supports the project requirement for handling "Undo operations following
 * [cite_start]update, add, or delete actions"[cite: 42].
 * </p>
 *
 * @author [Group Members Names Here]
 * @version 1.0
 * @see Undo.UndoManager
 */
public interface Command {

    /**
     * Executes the command's logic.
     * <p>
     * Defined as a default method to allow flexibility; in some implementations,
     * the action might be executed directly by the service before the command object
     * is created and pushed to the stack.
     * </p>
     */
    default void execute() {
       
    }

    /**
     * Reverses the operation performed by this command.
     * <p>
     * Implementations must ensure that calling this method restores the system state
     * to exactly how it was before the command was executed (e.g., restoring a deleted
     * record or deleting a newly created one).
     * </p>
     */
    void undo();
}