package Undo;

import java.util.ArrayDeque;
import java.util.Deque;

public class UndoManager {

    private final Deque<Command> stack = new ArrayDeque<>();

    public void push(Command cmd) {
        if (cmd == null) return;
        stack.push(cmd);
    }

    public void undoLast() {
        if (stack.isEmpty()) {
            System.out.println("No operation to undo.");
            return;
        }

        Command last = stack.pop();
        last.undo();
        System.out.println("Last operation undone.");
    }

    public boolean hasUndo() {
        return !stack.isEmpty();
    }
}
