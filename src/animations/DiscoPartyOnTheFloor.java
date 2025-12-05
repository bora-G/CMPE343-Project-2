package animations;

import java.util.Arrays;
/**
 * Renders an ASCII-based disco party scene in the console.
 * <p>
 * This class is responsible for:
 * <ul>
 *     <li>Drawing a static scene with a disco ball and dancers</li>
 *     <li>Running a timed animation where the disco ball rotates</li>
 *     <li>Animating floor tiles, light beams, and dancers in rhythm</li>
 * </ul>
 * The output is written directly to {@code System.out} using ANSI escape
 * codes to clear and reposition the cursor.
 * </p>
 */
public class DiscoPartyOnTheFloor {

    /** Width of the ASCII frame in characters. */
    public static final int WIDTH = 80;
    
    /** Height of the ASCII frame in characters. */
    public static final int HEIGHT = 50;

    /**
     * Aspect ratio used to compensate for character cell height,
     * so that the scene looks more proportional.
     */
    public static final double ASPECT_RATIO = 0.5;

    // ---------------------------------------------------------------------
    // 1. TYPE: SIDE DANCERS
    // ---------------------------------------------------------------------

    /** Side dancer sprite pose 1 (arms out). */
    private static final String[] SIDE_DANCER_1 = {
            "    (o_o)    ",
            "    <| |>    ",
            "     | |     ",
            "     / \\     ",
            "    |   |    "
    };

    /** Side dancer sprite pose 2 (different arm position). */
    private static final String[] SIDE_DANCER_2 = {
            "    (o_o)    ",
            "    /| |\\    ",
            "     | |     ",
            "     / \\     ",
            "    /   \\    "
    };

    // ---------------------------------------------------------------------
    // 2. TYPE: BACKGROUND COUPLE
    // ---------------------------------------------------------------------

    /** Background dancing couple sprite pose 1. */
    private static final String[] COUPLE_POSE_1 = {
            "   ( ^_^)    ",
            "    /| |\\    ",
            "     | |     ",
            "    /   \\    "
    };

    /** Background dancing couple sprite pose 2. */
    private static final String[] COUPLE_POSE_2 = {
            "   ( -_-)    ",
            "    | |    ",
            "     | |     ",
            "    /   \\    "
    };

    // ---------------------------------------------------------------------
    // 3. TYPE: CENTER DANCER
    // ---------------------------------------------------------------------

    /** Center dancer sprite pose A. */
    private static final String[] CENTER_POSE_A = {
            "    (>.<)    ",
            "    /|  |\\__ ",
            "   / |__|    ",
            "     /  \\    ",
            "    /    \\   "
    };

    /** Center dancer sprite pose B. */
    private static final String[] CENTER_POSE_B = {
            "    (>.<)    ",
            " __/|  |\\    ",
            "    |__| \\   ",
            "    /  \\     ",
            "   /    \\    "
    };

    /**
     * Entry point for quickly testing this class.
     * <p>
     * First shows a single static frame, then waits for the user to
     * press ENTER and starts the disco animation for a fixed duration.
     * </p>
     *
     * @param args command-line arguments (not used)
     * @throws Exception if reading from {@code System.in} fails
     */
    public static void main(String[] args) throws Exception {
        showStaticScene();
        System.out.print("\nPress ENTER to start the party...");
        System.in.read();
        runDisco(10_000);
    }

    /**
     * Renders a single, non-animated frame of the disco scene.
     * <p>
     * This method:
     * <ul>
     *     <li>Draws the floor tiles</li>
     *     <li>Draws the chain/rope holding the disco ball</li>
     *     <li>Draws static light beams</li>
     *     <li>Renders a non-rotating disco ball</li>
     *     <li>Places the dancers in their default poses</li>
     * </ul>
     * The frame is printed once to the console.
     * </p>
     */
    public static void showStaticScene() {
        int width = WIDTH;
        int height = HEIGHT;
        int bufferSize = width * height;

        double[] zBuffer = new double[bufferSize];
        char[] frameBuffer = new char[bufferSize];

        Arrays.fill(frameBuffer, ' ');
        Arrays.fill(zBuffer, 0);

       
        int floorHeight = 16;
        int startFloorY = height - floorHeight;

        for (int y = startFloorY; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int idx = x + width * y;
                int tileX = x / 10;
                int tileY = (y - startFloorY) / 3;
                boolean isTile = (tileX + tileY) % 2 == 0;

                if (isTile) frameBuffer[idx] = '#';
                else frameBuffer[idx] = '.';
            }
        }

