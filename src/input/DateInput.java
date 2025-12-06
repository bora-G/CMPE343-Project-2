package input;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

/**
 * Utility class that handles validated date input from the user.
 * <p>
 * This class provides several static methods for reading:
 * <ul>
 *     <li>A complete date in {@code YYYY-MM-DD} format</li>
 *     <li>Individual day, month, and year components</li>
 * </ul>
 * All inputs loop until a valid value is entered or the user types {@code Q}
 * to cancel and return {@code null}.
 * </p>
 * @author Can
 * <p>
 * This class cannot be instantiated and is designed for static access only.
 * It relies on {@link Input#scanner}, which should be a globally shared
 * {@code Scanner} instance.
 * </p>
 */
public class DateInput {

     /** Private constructor to prevent instantiation. */
    private DateInput() {}

    /**
     * Reads a complete date string from the user in {@code YYYY-MM-DD} format.
     * <p>
     * Behaviour:
     * <ul>
     *     <li>Prompts the user with the given message</li>
     *     <li>Allows exiting with {@code Q}</li>
     *     <li>Rejects invalid date formats</li>
     *     <li>Rejects dates that occur in the future</li>
     * </ul>
     * </p>
     * @author Bora
     * @param prompt the text displayed before requesting input
     * @return a valid {@link LocalDate}, or {@code null} if the user chooses to exit
     */
    public static LocalDate readDate(String prompt) {
        while (true) {
            System.out.print(prompt + " (YYYY-MM-DD) or Q to go back: ");
            String input = Input.scanner.nextLine().trim();

            if (input.equalsIgnoreCase("q"))
                return null;

            try {
                LocalDate date = LocalDate.parse(input);  
                LocalDate today = LocalDate.now();

                if (date.isAfter(today)) {
                    System.out.println("Date cannot be in the future.");
                    continue;
                }
                return date;

            } catch (DateTimeParseException e) {
                System.out.println("Invalid date. Please use YYYY-MM-DD.");
            }
        }
    }

    /**
     * Reads a valid day value (1–31) from the user.
     * <p>
     * Allows:
     * <ul>
     *     <li>Digit-only input</li>
     *     <li>Typing {@code Q} to cancel</li>
     * </ul>
     * </p>
     * @author Bora
     * @return an integer between 1 and 31, or {@code null} if cancelled
     */
    public static Integer readDay() {
        while (true) {
            System.out.print("Enter day (1-31) or Q to go back: ");
            String input = Input.scanner.nextLine().trim();

            if (input.equalsIgnoreCase("q")) return null;

            try {
                int day = Integer.parseInt(input);
                if (day >= 1 && day <= 31) {
                    return day;
                }
                System.out.println("Day must be between 1 and 31.");
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
            }
        }
    }

    /**
     * Reads a valid month value (1–12) from the user.
     * <p>
     * Allows cancellation with {@code Q}.
     * </p>
     * @author Can
     * @return an integer between 1 and 12, or {@code null} if cancelled
     */
    public static Integer readMonth() {
        while (true) {
            System.out.print("Enter month (1-12) or Q to go back: ");
            String input = Input.scanner.nextLine().trim();

            if (input.equalsIgnoreCase("q")) return null;

            try {
                int month = Integer.parseInt(input);
                if (month >= 1 && month <= 12) {
                    return month;
                }
                System.out.println("Month must be between 1 and 12.");
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
            }
        }
    }

     /**
     * Reads a valid year from the user within the range 1900 through the current year.
     * <p>
     * Ensures:
     * <ul>
     *     <li>No future years</li>
     *     <li>No years before 1900</li>
     *     <li>Allows {@code Q} to cancel</li>
     * </ul>
     * </p>
     * @author Can
     * @return a valid year integer, or {@code null} if cancelled
     */
    public static Integer readYear() {
        int currentYear = LocalDate.now().getYear();
        while (true) {
            System.out.print("Enter year (1900-" + currentYear + ") or Q to go back: ");
            String input = Input.scanner.nextLine().trim();

            if (input.equalsIgnoreCase("q")) return null;

            try {
                int year = Integer.parseInt(input);
                if (year >= 1900 && year <= currentYear) {
                    return year;
                }
                System.out.println("Please enter a valid year between 1900 and " + currentYear);
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
            }
        }
    }

}
