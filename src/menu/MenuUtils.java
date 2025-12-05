package menu;

/**
 * Utility class providing helper methods for menu rendering, animation timing,
 * and writing characters or text into a 2D frame buffer.
 * <p>
 * These utilities are primarily used by the animated startup screens,
 * loading bars, and other ASCII-based console effects.
 * </p>
 *
 * <p>This class is not meant to be instantiated.</p>
 */
public class MenuUtils {

    /**
     * Clears the console using ANSI escape codes.
     * <p>
     * Moves the cursor to the top-left of the screen and clears visible content.
     * Works on most Unix-based terminals and Windows terminals that support ANSI.
     * </p>
     */
    public static void clear() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

     /**
     * Sleeps for the given number of milliseconds without throwing an exception.
     * <p>
     * If interrupted, the thread's interrupt flag is restored.
     * </p>
     *
     * @param ms number of milliseconds to sleep
     */
    public static void sleep(int ms) {
        try { Thread.sleep(ms); }
        catch (InterruptedException e) { Thread.currentThread().interrupt(); }
    }

     /**
     * Places a single character at the specified (x, y) location inside a 1D frame buffer.
     * <p>
     * The buffer represents a 2D grid of characters encoded as a 1D array in
     * row-major order. Bounds are safely checked; if coordinates fall outside
     * the visible area, nothing happens.
     * </p>
     *
     * @param buffer the frame buffer array storing characters
     * @param width  the width of the buffer (number of columns)
     * @param x      x-coordinate where the character will be placed
     * @param y      y-coordinate where the character will be placed
     * @param ch     the character to write
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
     * Writes a full string horizontally into the frame buffer, starting at {@code (startX, y)}.
     * <p>
     * This method simply loops over the characters in the text and delegates to
     * {@link #putChar(char[], int, int, int, char)} for safe placement.
     * </p>
     *
     * @param buffer the frame buffer to write into
     * @param width  buffer width
     * @param y      y-coordinate where the text will be written
     * @param startX starting x-coordinate
     * @param text   the string to display
     */
    public static void putText(char[] buffer, int width, int y, int startX, String text) {
        for (int i = 0; i < text.length(); i++) {
            putChar(buffer, width, startX + i, y, text.charAt(i));
        }
    }

    /**
     * Creates a string containing {@code n} space characters.
     * <p>
     * Commonly used to indent text in menus or animation screens.
     * </p>
     *
     * @param n number of spaces
     * @return a string containing exactly {@code n} spaces
     */
    public static String spaces(int n) {
        if (n <= 0) return "";
        StringBuilder sb = new StringBuilder(n);
        for (int i = 0; i < n; i++) sb.append(' ');
        return sb.toString();
    }
}

