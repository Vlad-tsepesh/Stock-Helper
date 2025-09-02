package com.stockhelper.application.ports.out;

import com.stockhelper.domain.model.ImageDescription;
import org.springframework.core.io.Resource;

public interface ImageDescriptionGeneratorPort {
    ImageDescription generateDescription(Resource image);
}