        int ballCenterX = width / 2;
        for (int y = 0; y < 12; y++) {
            int idx = ballCenterX + width * y;
            if (idx < bufferSize) frameBuffer[idx] = '|';
        }

        double C = 0.0;
        for (int y = 0; y < startFloorY + 5; y++) {
            for (int x = 0; x < width; x++) {
                double xx = (x - width / 2.0) / (width / 2.0);
                double yy = (y - 15) / (height / 2.0) / ASPECT_RATIO;
                double dist = Math.sqrt(xx * xx + yy * yy);
                double angle = Math.atan2(yy, xx);
                double ray = Math.sin(angle * 5 + C);

                if (dist > 0.4 && ray > 0.8) {
                    int idx = x + width * y;
                    if (idx < bufferSize && frameBuffer[idx] == ' ') {
                        frameBuffer[idx] = (ray > 0.95) ? '|' : ';';
                    }
                }
            }
        }

        DiscoBallRenderer.renderBall(
                frameBuffer,
                zBuffer,
                width,
                height,
                0.0,
                0.0,
                0.0,
                15,     // centerY
                30.0,   // radius
                ASPECT_RATIO
        );

        int tick = 0;
        int rhythmBack = (tick / 6) % 2;
        String[] poseCouple = (rhythmBack == 0) ? COUPLE_POSE_1 : COUPLE_POSE_2;
        drawDancer(frameBuffer, width, height, poseCouple, 26, 35, true);
        drawDancer(frameBuffer, width, height, poseCouple, 44, 35, false);

        int rhythmSide = (tick / 5) % 4;
        String[] poseSide = (rhythmSide % 2 == 0) ? SIDE_DANCER_1 : SIDE_DANCER_2;
        drawDancer(frameBuffer, width, height, poseSide, 8, 39, false);
        drawDancer(frameBuffer, width, height, poseSide, 62, 39, false);

        int rhythmCenter = (tick / 4) % 2;
        String[] poseCenter = (rhythmCenter == 0) ? CENTER_POSE_A : CENTER_POSE_B;
        drawDancer(frameBuffer, width, height, poseCenter, 34, 43, false);

