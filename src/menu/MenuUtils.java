package menu;

public class MenuUtils {

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
}
