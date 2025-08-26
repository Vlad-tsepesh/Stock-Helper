package com.example.stockhelper.application;

import com.example.stockhelper.application.port.in.DescribeAndTagImageUseCase;
import com.example.stockhelper.application.port.out.ImageArchivePort;
import com.example.stockhelper.application.port.out.ImageDescriptionGeneratorPort;
import com.example.stockhelper.application.port.out.ImageResizerPort;
import com.example.stockhelper.application.port.out.XmpUpdaterPort;
import com.example.stockhelper.domain.model.ImageDescription;
import com.example.stockhelper.domain.model.ImageRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DescribeAndTagImageService implements DescribeAndTagImageUseCase {

    private final ImageResizerPort resizer;
    private final ImageDescriptionGeneratorPort descriptionGenerator;
    private final XmpUpdaterPort updater;
    private final ImageDescriptionValidator validator;
    private final ImageArchivePort archiver;

    private Resource generateAndSetMetadata(ImageRequest image) {
        Resource resizedResource = resizer.resizeImage(image, 500);
        ImageDescription imageDescription = descriptionGenerator.generateDescription(resizedResource);
        validator.validate(imageDescription);

        return updater.updateXmp(image, imageDescription);
    }

    public Resource process(List<ImageRequest> imageRequests) {

        List<Resource> updatedResources = imageRequests.stream()
                .map(this::generateAndSetMetadata)
                .toList();

        return archiver.createArchive(updatedResources);
    }
}