        StringBuilder sb = new StringBuilder();
        sb.append("\u001b[2J");
        sb.append("\u001b[H");
        for (int i = 0; i < bufferSize; i++) {
            sb.append(frameBuffer[i]);
            if (i % width == width - 1) sb.append('\n');
        }
        System.out.print(sb.toString());
    }
 /**
     * Runs the animated disco party for a given duration.
     * <p>
     * Inside the loop this method:
     * <ul>
     *     <li>Animates the disco floor tiles</li>
     *     <li>Animates the rope and light beams</li>
     *     <li>Rotates the disco ball by updating angles A, B, C</li>
     *     <li>Changes the poses of dancers according to a rhythm tick</li>
     *     <li>Reprints the entire frame on each iteration</li>
     * </ul>
     * The loop stops when the given duration (in milliseconds) has elapsed.
     * </p>
     *
     * @param durationMillis duration of the animation in milliseconds
     * @throws InterruptedException if {@link Thread#sleep(long)} is interrupted
     */
    public static void runDisco(long durationMillis) throws InterruptedException {
        int width = WIDTH;
        int height = HEIGHT;
        int bufferSize = width * height;

        double aspectRatio = ASPECT_RATIO;

        double A = 0, B = 0, C = 0;
        int tick = 0;

        double[] zBuffer = new double[bufferSize];
        char[] frameBuffer = new char[bufferSize];

        System.out.print("\u001b[2J");

        long endTime = System.currentTimeMillis() + durationMillis;

        while (System.currentTimeMillis() < endTime) {
            Arrays.fill(frameBuffer, ' ');
            Arrays.fill(zBuffer, 0);

            int floorHeight = 16;
            int startFloorY = height - floorHeight;
            for (int y = startFloorY; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    int idx = x + width * y;
                    int tileX = x / 10;
                    int tileY = (y - startFloorY) / 3;
                    boolean isTile = (tileX + tileY) % 2 == 0;

                    if (isTile) {
                        if ((tick / 6 + tileX) % 3 == 0) frameBuffer[idx] = '#';
                        else frameBuffer[idx] = ':';
                    } else {
                        frameBuffer[idx] = '.';
                    }
                }
            }

            int ballCenterX = width / 2;
            for (int y = 0; y < 12; y++) {
                int idx = ballCenterX + width * y;
                if (idx < bufferSize) {
                    frameBuffer[idx] = '|';
                    if (tick % 2 == 0 && idx + 1 < bufferSize) frameBuffer[idx + 1] = '.';
                }
            }

            for (int y = 0; y < startFloorY + 5; y++) {
                for (int x = 0; x < width; x++) {
                    double xx = (x - width / 2.0) / (width / 2.0);
                    double yy = (y - 15) / (height / 2.0) / aspectRatio;
                    double dist = Math.sqrt(xx * xx + yy * yy);
                    double angle = Math.atan2(yy, xx);
                    double ray = Math.sin(angle * 5 + C);

                    if (dist > 0.4 && ray > 0.8) {
                        int idx = x + width * y;
                        if (idx < bufferSize && frameBuffer[idx] == ' ') {
                            frameBuffer[idx] = (ray > 0.95) ? '|' : ';';
                        }
                    }
                }
            }

            DiscoBallRenderer.renderBall(
                    frameBuffer,
                    zBuffer,
                    width,
                    height,
                    A,
                    B,
                    C,
                    15,
                    30.0,
                    aspectRatio
            );

            int rhythmBack = (tick / 6) % 2;
            String[] poseCouple = (rhythmBack == 0) ? COUPLE_POSE_1 : COUPLE_POSE_2;
            drawDancer(frameBuffer, width, height, poseCouple, 26, 35, true);
            drawDancer(frameBuffer, width, height, poseCouple, 44, 35, false);

            int rhythmSide = (tick / 5) % 4;
            String[] poseSide = (rhythmSide % 2 == 0) ? SIDE_DANCER_1 : SIDE_DANCER_2;
            drawDancer(frameBuffer, width, height, poseSide, 8, 39, false);
            drawDancer(frameBuffer, width, height, poseSide, 62, 39, false);

            int rhythmCenter = (tick / 4) % 2;
            String[] poseCenter = (rhythmCenter == 0) ? CENTER_POSE_A : CENTER_POSE_B;
            drawDancer(frameBuffer, width, height, poseCenter, 34, 43, false);

            StringBuilder sb = new StringBuilder();
            sb.append("\u001b[H");
            for (int i = 0; i < bufferSize; i++) {
                sb.append(frameBuffer[i]);
                if (i % width == width - 1) sb.append('\n');
            }
            System.out.print(sb.toString());

            A += 0.04;
            B += 0.08;
            C -= 0.05;
            tick++;

            Thread.sleep(80);
        }
    }

    /**
     * Draws a dancer sprite onto the given frame buffer.
     * <p>
     * The sprite is defined as an array of strings, each string
     * representing one row of the dancer. Non-space characters
     * are copied into the frame buffer at the specified starting
     * position.
     * </p>
     *
     * @param buffer the character buffer representing the whole frame
     * @param width  frame width in characters
     * @param height frame height in characters
     * @param sprite the sprite lines to draw
     * @param startX leftmost x position where the sprite will be drawn
     * @param startY top y position where the sprite will be drawn
     * @param flip   if true, horizontally mirrors the sprite (left/right)
     */
    private static void drawDancer(char[] buffer, int width, int height,
                                   String[] sprite, int startX, int startY, boolean flip) {
        for (int r = 0; r < sprite.length; r++) {
            String line = sprite[r];

            if (flip) {
                line = new StringBuilder(line).reverse().toString();
                line = line.replace('(', '#').replace(')', '(').replace('#', ')');
                line = line.replace('/', '#').replace('\\', '/').replace('#', '\\');
                line = line.replace('{', '#').replace('}', '{').replace('#', '}');
                line = line.replace('<', '#').replace('>', '<').replace('#', '>');
                line = line.replace('^', '#').replace('^', '^').replace('#', '^');
            }

            int drawY = startY + r;
            if (drawY >= 0 && drawY < height) {
                for (int c = 0; c < line.length(); c++) {
                    char ch = line.charAt(c);
                    if (ch != ' ') {
                        int drawX = startX + c;
                        if (drawX >= 0 && drawX < width) {
                            int idx = drawX + width * drawY;
                            buffer[idx] = ch;
                        }
                    }
                }
            }
        }
    }
}

