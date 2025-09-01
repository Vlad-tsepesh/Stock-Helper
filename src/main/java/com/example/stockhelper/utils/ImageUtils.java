package com.example.stockhelper.utils;

import java.awt.Dimension;
import java.awt.image.BufferedImage;

public class ImageUtils {

    public static Dimension getScaledDimension(BufferedImage image, int maxSize) {
        int originalWidth = image.getWidth();
        int originalHeight = image.getHeight();

        // Calculate scale factors for both dimensions
        double widthScale = (double) maxSize / originalWidth;
        double heightScale = (double) maxSize / originalHeight;

        // Choose the smaller scale to keep aspect ratio
        double scale = Math.min(widthScale, heightScale);

        // Calculate target dimensions
        int targetWidth = (int) (originalWidth * scale);
        int targetHeight = (int) (originalHeight * scale);

        return new Dimension(targetWidth, targetHeight);
    }
}
