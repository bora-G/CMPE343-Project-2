package menu;

public class MenuUtils {

    public static final int CONSOLE_WIDTH = 80;

    public static final String RESET = "\u001B[0m";
    public static final String BLACK = "\u001B[30m";
    public static final String RED = "\u001B[31m";
    public static final String GREEN = "\u001B[32m";
    public static final String YELLOW = "\u001B[33m";
    public static final String BLUE = "\u001B[34m";
    public static final String PURPLE = "\u001B[35m";
    public static final String CYAN = "\u001B[36m";
    public static final String WHITE = "\u001B[37m";
    public static final String BOLD = "\u001B[1m";

    public static void clear() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    public static void sleep(int ms) {
        try { Thread.sleep(ms); }
        catch (InterruptedException e) { Thread.currentThread().interrupt(); }
    }

    public static void putChar(char[] buffer, int width, int x, int y, char ch) {
        if (x < 0 || x >= width) return;
        int height = buffer.length / width;
        if (y < 0 || y >= height) return;
        int idx = x + width * y;
        if (idx < 0 || idx >= buffer.length) return;
        buffer[idx] = ch;
    }

    public static void putText(char[] buffer, int width, int y, int startX, String text) {
        for (int i = 0; i < text.length(); i++) {
            putChar(buffer, width, startX + i, y, text.charAt(i));
        }
    }

    public static String spaces(int n) {
        if (n <= 0) return "";
        StringBuilder sb = new StringBuilder(n);
        for (int i = 0; i < n; i++) sb.append(' ');
        return sb.toString();
    }

    public static void printCentered(String text, String color) {
        int padding = (CONSOLE_WIDTH - text.length()) / 2;
        System.out.println(spaces(padding) + color + text + RESET);
    }

    public static void printCentered(String text) {
        printCentered(text, WHITE);
    }

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

    public static void printOption(String key, String description) {
        String optionText = String.format("[%s] %s", key, description);
        System.out.println(spaces(25) + CYAN + BOLD + "[" + key + "] " + RESET + WHITE + description);
    }
    
    public static void printPrompt() {
         System.out.println();
         System.out.print(spaces(25) + YELLOW + "Your choice > " + RESET);
    }
}