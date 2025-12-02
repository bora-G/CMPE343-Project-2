package input;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public class DateInput {

    private DateInput() {}

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