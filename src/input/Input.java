package input;

import java.util.Scanner;

/**
 * Provides a single shared {@link Scanner} instance for reading user input
 * from standard input ({@code System.in}).
 * <p>
 * This class is intended to centralize input handling across the entire
 * project so that multiple {@code Scanner} objects are not created for
 * the same input stream, which can lead to resource conflicts or
 * unexpected behavior.
 * </p>
 *
 * <p>
 * Usage example:
 * </p>
 * <pre>{@code
 * String name = Input.scanner.nextLine();
 * }</pre>
 */
public class Input {

    /**
     * A globally accessible scanner instance used throughout the project
     * for reading console input.
     * <p>
     * This scanner should not be closed, because closing it would also
     * close {@code System.in}, preventing further user input.
     * </p>
     *
     */
    public static final Scanner scanner = new Scanner(System.in);

}
