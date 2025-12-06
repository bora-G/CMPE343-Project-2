package menu;

import input.Input;

public class MenuUtils {

    /** Console output width used for text alignment. */
    public static final int CONSOLE_WIDTH = 80;

    /** Resets console color and style attributes. */
    public static final String RESET = "\u001B[0m";
    /** Black text color. */
    public static final String BLACK = "\u001B[30m";
    /** Red text color. */
    public static final String RED = "\u001B[31m";
    /** Green text color. */
    public static final String GREEN = "\u001B[32m";
    /** Yellow text color. */
    public static final String YELLOW = "\u001B[33m";
    /** Blue text color. */
    public static final String BLUE = "\u001B[34m";
    /** Purple text color. */
    public static final String PURPLE = "\u001B[35m";
    /** Cyan text color. */
    public static final String CYAN = "\u001B[36m";
    /** White text color. */
    public static final String WHITE = "\u001B[37m";
    /** Bold text style. */
    public static final String BOLD = "\u001B[1m";

    /**
     * Clears the console screen using ANSI escape sequences and resets
     * the cursor position to the top-left corner.
     * @author Can
     * <p>Note: Works on terminals that support ANSI codes.</p>
     */
    public static void clear() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    /**
     * Pauses execution for a given number of milliseconds.
     *@author Can
     * @param ms the number of milliseconds to sleep
     */
    public static void sleep(int ms) {
        try { Thread.sleep(ms); }
        catch (InterruptedException e) { Thread.currentThread().interrupt(); }
    }

    /**
     * Places a character into a 2D-rendered console buffer at the
     * specified (x, y) coordinate.
     *
     * <p>The buffer is treated as a flattened 2D array with a fixed width.
     * If the coordinates fall outside the buffer boundaries, the call is ignored.</p>
     * @author Mikail
     * @param buffer the character buffer being written to
     * @param width  the width of the buffer (number of columns)
     * @param x      the x-coordinate (column index)
     * @param y      the y-coordinate (row index)
     * @param ch     the character to place in the buffer
     */
    public static void putChar(char[] buffer, int width, int x, int y, char ch) {
        if (x < 0 || x >= width) return;
        int height = buffer.length / width;
        if (y < 0 || y >= height) return;
        int idx = x + width * y;
        if (idx < 0 || idx >= buffer.length) return;
        buffer[idx] = ch;
    }

    /**
     * Writes an entire string into the buffer starting at a given (x, y)
     * position, writing characters sequentially to the right.
     *
     * <p>This method delegates character placement to {@link #putChar}.</p>
     * @author Mikail
     * @param buffer the character buffer to modify
     * @param width  the width of the buffer grid
     * @param y      the row in which the text will be placed
     * @param startX the starting column of the text
     * @param text   the string to write into the buffer
     */
    public static void putText(char[] buffer, int width, int y, int startX, String text) {
        for (int i = 0; i < text.length(); i++) {
            putChar(buffer, width, startX + i, y, text.charAt(i));
        }
    }

    /**
     * Returns a string consisting of {@code n} space characters.
     * @author Mikail
     * @param n the number of spaces to generate
     * @return a string of {@code n} spaces, or an empty string if {@code n <= 0}
     */
    public static String spaces(int n) {
        if (n <= 0) return "";
        StringBuilder sb = new StringBuilder(n);
        for (int i = 0; i < n; i++) sb.append(' ');
        return sb.toString();
    }

    /**
     * Prints the given text centered horizontally within the console width,
     * applying the specified ANSI color.
     * @author Mikail
     * @param text  the text to print centered
     * @param color the ANSI color code to apply
     */
    public static void printCentered(String text, String color) {
        int padding = (CONSOLE_WIDTH - text.length()) / 2;
        System.out.println(spaces(padding) + color + text + RESET);
    }

    /**
     * Prints the given text centered horizontally using the default white color.
     * @author Can
     * @param text the text to print centered
     */
    public static void printCentered(String text) {
        printCentered(text, WHITE);
    }

    /**
     * Prints a stylized centered menu header consisting of:
     * <ul>
     *   <li>A decorative top border</li>
     *   <li>A centered uppercase title inside a framed box</li>
     *   <li>A matching bottom border</li>
     * </ul>
     *
     * <p>The header uses purple bold ANSI styling and fixed-width ASCII framing.</p>
     * @author Can
     * @param title the menu title to display
     */
    public static void printMenuHeader(String title) {
        String line = "----------------------------------------"; // 40 chars
        String color = PURPLE + BOLD;
        
        System.out.println();
        printCentered(".--" + line + "--.", color);

        int totalInnerWidth = line.length() + 2; // 42
        int titleLen = title.length();
        int leftPad = (totalInnerWidth - titleLen) / 2;
        int rightPad = totalInnerWidth - titleLen - leftPad;
        
        String content = "|" + spaces(leftPad) + title.toUpperCase() + spaces(rightPad) + "|";
        printCentered(content, color);
        
        printCentered("'--" + line + "--'", color);
        System.out.println();
    }

    /**
     * Prints a single menu option with a highlighted key and accompanying description.
     *
     * <p>The key is displayed in cyan bold, while the description remains white.</p>
     * @author Can
     * @param key         the shortcut key that triggers the option
     * @param description the text describing the option's purpose
     */
    public static void printOption(String key, String description) {
        String optionText = String.format("[%s] %s", key, description);
        System.out.println(spaces(25) + CYAN + BOLD + "[" + key + "] " + RESET + WHITE + description);
    }

    /**
     * Prints the standard input prompt used for menu selections.
     * The prompt is aligned using spaces and displayed in yellow.
     * @author Can
     */
    public static void printPrompt() {
         System.out.println();
         System.out.print(spaces(25) + YELLOW + "Your choice > " + RESET);
    }

    /**
 * Displays a prompt asking the user to press ENTER and waits
 * until an empty line is submitted. 
 * <p>
 * This method is typically used to pause the console flow
 * so the user can read messages before continuing.
 * @author Bora
 */
       public static void waitForEnter() {
        System.out.println("\nPress ENTER to continue...");
        Input.scanner.nextLine();
    }
}