package com.example.stockhelper.application;

import com.example.stockhelper.domain.model.ImageRequest;
import com.example.stockhelper.application.port.in.DescribeAndTagImageUseCase;
import com.example.stockhelper.application.port.out.ImageDescriptionGeneratorPort;
import com.example.stockhelper.application.port.out.ImageResizerPort;
import com.example.stockhelper.application.port.out.XmpUpdaterPort;
import com.example.stockhelper.domain.model.ImageDescription;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DescribeAndTagImageService implements DescribeAndTagImageUseCase {

    private final ImageResizerPort resizer;
    private final ImageDescriptionGeneratorPort descriptionGenerator;
    private final XmpUpdaterPort updater;
    private final ImageDescriptionValidator validator;

    public Resource process(ImageRequest image) {
        Resource resizedResource = resizer.resizeImage(image, 500);
        ImageDescription imageDescription = descriptionGenerator.generateDescription(resizedResource);
        validator.validate(imageDescription);

        return updater.updateXmp(image, imageDescription);
    }
}
