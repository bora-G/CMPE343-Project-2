package input;

public class MenuInput {

    private MenuInput() {}

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
