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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@RequiredArgsConstructor
public class DescribeAndTagImageService implements DescribeAndTagImageUseCase {

    private final ImageResizerPort resizer;
    private final ImageDescriptionGeneratorPort descriptionGenerator;
    private final XmpUpdaterPort updater;
    private final ImageDescriptionValidator validator;
    private final ImageArchivePort archiver;
    private final static Logger logger = LoggerFactory.getLogger(DescribeAndTagImageService.class);

    private static final int MAX_ATTEMPTS = 3;
    private final AtomicInteger processCounter = new AtomicInteger();
    private static long startTime;

    @Override
    public Resource process(List<ImageRequest> imageRequests) {
        List<Resource> updatedResources = imageRequests.stream()
                .map(this::safeGenerateAndSetMetadata)
                .flatMap(Optional::stream)
                .toList();

        return archiver.createArchive(updatedResources);
    }

//    @Override
//    public Resource process(List<ImageRequest> imageRequests) {
//        startTime = System.currentTimeMillis();
//        // Limit concurrency to 5 threads
//        ExecutorService executor = Executors.newFixedThreadPool(5);
//
//        try {
//            // Submit all images as tasks
//            List<Future<Optional<Resource>>> futures = imageRequests.stream()
//                    .map(image -> executor.submit(() -> safeGenerateAndSetMetadata(image)))
//                    .toList();
//
//            // Collect results
//            List<Resource> updatedResources = futures.stream()
//                    .map(f -> {
//                        try {
//                            return f.get(); // blocks until task finishes
//                        } catch (Exception e) {
//                            // log exception for failed image
//                            return Optional.<Resource>empty();
//                        }
//                    })
//                    .flatMap(Optional::stream)
//                    .toList();
//
//            return archiver.createArchive(updatedResources);
//        } finally {
//            executor.shutdown();
//        }
//    }

    private Optional<Resource> safeGenerateAndSetMetadata(ImageRequest image) {
        Resource resizedResource = resizer.resizeImage(image, 500);
        logger.info("Generating AI description for image: {}", image.filename());
        return tryGenerateValidDescription(resizedResource)
                .map(desc -> updater.updateXmp(image, desc));
    }

    private Optional<ImageDescription> tryGenerateValidDescription(Resource resizedResource) {
        for (int attempt = 1; attempt <= MAX_ATTEMPTS; attempt++) {
            ImageDescription description = descriptionGenerator.generateDescription(resizedResource);
            try {
                validator.validate(description);

                long elapsed = (System.currentTimeMillis() - startTime) / 1000;
                int current = processCounter.incrementAndGet();

                logger.info("Image {} - {}: description generated on attempt {} after {}s",
                        current, description.title(), attempt, elapsed);

                return Optional.of(description);
            } catch (IllegalArgumentException e) {
                logger.warn("Image validation failed on attempt {}: {}", attempt, e.getMessage());
            }
        }
        return Optional.empty();
    }
}
