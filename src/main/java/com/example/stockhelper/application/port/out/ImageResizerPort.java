package com.example.stockhelper.application.port.out;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface ImageResizerPort {
    Resource resizeImage(MultipartFile file, int maxSize);
}
