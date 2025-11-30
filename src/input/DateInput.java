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
                LocalDate date = LocalDate.parse(input);  // 30 Şubat → otomatik hata
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
}
