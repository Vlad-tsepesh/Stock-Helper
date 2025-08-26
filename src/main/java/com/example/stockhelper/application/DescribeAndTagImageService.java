package com.example.stockhelper.application;

import com.example.stockhelper.application.ports.in.DescribeAndTagImageUseCase;
import com.example.stockhelper.application.ports.out.ImageArchivePort;
import com.example.stockhelper.application.ports.out.ImageDescriptionGeneratorPort;
import com.example.stockhelper.application.ports.out.ImageResizerPort;
import com.example.stockhelper.application.ports.out.XmpUpdaterPort;
import com.example.stockhelper.application.validators.ImageDescriptionValidator;
import com.example.stockhelper.domain.model.ImageDescription;
import com.example.stockhelper.domain.model.ImageRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DescribeAndTagImageService implements DescribeAndTagImageUseCase {

    private final ImageResizerPort resizer;
    private final ImageDescriptionGeneratorPort descriptionGenerator;
    private final XmpUpdaterPort updater;
    private final ImageDescriptionValidator validator;
    private final ImageArchivePort archiver;

    private static final int MAX_ATTEMPTS = 3;

    @Override
    public Resource process(List<ImageRequest> imageRequests) {
        List<Resource> updatedResources = imageRequests.stream()
                .map(this::safeGenerateAndSetMetadata)
                .flatMap(Optional::stream)
                .toList();

        return archiver.createArchive(updatedResources);
    }

    private Optional<Resource> safeGenerateAndSetMetadata(ImageRequest image) {
        Resource resizedResource = resizer.resizeImage(image, 500);
        return tryGenerateValidDescription(resizedResource)
                .map(desc -> updater.updateXmp(image, desc));
    }

    private Optional<ImageDescription> tryGenerateValidDescription(Resource resizedResource) {
        for (int attempts = 0; attempts < MAX_ATTEMPTS; attempts++) {
            ImageDescription description = descriptionGenerator.generateDescription(resizedResource);
            try {
                validator.validate(description);
                return Optional.of(description);
            } catch (IllegalArgumentException e) {
                // TODO: add proper logging
            }
        }
        return Optional.empty();
    }
}
