package Undo;

public interface Command {

    default void execute() {
       
    }

    void undo();
}
