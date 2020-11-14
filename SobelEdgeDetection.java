// Written by: Brianna McDonald

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.awt.Color;

public class SobelEdgeDetection {
    static BufferedImage input;
    static int width, height;
    static int[][] horizSobel, vertSobel, sobel;

    public static void main(String args[]) throws IOException {
        // get input file
        String filename = args.length == 1 ? args[0] : "flower.png";
        try {
            input = ImageIO.read(new File(filename));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        width = input.getWidth();
        height = input.getHeight();

        // initialize sobel matrices
        horizSobel = new int[width][height];
        vertSobel = new int[width][height];
        sobel = new int[width][height];

        // perform sobel edge detection
        sobelEdgeDetection();

        // create output image
        File output = new File("sobel.png");
        ImageIO.write(input, "png", output);
    }

    // prevents index out of bounds errors
    private static int getIndex(int x, int maxX, int minX) {
        if (x < minX) {
            return minX;
        } else if (x > maxX) {
            return maxX;
        } else {
            return x;
        }
    }

    // find the grey value / luminosity of a given rgb value
    public static int getGreyValue(int rgb) {
        Color clr = new Color(rgb);
        int red = clr.getRed();
        int green = clr.getGreen();
        int blue = clr.getBlue();
        int greyValue = (int) Math.round((red + green + blue) / 3);
        return greyValue;
    }

    public static void horizontalSobel() {
        // set up horizontal sobel kernel
        int[][] dx = new int[3][3];
        dx[0][0] = -1;
        dx[0][1] = 0;
        dx[0][2] = 1;
        dx[1][0] = -2;
        dx[1][1] = 0;
        dx[1][2] = 2;
        dx[2][0] = -1;
        dx[2][1] = 0;
        dx[2][2] = 1;
        // calculate horizontal sobel values and store in the horizSobel array
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                int sum = 0;
                sum += getGreyValue(input.getRGB(getIndex(i - 1, width - 1, 0), getIndex(j - 1, height - 1, 0)))
                        * dx[0][0];
                sum += getGreyValue(input.getRGB(getIndex(i - 1, width - 1, 0), getIndex(j, height - 1, 0))) * dx[0][1];
                sum += getGreyValue(input.getRGB(getIndex(i - 1, width - 1, 0), getIndex(j + 1, height - 1, 0)))
                        * dx[0][2];
                sum += getGreyValue(input.getRGB(getIndex(i, width - 1, 0), getIndex(j - 1, height - 1, 0))) * dx[1][0];
                sum += getGreyValue(input.getRGB(getIndex(i, width - 1, 0), getIndex(j, height - 1, 0))) * dx[1][1];
                sum += getGreyValue(input.getRGB(getIndex(i, width - 1, 0), getIndex(j + 1, height - 1, 0))) * dx[1][2];
                sum += getGreyValue(input.getRGB(getIndex(i + 1, width - 1, 0), getIndex(j - 1, height - 1, 0)))
                        * dx[2][0];
                sum += getGreyValue(input.getRGB(getIndex(i + 1, width - 1, 0), getIndex(j, height - 1, 0))) * dx[2][1];
                sum += getGreyValue(input.getRGB(getIndex(i + 1, width - 1, 0), getIndex(j + 1, height - 1, 0)))
                        * dx[2][2];
                horizSobel[i][j] = sum;
            }
        }
    }

    public static void verticalSobel() {
        // set up vertical sobel kernel
        int[][] dy = new int[3][3];
        dy[0][0] = -1;
        dy[0][1] = -2;
        dy[0][2] = -1;
        dy[1][0] = 0;
        dy[1][1] = 0;
        dy[1][2] = 0;
        dy[2][0] = 1;
        dy[2][1] = 2;
        dy[2][2] = 1;
        // calculate vertical sobel value and store in vertSobel array
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                int sum = 0;
                sum += getGreyValue(input.getRGB(getIndex(i - 1, width - 1, 0), getIndex(j - 1, height - 1, 0)))
                        * dy[0][0];
                sum += getGreyValue(input.getRGB(getIndex(i - 1, width - 1, 0), getIndex(j, height - 1, 0))) * dy[0][1];
                sum += getGreyValue(input.getRGB(getIndex(i - 1, width - 1, 0), getIndex(j + 1, height - 1, 0)))
                        * dy[0][2];
                sum += getGreyValue(input.getRGB(getIndex(i, width - 1, 0), getIndex(j - 1, height - 1, 0))) * dy[1][0];
                sum += getGreyValue(input.getRGB(getIndex(i, width - 1, 0), getIndex(j, height - 1, 0))) * dy[1][1];
                sum += getGreyValue(input.getRGB(getIndex(i, width - 1, 0), getIndex(j + 1, height - 1, 0))) * dy[1][2];
                sum += getGreyValue(input.getRGB(getIndex(i + 1, width - 1, 0), getIndex(j - 1, height - 1, 0)))
                        * dy[2][0];
                sum += getGreyValue(input.getRGB(getIndex(i + 1, width - 1, 0), getIndex(j, height - 1, 0))) * dy[2][1];
                sum += getGreyValue(input.getRGB(getIndex(i + 1, width - 1, 0), getIndex(j + 1, height - 1, 0)))
                        * dy[2][2];
                vertSobel[i][j] = sum;
            }
        }
    }

    public static void sobelEdgeDetection() {
        // perform horizontal and vertical sobel
        horizontalSobel();
        verticalSobel();
        // store combined results in a sobel matrix
        int maxValue = Integer.MIN_VALUE;
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                double horizSobelSquared = Math.pow((double) horizSobel[i][j], 2);
                double vertSobelSquared = Math.pow((double) vertSobel[i][j], 2);
                int value = (int) Math.round(Math.sqrt(horizSobelSquared + vertSobelSquared));
                sobel[i][j] = value;
                // keep track of max value in the matrix
                if (value > maxValue) {
                    maxValue = value;
                }
            }
        }
        // scale all values to be in the range [0, 255]
        double scaleCoeff = 255.0 / maxValue;
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                int color = sobel[i][j];
                color = (int) (color * scaleCoeff);
                color = (color << 16) | (color << 8) | color;
                // set each pixel of the output image to the corresponding scaled value
                input.setRGB(i, j, color);
            }
        }
    }
}