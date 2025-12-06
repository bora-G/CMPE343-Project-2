package menu;

import animations.DiscoPartyOnTheFloor;
import animations.ProjectIntroAnimation;
import animations.DiscoBallRenderer;
import input.Input;

import java.util.Arrays;
import static menu.MenuUtils.*;

public class MenuItems {

    /**
     * Runs the startup sequence of the application, displaying various animated
     * intro scenes and handling user input for proceeding to the login screen
     * or restarting the startup flow.
     *
     * <p>The sequence includes:
     * <ul>
     *   <li>Showing a static intro scene</li>
     *   <li>Waiting for the user to press ENTER</li>
     *   <li>Running the disco animation loop</li>
     *   <li>Displaying group members and a loading animation</li>
     *   <li>Allowing the user to choose between starting the system or restarting the sequence</li>
     * </ul>
     *@author Can
     * The method continues looping until the user chooses to start the system ('S'),
     * at which point it returns.
     */
    public static void runStartupSequence() {
        DiscoPartyOnTheFloor.showStaticScene();
        System.out.print("\n" + spaces(20) + PURPLE + BOLD + "Press ENTER to start the party..." + RESET);
        Input.scanner.nextLine();

        while (true) {
            try {
                DiscoPartyOnTheFloor.runDisco(5_000); 
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            ProjectIntroAnimation.showOnlyGroupMembers();

            showLoadingWithDiscoBall("System Initialization...");

            char choice = showLoginOrRestartScreen();

            if (choice == 'S') {
                return;
            } else if (choice == 'R') {
                continue;
            }
        }
    }

    /**
     * Shows a short transition animation with a disco ball and loading bar
     * using a default, relatively fast delay between frames.
     *
     * <p>This is a convenience method that forwards the call to
     * {@link #showLoadingWithDiscoBall(String, int)} with a shorter delay.</p>
     *@author Mikail
     * @param message the status message to display under the loading animation
     */
    public static void showTransition(String message) {
        showLoadingWithDiscoBall(message, 20); 
    }

    /**
     * Shows a disco ball loading animation with a progress bar and status text,
     * using a default delay suitable for longer loading sequences.
     *
     * <p>This is a convenience overload that forwards the call to
     * {@link #showLoadingWithDiscoBall(String, int)} with a standard delay.</p>
     * @author Mikail
     * @param statusText the status message to display under the loading animation
     */
    public static void showLoadingWithDiscoBall(String statusText) {
        showLoadingWithDiscoBall(statusText, 60); 
    }

    /**
     * Renders and animates a disco ball loading screen in the console, including:
     * <ul>
     *   <li>A hanging disco ball with simple "chain" characters</li>
     *   <li>A rotating disco ball rendered into a character buffer</li>
     *   <li>A progress bar that fills over time</li>
     *   <li>A percentage indicator and spinner next to it</li>
     *   <li>A status text line under a "PLEASE WAIT" title</li>
     * </ul>
     *
     * <p>The animation is drawn frame by frame into a character buffer,
     * then printed to the console using ANSI escape sequences to clear
     * and reposition the cursor, as well as to apply basic coloring.</p>
     * @author Can
     * @param statusText the status or description text displayed below the title
     * @param delayMs    the delay in milliseconds between each animation frame
     */
    public static void showLoadingWithDiscoBall(String statusText, int delayMs) {
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
        
        int ballCenterY = 12; 
        int ballCenterX = width / 2;

        for (int step = 0; step <= barLength; step++) {
            Arrays.fill(frameBuffer, ' ');
            Arrays.fill(zBuffer, 0.0);

            for (int y = 0; y < ballCenterY - 5; y++) {
                putChar(frameBuffer, width, ballCenterX, y, '|');
                if (y % 4 == 0) putChar(frameBuffer, width, ballCenterX, y, 'O'); 
            }

            DiscoBallRenderer.renderBall(
                    frameBuffer,
                    zBuffer,
                    width,
                    height,
                    A,
                    B,
                    C,
                    ballCenterY, 
                    20.0,    
                    DiscoPartyOnTheFloor.ASPECT_RATIO
            );

            A += 0.06;
            B += 0.10;

            int textStartY = 28; 
            
            String title = "PLEASE WAIT";
            putText(frameBuffer, width, textStartY, (width - title.length()) / 2, title);
            putText(frameBuffer, width, textStartY + 2, (width - statusText.length()) / 2, statusText);

            int barY = textStartY + 5;
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
                if (frameBuffer[i] == '#' || frameBuffer[i] == '[' || frameBuffer[i] == ']') {
                     sb.append(CYAN).append(frameBuffer[i]).append(RESET);
                } else if (frameBuffer[i] == '8' || frameBuffer[i] == '<' || frameBuffer[i] == '>') {
                     sb.append(PURPLE).append(frameBuffer[i]).append(RESET);
                } else if (frameBuffer[i] == '|') {
                     sb.append(WHITE).append(frameBuffer[i]).append(RESET); 
                } else {
                     sb.append(frameBuffer[i]);
                }
                
                if (i % width == width - 1) sb.append('\n');
            }

            System.out.print(sb.toString());
            sleep(delayMs); 
        }
        sleep(200);
        clear();
    }

    /**
     * Displays the initial screen that lets the user choose between
     * logging into the system or restarting the startup animation.
     *
     * <p>The method continuously prompts the user until a valid choice
     * is entered:</p>
     * <ul>
     *   <li><b>S</b> – proceed to the login flow</li>
     *   <li><b>R</b> – restart the intro/animation sequence</li>
     * </ul>
     * @author Can
     * @return 'S' if the user chooses to log in, or 'R' if the user chooses to restart the animation
     */
    private static char showLoginOrRestartScreen() {
        while (true) {
            clear();
            printMenuHeader("WELCOME!");
            
            printOption("S", "Login to System");
            printOption("R", "Restart Animation");
            
            printPrompt();

            String line = Input.scanner.nextLine().trim().toUpperCase();

            if (line.equals("S")) return 'S';
            if (line.equals("R")) return 'R';

            System.out.println("\n" + spaces(25) + RED + "Invalid choice. Try again." + RESET);
            sleep(800);
        }
    }
}