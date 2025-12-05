package Undo;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * Manages the history of executable commands to facilitate Undo operations.
 * <p>
 * This class serves as the invoker in the Command Design Pattern. It maintains a
 * Last-In-First-Out (LIFO) stack of {@link Command} objects. Each time a state-changing
 * operation (Add, Update, Delete) is performed, a corresponding command is pushed onto this stack.
 * </p>
 * <p>
 * This mechanism directly addresses the project requirement: "How the system will support
 * Undo operations following update, add, or delete actions" .
 * </p>
 *
 * @author [Group Members Names Here]
 * @version 1.0
 * @see Undo.Command
 */
public class UndoManager {

    /**
     * The stack data structure used to store command history.
     * Use of Deque (ArrayDeque) provides efficient stack operations.
     */
    private final Deque<Command> stack = new ArrayDeque<>();

    /**
     * Pushes a new command onto the undo stack.
     * <p>
     * This method is called immediately after a successful operation (e.g., adding a contact)
     * to ensure the action can be reverted later.
     * </p>
     *
     * @param cmd The command object representing the action to be stored.
     */
    public void push(Command cmd) {
        if (cmd == null) return;
        stack.push(cmd);
    }

    /**
     * Reverts the most recently executed command.
     * <p>
     * Pops the last command from the stack and calls its {@link Command#undo()} method.
     * If the stack is empty, it notifies the user that there are no operations to undo.
     * </p>
     */
    public void undoLast() {
        if (stack.isEmpty()) {
            System.out.println("No operation to undo.");
            return;
        }

        Command last = stack.pop();
        last.undo();
        System.out.println("Last operation undone.");
    }

    /**
     * Checks if there are any commands available to undo.
     *
     * @return {@code true} if the stack contains commands; {@code false} otherwise.
     */
    public boolean hasUndo() {
        return !stack.isEmpty();
    }
}