package com.example.Stock.Helper.service;


import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Service
public class ImageSizeService {

    public Resource resizeImage(MultipartFile file, int maxSize) throws IOException {
        BufferedImage originalImage = ImageIO.read(file.getInputStream());

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
