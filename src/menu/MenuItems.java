package menu;

import animations.DiscoPartyOnTheFloor;
import animations.ProjectIntroAnimation;
import animations.DiscoBallRenderer;
import input.Input;

import java.util.Arrays;
import static menu.MenuUtils.*;

public class MenuItems {

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
    
    public static void showTransition(String message) {
        showLoadingWithDiscoBall(message, 20); 
    }

    public static void showLoadingWithDiscoBall(String statusText) {
        showLoadingWithDiscoBall(statusText, 60); 
    }

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