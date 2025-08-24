package com.example.Stock.Helper;

import org.apache.commons.io.output.ByteArrayOutputStream;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class ImageResizer {

    public static Resource resize(String inputPath, int maxSize) throws IOException {
        FileSystemResource resource = new FileSystemResource(inputPath);
        BufferedImage originalImage = ImageIO.read(resource.getInputStream());

        // calculate proportional scale
        double scale = (double) maxSize / originalImage.getWidth();

        int targetWidth = (int) (originalImage.getWidth() * scale);
        int targetHeight = (int) (originalImage.getHeight() * scale);

        BufferedImage resizedImage = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics2D = resizedImage.createGraphics();
        graphics2D.drawImage(originalImage, 0, 0, targetWidth, targetHeight, null);
        graphics2D.dispose();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(resizedImage, "png", baos);
        return new ByteArrayResource(baos.toByteArray());
    }
}
