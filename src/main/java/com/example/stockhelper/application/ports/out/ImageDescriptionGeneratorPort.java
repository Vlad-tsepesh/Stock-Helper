package com.example.stockhelper.application.ports.out;

import com.example.stockhelper.domain.model.ImageDescription;
import org.springframework.core.io.Resource;

public interface ImageDescriptionGeneratorPort {
    ImageDescription generateDescription(Resource image);
}
