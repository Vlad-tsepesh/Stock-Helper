package com.example.stockhelper.infrastructure;

import com.example.stockhelper.domain.model.ImageRequest;
import com.example.stockhelper.application.ports.out.ImageResizerPort;
import com.example.stockhelper.utils.ImageUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Component
public class ImageResizerAdapter implements ImageResizerPort {
    private static final Logger log = LoggerFactory.getLogger(ImageResizerAdapter.class);

    @Override
    public Resource resizeImage(ImageRequest image, int maxSize) {
        try {
            BufferedImage originalImage = ImageIO.read(image.content().getInputStream());
            log.info("Resizing image: original size unknown, maxSize = {}", maxSize);

            Dimension newSize = ImageUtils.getScaledDimension(originalImage, 500);
            log.info("Resized image dimensions will be: {}x{}", newSize.width, newSize.height);

            BufferedImage resizedImage = new BufferedImage(newSize.width, newSize.height, BufferedImage.TYPE_INT_RGB);
            Graphics2D graphics2D = resizedImage.createGraphics();
            graphics2D.drawImage(originalImage, 0, 0, newSize.width, newSize.height, null);
            graphics2D.dispose();

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(resizedImage, "png", baos);
            log.info("Image resized successfully, byte size: {}", baos.size());
            return new ByteArrayResource(baos.toByteArray());

        } catch (IOException e) {
            log.error("Failed to resize image", e);
            throw new RuntimeException("Failed to resize image", e);
        }
    }
}
