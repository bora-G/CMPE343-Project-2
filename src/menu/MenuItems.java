package menu;

import animations.DiscoPartyOnTheFloor;
import animations.ProjectIntroAnimation;
import animations.DiscoBallRenderer;
import input.Input;

import java.util.Arrays;

import static menu.MenuUtils.*;

public class MenuItems {

    // Main.main() başında çağrılacak
    public static void runStartupSequence() {

        // 1) SADECE BİR KEZ: durağan sahne + ENTER
        DiscoPartyOnTheFloor.showStaticScene();
        System.out.print("\nPress ENTER to start the party...");
        Input.scanner.nextLine();

        // 2) DÖNGÜ: ANİMASYON + INTRO + LOADING + S/R
        while (true) {

            // Animasyon
            try {
                DiscoPartyOnTheFloor.runDisco(10_000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            // Grup üyeleri
            ProjectIntroAnimation.showOnlyGroupMembers();

            // Loading ekranı (3D disco topu + bar)
            showLoadingWithDiscoBall("Loading contact manager...");

            // S / R seçimi
            char choice = showLoginOrRestartScreen();

            if (choice == 'S') {
                return; // Main’in login akışına dön
            } else if (choice == 'R') {
                continue; // animasyon+intro+loading baştan
            }
        }
    }

    // Loading ekranı – 3D disco ball + char bazlı bar
    public static void showLoadingWithDiscoBall(String statusText) {
        int width = DiscoPartyOnTheFloor.WIDTH;
        int height = DiscoPartyOnTheFloor.HEIGHT;
        int bufferSize = width * height;

        char[] frameBuffer = new char[bufferSize];
        double[] zBuffer = new double[bufferSize];

        int barLength = 32;
        char[] spinner = { '|', '/', '-', '\\' };

        double A = 0.0;
        double B = 0.0;
        double C = 0.0;

        for (int step = 0; step <= barLength; step++) {
            Arrays.fill(frameBuffer, ' ');
            Arrays.fill(zBuffer, 0.0);

            // Yukarıya dönen disco topu
            DiscoBallRenderer.renderBall(
                    frameBuffer,
                    zBuffer,
                    width,
                    height,
                    A,
                    B,
                    C,
                    10,      // centerY
                    20.0,    // radius
                    DiscoPartyOnTheFloor.ASPECT_RATIO
            );

            A += 0.06;
            B += 0.10;

            String title = "Loading, please wait...";
            putText(frameBuffer, width, 2, (width - title.length()) / 2, title);
            putText(frameBuffer, width, 4, (width - statusText.length()) / 2, statusText);

            int barY = 30;
            putChar(frameBuffer, width, (width - (barLength + 2)) / 2, barY, '[');
            putChar(frameBuffer, width, (width - (barLength + 2)) / 2 + 1 + barLength, barY, ']');

            int barStartX = (width - (barLength + 2)) / 2 + 1;
            for (int j = 0; j < barLength; j++) {
                char ch = (j <= step) ? '#' : '.';
                putChar(frameBuffer, width, barStartX + j, barY, ch);
            }

            int percent = (int) ((step * 100.0) / barLength);
            String percStr = String.format("%3d%% ", percent) + spinner[step % spinner.length];
            putText(frameBuffer, width, barY + 2, (width - percStr.length()) / 2, percStr);

            StringBuilder sb = new StringBuilder();
            sb.append("\u001b[2J");
            sb.append("\u001b[H");
            for (int i = 0; i < bufferSize; i++) {
                sb.append(frameBuffer[i]);
                if (i % width == width - 1) sb.append('\n');
            }

            System.out.print(sb.toString());
            sleep(90);
        }

        sleep(400);
    }

    private static char showLoginOrRestartScreen() {
        while (true) {
            clear();
            System.out.println();
            System.out.println("============================================================================");
            System.out.println("                           PROJECT INTRO COMPLETED                           ");
            System.out.println("============================================================================");
            System.out.println();
            System.out.println("    Press 'S' to login to the system.");
            System.out.println("    Press 'R' to restart the disco animation + intro sequence.");
            System.out.println();
            System.out.print("Your choice (S/R): ");

            String line = Input.scanner.nextLine().trim().toUpperCase();

            if (line.equals("S")) return 'S';
            if (line.equals("R")) return 'R';

            System.out.println();
            System.out.println("Invalid choice. Please try again.");
            sleep(800);
        }
    }
}
