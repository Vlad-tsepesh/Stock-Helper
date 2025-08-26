package com.example.stockhelper.infrastructure.adapters;

import com.example.stockhelper.application.port.out.ImageArchivePort;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.Objects;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Component
public class ZipArchiveAdapter implements ImageArchivePort {
    @Override
    public Resource createArchive(List<Resource> resources) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (
                ZipOutputStream zos = new ZipOutputStream(baos)) {
            for (Resource resource : resources) {
                zos.putNextEntry(new ZipEntry(Objects.requireNonNull(resource.getFilename())));
                resource.getInputStream().transferTo(zos);
                zos.closeEntry();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return new ByteArrayResource(baos.toByteArray());
    }
}
