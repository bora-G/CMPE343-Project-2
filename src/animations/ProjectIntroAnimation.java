package animations;

public class ProjectIntroAnimation {

    private static final String RESET   = "\u001B[0m";
    private static final String BOLD    = "\u001B[1m";
    private static final String CYAN    = "\u001B[36m";
    private static final String WHITE   = "\u001B[37m";

    private static void clear() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    private static void sleep(int ms) {
        try { Thread.sleep(ms); }
        catch (InterruptedException e) { Thread.currentThread().interrupt(); }
    }

    private static String spaces(int n) {
        if (n <= 0) return "";
        StringBuilder sb = new StringBuilder(n);
        for (int i = 0; i < n; i++) sb.append(' ');
        return sb.toString();
}

    private static void showGroupMembers() {
        String[] names = {
                "Bora Görgün",
                "Melek Sadiki",
                "Can Ersan",
                "Mikail Karacaer"
        };

        int finalIndent = 15;
        int slideSteps  = 12;

        for (int i = 0; i < names.length; i++) {
            for (int step = 0; step <= slideSteps; step++) {
                clear();
                System.out.println();
                System.out.println(spaces(15) + WHITE + BOLD + "PROJECT TEAM" + RESET);
                System.out.println(spaces(15) + CYAN + "------------------------" + RESET);
                System.out.println();

                for (int j = 0; j < i; j++) {
                    System.out.println(spaces(finalIndent) + WHITE + "- " + names[j] + RESET);
                }

                int currentIndent = 3 + step;
                if (currentIndent > finalIndent) currentIndent = finalIndent;
                System.out.println(spaces(currentIndent) + WHITE + "- " + names[i] + RESET);

                sleep(60);
            }
        }

        clear();
        System.out.println();
        System.out.println(spaces(15) + WHITE + BOLD + "PROJECT TEAM" + RESET);
        System.out.println(spaces(15) + CYAN + "------------------------" + RESET);
        System.out.println();
        for (String name : names) {
            System.out.println(spaces(finalIndent) + WHITE + "- " + name + RESET);
        }

        sleep(800);
    }

    // Menünün çağıracağı metot
    public static void showOnlyGroupMembers() {
        showGroupMembers();
    }

    // Test için
    public static void main(String[] args) {
        showOnlyGroupMembers();
    }
}
