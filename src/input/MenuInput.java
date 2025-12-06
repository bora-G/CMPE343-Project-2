package input;

/**
 * Utility class that provides validated numeric menu input.
 * <p>
 * Allows users to enter a menu option within a specified range or type
 * {@code Q} to cancel and return {@code null}. This class relies on
 * {@link Input#scanner} for reading user input.
 * </p>
 *
 * <p>This class cannot be instantiated.</p>
 */
public class MenuInput {

    /** Private constructor to prevent instantiation. */
    private MenuInput() {}

    /**
     * Reads a numeric menu choice from the user within the given range.
     * <p>
     * Behaviour:
     * <ul>
     *     <li>Prompts using the provided message</li>
     *     <li>Accepts only numbers between {@code min} and {@code max}</li>
     *     <li>Allows cancellation by typing {@code Q}</li>
     *     <li>Repeats the prompt until valid input is provided</li>
     * </ul>
     * </p>
     * @author Mikail
     * @param min    the minimum allowed menu value
     * @param max    the maximum allowed menu value
     * @param prompt the text displayed before asking for input
     * @return the valid menu choice, or {@code null} if the user enters {@code Q}
     */
    public static Integer readMenuChoice(int min, int max, String prompt) {
        while (true) {
            System.out.print(prompt + " (" + min + "-" + max + ") or Q to go back: ");

            String input = Input.scanner.nextLine().trim();

            if (input.equalsIgnoreCase("q"))
                return null;

            try {
                int choice = Integer.parseInt(input);
                if (choice >= min && choice <= max)
                    return choice;

                System.out.println("Please enter a number between " + min + " and " + max + ".");
            } catch (Exception e) {
                System.out.println("Invalid input. Enter a number or Q.");
            }
        }
    }
}

