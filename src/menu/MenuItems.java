package menu;

import animations.DiscoPartyOnTheFloor;
import animations.ProjectIntroAnimation;
import animations.DiscoBallRenderer;
import input.Input;

import java.util.Arrays;

import static menu.MenuUtils.*;

/**
 * Handles the main animated startup sequence and loading screens for the project.
 * <p>
 * This class is responsible for orchestrating:
 * <ul>
 *     <li>The initial static disco scene</li>
 *     <li>A loop containing: full disco animation → project intro → loading screen</li>
 *     <li>A login or restart selection screen after loading</li>
 * </ul>
 * It acts as the entry point for the visual experience before the system's actual
 * logic (such as the contact manager) begins.
 * </p>
 */
public class MenuItems {

   /**
     * Runs the full startup sequence shown when the program launches.
     * <p>
     * Sequence:
     * <ol>
     *     <li>Show static disco scene and wait for ENTER</li>
     *     <li>Enter loop:
     *         <ul>
     *             <li>Run animated disco party (10 seconds)</li>
     *             <li>Show group member intro animation</li>
     *             <li>Show 3D disco ball loading screen</li>
     *             <li>Ask user: Start system (S) or Restart animation (R)</li>
     *         </ul>
     *     </li>
     *     <li>If user selects S, return to caller (likely Main → login flow)</li>
     * </ol>
     * </p>
     */
    public static void runStartupSequence() {

        DiscoPartyOnTheFloor.showStaticScene();
        System.out.print("\nPress ENTER to start the party...");
        Input.scanner.nextLine();

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

    /**
     * Displays an animated loading screen that includes:
     * <ul>
     *     <li>A rotating 3D ASCII disco ball</li>
     *     <li>A progress bar that fills step-by-step</li>
     *     <li>Status text and percentage spinner</li>
     * </ul>
     * The animation lasts until the progress bar completes.
     *
     * @param statusText Additional text displayed under the main loading title
     */
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

     /**
     * Shows a simple screen after the intro finishes, asking whether the user wants to:
     * <ul>
     *     <li>Start the system (login)</li>
     *     <li>Restart the full animation sequence</li>
     * </ul>
     * The method loops until the user enters either S or R.
     *
     * @return 'S' for starting the system, or 'R' for restarting animations
     */
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

