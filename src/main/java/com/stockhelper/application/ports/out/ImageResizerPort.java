package com.stockhelper.application.ports.out;

import com.stockhelper.domain.model.ImageRequest;
import org.springframework.core.io.Resource;

public interface ImageResizerPort {
    Resource resizeImage(ImageRequest image, int maxSize);
}
