package animations;
/**
 * A renderer class responsible for drawing a rotating 3D disco ball
 * onto a character-based frame buffer using simple projection and
 * lighting calculations.
 * <p>
 * This renderer uses sphere parametrization and applies rotations around
 * the X and Y axes to animate the disco ball. A z-buffer is used to ensure
 * correct depth rendering, and lighting values determine which ASCII
 * characters appear brighter or dimmer.
 * </p>
 */

public class DiscoBallRenderer {

   /**
     * Renders a 3D rotating disco ball into the provided frame buffer and z-buffer.
     * <p>
     * This method:
     * <ul>
     *   <li>Iterates over spherical coordinates (i, j) to form a sphere</li>
     *   <li>Applies rotations using angles A (X-axis) and B (Y-axis)</li>
     *   <li>Projects 3D points into 2D screen space</li>
     *   <li>Applies a simple brightness/light calculation</li>
     *   <li>Updates the frame buffer with ASCII characters ('8', '&lt;', '&gt;', ';', '.') based on brightness</li>
     *   <li>Uses the z-buffer to avoid drawing behind other points</li>
     * </ul>
     * </p>
     *
     * @param frameBuffer the character array representing the screen; will be modified by this method
     * @param zBuffer     the z-depth array corresponding to the frame buffer
     * @param width       the width of the frame in characters
     * @param height      the height of the frame in characters
     * @param A           rotation angle around the X-axis
     * @param B           rotation angle around the Y-axis
     * @param C           rotation angle around the Z-axis (currently unused but retained for extensibility)
     * @param centerY     vertical center point where the disco ball will be drawn
     * @param radius      radius of the disco ball in projection space
     * @param aspectRatio scaling factor used to correct vertical stretching in character-based terminals
    *  @author Can
    *  @author Mikail
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

