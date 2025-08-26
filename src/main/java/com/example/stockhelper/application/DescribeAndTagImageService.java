package com.example.stockhelper.application;

import com.example.stockhelper.domain.model.ImageRequest;
import com.example.stockhelper.application.port.in.DescribeAndTagImageUseCase;
import com.example.stockhelper.application.port.out.ImageDescriptionGeneratorPort;
import com.example.stockhelper.application.port.out.ImageResizerPort;
import com.example.stockhelper.application.port.out.XmpUpdaterPort;
import com.example.stockhelper.domain.model.ImageDescription;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.Objects;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
@RequiredArgsConstructor
public class DescribeAndTagImageService implements DescribeAndTagImageUseCase {

    private final ImageResizerPort resizer;
    private final ImageDescriptionGeneratorPort descriptionGenerator;
    private final XmpUpdaterPort updater;
    private final ImageDescriptionValidator validator;

    public Resource generateAndSetMetadata(ImageRequest image) {
        Resource resizedResource = resizer.resizeImage(image, 500);
        ImageDescription imageDescription = descriptionGenerator.generateDescription(resizedResource);
        validator.validate(imageDescription);

        return updater.updateXmp(image, imageDescription);
    }

    public Resource process(List<ImageRequest> imageRequests) {
        List<Resource> updatedResources = imageRequests.stream()
                .map(this::generateAndSetMetadata)
                .toList();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (
                ZipOutputStream zos = new ZipOutputStream(baos)) {
            for (Resource updatedResource : updatedResources) {
                zos.putNextEntry(new ZipEntry(Objects.requireNonNull(updatedResource.getFilename())));
                updatedResource.getInputStream().transferTo(zos);
                zos.closeEntry();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return new ByteArrayResource(baos.toByteArray());
    }
}
