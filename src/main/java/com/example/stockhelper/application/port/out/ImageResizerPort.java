package com.example.stockhelper.application.port.out;

import com.example.stockhelper.domain.model.ImageRequest;
import org.springframework.core.io.Resource;

public interface ImageResizerPort {
    Resource resizeImage(ImageRequest image, int maxSize);
}
