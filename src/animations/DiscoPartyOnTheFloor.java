package animations;

import java.util.Arrays;

public class DiscoPartyOnTheFloor {

    public static final int WIDTH = 80;
    public static final int HEIGHT = 50;
    public static final double ASPECT_RATIO = 0.5;

    // 1. TİP: YAN DANSÇILAR
    private static final String[] SIDE_DANCER_1 = {
            "    (o_o)    ",
            "    <| |>    ",
            "     | |     ",
            "     / \\     ",
            "    |   |    "
    };

    private static final String[] SIDE_DANCER_2 = {
            "    (o_o)    ",
            "    /| |\\    ",
            "     | |     ",
            "     / \\     ",
            "    /   \\    "
    };

    // 2. TİP: ARKA PLAN ÇİFTİ
    private static final String[] COUPLE_POSE_1 = {
            "   ( ^_^)    ",
            "    /| |\\    ",
            "     | |     ",
            "    /   \\    "
    };

    private static final String[] COUPLE_POSE_2 = {
            "   ( -_-)    ",
            "    | |    ",
            "     | |     ",
            "    /   \\    "
    };

    // 3. TİP: ORTA DANSÇI
    private static final String[] CENTER_POSE_A = {
            "    (>.<)    ",
            "    /|  |\\__ ",
            "   / |__|    ",
            "     /  \\    ",
            "    /    \\   "
    };

    private static final String[] CENTER_POSE_B = {
            "    (>.<)    ",
            " __/|  |\\    ",
            "    |__| \\   ",
            "    /  \\     ",
            "   /    \\    "
    };

    // Test için direkt bu sınıfı çalıştırmak istersen:
    public static void main(String[] args) throws Exception {
        showStaticScene();
        System.out.print("\nPress ENTER to start the party...");
        System.in.read();
        runDisco(10_000);
    }

    // 1) DURAĞAN SAHNE (tek frame, animasyonsuz)
    public static void showStaticScene() {
        int width = WIDTH;
        int height = HEIGHT;
        int bufferSize = width * height;

        double[] zBuffer = new double[bufferSize];
        char[] frameBuffer = new char[bufferSize];

        Arrays.fill(frameBuffer, ' ');
        Arrays.fill(zBuffer, 0);

        // Zemin
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

        // Askı
        int ballCenterX = width / 2;
        for (int y = 0; y < 12; y++) {
            int idx = ballCenterX + width * y;
            if (idx < bufferSize) frameBuffer[idx] = '|';
        }

        // Işık hüzmeleri (sabit C ile)
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

        // 3D küre (tek frame)
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

        // Dansçılar (sabit poz)
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

        // Çıktı
        StringBuilder sb = new StringBuilder();
        sb.append("\u001b[2J");
        sb.append("\u001b[H");
        for (int i = 0; i < bufferSize; i++) {
            sb.append(frameBuffer[i]);
            if (i % width == width - 1) sb.append('\n');
        }
        System.out.print(sb.toString());
    }

    // 2) SÜRELİ ANİMASYON
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

            // Zemin
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

            // Askı
            int ballCenterX = width / 2;
            for (int y = 0; y < 12; y++) {
                int idx = ballCenterX + width * y;
                if (idx < bufferSize) {
                    frameBuffer[idx] = '|';
                    if (tick % 2 == 0 && idx + 1 < bufferSize) frameBuffer[idx + 1] = '.';
                }
            }

            // Hüzmeler
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

            // 3D küre
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

            // Dansçılar
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

            // Çıktı
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

    // Dansçı çizimi
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
// ... Sınıfın geri kalanı aynı ...

