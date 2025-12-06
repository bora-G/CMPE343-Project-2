package animations;
import menu.MenuUtils;

/**
 * Provides a simple console-based introduction animation that displays
 * project group members with sliding text and ANSI color effects.
 * <p>
 * This class is intended to be used as an intro sequence inside a console
 * application, such as a menu-based Java project. It renders the names
 * of team members one by one, sliding them into place to create a
 * smooth animated effect.
 * </p>
 */
public class ProjectIntroAnimation {

     /** ANSI reset code for clearing formatting. */
    private static final String RESET   = "\u001B[0m";
    /** ANSI code for bold text. */
    private static final String BOLD    = "\u001B[1m";
    /** ANSI code for cyan text color. */
    private static final String CYAN    = "\u001B[36m";
    /** ANSI code for white text color. */
    private static final String WHITE   = "\u001B[37m";

    /**
     * Clears the console screen using ANSI escape sequences.
     * <p>
     * This method resets the cursor to the top-left and clears all printed
     * characters, simulating a fresh screen for animations.
     * </p>
     */
    private static void clear() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    /**
     * Sleeps for a given number of milliseconds.
     *
     * @param ms the number of milliseconds to pause execution
     */
    private static void sleep(int ms) {
        try { Thread.sleep(ms); }
        catch (InterruptedException e) { Thread.currentThread().interrupt(); }
    }

    /**
     * Creates a string consisting of a given number of space characters.
     *
     * @param n the number of spaces to generate
     * @return a string containing {@code n} consecutive space characters
     */
    private static String spaces(int n) {
        if (n <= 0) return "";
        StringBuilder sb = new StringBuilder(n);
        for (int i = 0; i < n; i++) sb.append(' ');
        return sb.toString();
}

     /**
     * Displays each group member's name with a sliding animation effect.
     * <p>
     * The animation proceeds name by name:
     * <ul>
     *     <li>Previously printed names remain fixed in their final position</li>
     *     <li>The next name slides horizontally until it reaches its target indent</li>
     *     <li>ANSI color codes are used for a highlight effect</li>
     * </ul>
     * After all names are presented, a final static list is shown.
     * </p>
     */
    private static void showGroupMembers() {
        String[] names = {
                "Bora Görgün",
                "Melek Sadiki",
                "Can Ersan",
                "Mikail Karacaer"
        };

        int finalIndent = (MenuUtils.CONSOLE_WIDTH - 20) / 2;;
        int slideSteps  = 20;

        for (int i = 0; i < names.length; i++) {
            for (int step = 0; step <= slideSteps; step++) {
                clear();
                System.out.println();
                MenuUtils.printCentered("PROJECT TEAM", WHITE + BOLD);
                MenuUtils.printCentered("------------------------", CYAN);
                System.out.println();
                for (int j = 0; j < i; j++) {
                    System.out.println(spaces(finalIndent) + WHITE + "- " + names[j] + RESET);
                }

                int currentIndent = (finalIndent - slideSteps) + step;
                if (currentIndent > finalIndent) currentIndent = finalIndent;
                System.out.println(spaces(currentIndent) + WHITE + "- " + names[i] + RESET);

                sleep(40);
            }
        }

        clear();    
        System.out.println();
        MenuUtils.printCentered("PROJECT TEAM", WHITE + BOLD);
        MenuUtils.printCentered("------------------------", CYAN);
        System.out.println();
        for (String name : names) {
            System.out.println(spaces(finalIndent) + WHITE + "- " + name + RESET);
        }

        sleep(1500);
    }

    /**
     * Public method used by menus or other classes to trigger the
     * introductory group-member animation.
     */
    public static void showOnlyGroupMembers() {
        showGroupMembers();
    }

    /**
     * Main method for testing the intro animation independently.
     * <p>
     * Running this class directly will show the full animation
     * without requiring integration into another part of the project.
     * </p>
     *
     * @param args not used
     */
    public static void main(String[] args) {
        showOnlyGroupMembers();
    }
}

