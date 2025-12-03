package animations;

public class DiscoBallRenderer {

    /**
     * 3D disco topunu verilen frameBuffer + zBuffer üzerine çizer.
     */
    public static void renderBall(
            char[] frameBuffer,
            double[] zBuffer,
            int width,
            int height,
            double A,
            double B,
            double C,
            int centerY,
            double radius,
            double aspectRatio
    ) {
        if (frameBuffer == null || zBuffer == null) return;
        if (frameBuffer.length != zBuffer.length) return;

        int bufferSize = frameBuffer.length;

        for (double i = 0; i < 6.28; i += 0.06) {
            for (double j = 0; j < 3.14; j += 0.06) {

                double sinI = Math.sin(i), cosI = Math.cos(i);
                double sinJ = Math.sin(j), cosJ = Math.cos(j);
                double sinA = Math.sin(A), cosA = Math.cos(A);
                double sinB = Math.sin(B), cosB = Math.cos(B);

                double x0 = Math.sin(j) * Math.cos(i);
                double y0 = Math.sin(j) * Math.sin(i);
                double z0 = Math.cos(j);

                double y1 = y0 * cosA - z0 * sinA;
                double z1 = y0 * sinA + z0 * cosA;
                double x1 = x0;

                double x2 = x1 * cosB - y1 * sinB;
                double y2 = x1 * sinB + y1 * cosB;
                double z2 = z1;

                double zFinal = z2 + 3.5;
                double D = 1 / zFinal;

                int xp = (int) (width / 2 + radius * D * x2);
                int yp = (int) (centerY + (radius * D * y2) * aspectRatio);

                if (xp < 0 || xp >= width || yp < 0 || yp >= height) continue;

                int idx = xp + width * yp;
                if (idx < 0 || idx >= bufferSize) continue;

                double L = x2 * 0.5 + y2 * 0.5 - z2 * 0.5;

                if (D > zBuffer[idx]) {
                    zBuffer[idx] = D;
                    if (L > 0) {
                        char sparkle = ((xp + yp) % 2 == 0) ? '<' : '>';
                        if (L > 0.8) frameBuffer[idx] = '8';
                        else if (L > 0.6) frameBuffer[idx] = sparkle;
                        else if (L > 0.3) frameBuffer[idx] = ';';
                        else frameBuffer[idx] = '.';
                    }
                }
            }
        }
    }
}
