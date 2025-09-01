package com.example.stockhelper.infrastructure;

import com.example.stockhelper.application.ports.out.ImageArchivePort;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class ZipArchiveAdapter implements ImageArchivePort {
    private static final Logger log = LoggerFactory.getLogger(ZipArchiveAdapter.class);

    @Override
    public Resource createArchive(List<Resource> resources) {
        log.info("Creating ZIP archive with {} resources", resources.size());
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Set<String> usedNames = new HashSet<>(); // track filenames we already put

        try (ZipOutputStream zos = new ZipOutputStream(baos)) {
            for (Resource resource : resources) {
                String baseName = Objects.requireNonNull(resource.getFilename());
                String entryName = makeUniqueName(baseName, usedNames);
                log.debug("Adding file to archive: original='{}', entryName='{}'", baseName, entryName);

                zos.putNextEntry(new ZipEntry(entryName));
                resource.getInputStream().transferTo(zos);
                log.info("ZIP archive created successfully, size: {} bytes", baos.size());

                zos.closeEntry();
            }
        } catch (Exception e) {
            log.error("Failed to create ZIP archive", e);
            throw new RuntimeException(e);
        }

        return new ByteArrayResource(baos.toByteArray());
    }

    private String makeUniqueName(String name, Set<String> used) {
        String candidate = name;
        int counter = 1;
        while (!used.add(candidate)) {
            int dotIndex = name.lastIndexOf('.');
            if (dotIndex > 0) {
                candidate = name.substring(0, dotIndex) + " " + counter + name.substring(dotIndex);
            } else {
                candidate = name + " " + counter;
            }
            counter++;
        }
        return candidate;
    }
}