    // GÜNCELLENMİŞ: Goodbye Animasyonu
    public static void runGoodbyeSequence() {
        int width = WIDTH;
        int height = HEIGHT;
        int bufferSize = width * height;

        double aspectRatio = ASPECT_RATIO;

        // Topun son konumu
        double A = 0, B = 0, C = 0; 
        
        // Dansçıların kaçış ofseti
        int dancerOffset = 0;
        int centerDancerY = 34; 

        double[] zBuffer = new double[bufferSize];
        char[] frameBuffer = new char[bufferSize];

        // Animasyon döngüsü
        for (int frame = 0; frame < 80; frame++) {
            Arrays.fill(frameBuffer, ' ');
            Arrays.fill(zBuffer, 0);

            // 1. Zemin
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

            // 2. Askı
            int ballCenterX = width / 2;
            for (int y = 0; y < 12; y++) {
                int idx = ballCenterX + width * y;
                if (idx < bufferSize) frameBuffer[idx] = '|';
            }

            // 3. Işık Hüzmeleri (Başlangıçta var, sonra sönüyor)
            if (frame < 20) {
                for (int y = 0; y < startFloorY + 5; y++) {
                    for (int x = 0; x < width; x++) {
                        double xx = (x - width / 2.0) / (width / 2.0);
                        double yy = (y - 15) / (height / 2.0) / aspectRatio;
                        double dist = Math.sqrt(xx * xx + yy * yy);
                        double angle = Math.atan2(yy, xx);
                        double ray = Math.sin(angle * 5 + C);
                        if (dist > 0.4 + (frame/40.0) && ray > 0.8) { 
                            int idx = x + width * y;
                            if (idx < bufferSize && frameBuffer[idx] == ' ') {
                                frameBuffer[idx] = (ray > 0.95) ? '|' : ';';
                            }
                        }
                    }
                }
                 A += 0.04 * (1 - frame/20.0); 
                 B += 0.08 * (1 - frame/20.0);
                 C -= 0.05 * (1 - frame/20.0);
            }

            // 4. 3D Küre
            DiscoBallRenderer.renderBall(
                    frameBuffer, zBuffer, width, height,
                    A, B, C, 15, 30.0, aspectRatio
            );

            // 5. Dansçıları Hareket Ettir
            if (frame > 20) {
                dancerOffset += 2; 
                centerDancerY += 1; 
            }

            // Dansçıları Çiz (Eğer sahne dışına çıkmadılarsa)
            drawDancer(frameBuffer, width, height, COUPLE_POSE_2, 26 - dancerOffset, 35, true);
            drawDancer(frameBuffer, width, height, COUPLE_POSE_2, 44 + dancerOffset, 35, false);

            drawDancer(frameBuffer, width, height, SIDE_DANCER_2, 8 - (dancerOffset + 2), 39, false);
            drawDancer(frameBuffer, width, height, SIDE_DANCER_2, 62 + (dancerOffset + 2), 39, false);

            if (34 + centerDancerY < height + 10) { 
                 drawDancer(frameBuffer, width, height, CENTER_POSE_B, 34, 43 + (frame - 20), false);
            }

            // 6. GÜNCELLENMİŞ GOODBYE YAZISI
            if (frame > 50) {
                // Daha büyük ve okunaklı "GOODBYE" ASCII Art
                String[] byeText = {
                    " _____                 _  _                  ",
                    "|  __ \\               | || |                 ",
                    "| |  \\/  ___    ___   | || |__   _   _   ___ ",
                    "| | __  / _ \\  / _ \\  | || '_ \\ | | | | / _ \\",
                    "| |_\\ \\| (_) || (_) | | || |_) || |_| ||  __/",
                    " \\____/ \\___/  \\___/  |_||_.__/  \\__, | \\___|",
                    "                                  __/ |      ",
                    "                                 |___/       "
                };
                
                // Yazıyı ekranın ortasına (topun üstüne gelecek şekilde) yerleştir
                int textY = 18; 
                for (int r = 0; r < byeText.length; r++) {
                    String line = byeText[r];
                    int textX = (width - line.length()) / 2; // Ortala
                    for (int k = 0; k < line.length(); k++) {
                         int idx = (textX + k) + width * (textY + r);
                         if (idx >= 0 && idx < bufferSize) {
                             // Yazıyı frameBuffer'a bas
                             frameBuffer[idx] = line.charAt(k);
                         }
                    }
                }
            }

            // Çıktıyı Bas
            StringBuilder sb = new StringBuilder();
            sb.append("\u001b[H"); // Cursor Home
            for (int i = 0; i < bufferSize; i++) {
                // GOODBYE yazısı belirdiğinde onu Sarı (Yellow) yapalım
                if (frame > 50 && frameBuffer[i] != ' ' && frameBuffer[i] != '#' && frameBuffer[i] != '.' && frameBuffer[i] != '|') {
                     sb.append("\u001B[33m").append(frameBuffer[i]).append("\u001B[0m");
                } else {
                     sb.append(frameBuffer[i]);
                }
                if (i % width == width - 1) sb.append('\n');
            }
            System.out.print(sb.toString());

            try { Thread.sleep(60); } catch (InterruptedException e) {}
        }
        
        // Son temizlik ve kapanış mesajı
        System.out.print("\033[H\033[2J");
        System.out.flush();
        System.out.println("\n\n");
        // Konsola son bir şık veda mesajı da ekleyebiliriz
        System.out.println("        See you next time on the dance floor!       ");
        System.out.println("\n\n");
    }
}
