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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@Service
@RequiredArgsConstructor
public class DescribeAndTagImageService implements DescribeAndTagImageUseCase {

    private final ImageResizerPort resizer;
    private final ImageDescriptionGeneratorPort descriptionGenerator;
    private final XmpUpdaterPort updater;
    private final ImageDescriptionValidator validator;
    private final ImageArchivePort archiver;

    private static final int MAX_ATTEMPTS = 3;
    private static int processNumber;
    private static long startTime;

//    @Override
//    public Resource process(List<ImageRequest> imageRequests) {
//        List<Resource> updatedResources = imageRequests.stream()
//                .map(this::safeGenerateAndSetMetadata)
//                .flatMap(Optional::stream)
//                .toList();
//
//        return archiver.createArchive(updatedResources);
//    }

    @Override
    public Resource process(List<ImageRequest> imageRequests) {
        processNumber = 0;
        startTime = System.currentTimeMillis();
        // Limit concurrency to 5 threads
        ExecutorService executor = Executors.newFixedThreadPool(5);

        try {
            // Submit all images as tasks
            List<Future<Optional<Resource>>> futures = imageRequests.stream()
                    .map(image -> executor.submit(() -> safeGenerateAndSetMetadata(image)))
                    .toList();

            // Collect results
            List<Resource> updatedResources = futures.stream()
                    .map(f -> {
                        try {
                            return f.get(); // blocks until task finishes
                        } catch (Exception e) {
                            // log exception for failed image
                            return Optional.<Resource>empty();
                        }
                    })
                    .flatMap(Optional::stream)
                    .toList();

            return archiver.createArchive(updatedResources);
        } finally {
            executor.shutdown();
        }
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
                System.out.println(processNumber++ + description.title() + ", getting description done. "+ (System.currentTimeMillis() - startTime)/1000);
                return Optional.of(description);
            } catch (IllegalArgumentException e) {
                // TODO: add proper logging
            }
        }
        return Optional.empty();
    }
}
