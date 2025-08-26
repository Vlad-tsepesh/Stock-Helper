package com.example.stockhelper.infrastructure.adapters;

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

@Component
public class ZipArchiveAdapter implements ImageArchivePort {
    @Override
    public Resource createArchive(List<Resource> resources) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Set<String> usedNames = new HashSet<>(); // track filenames we already put

        try (ZipOutputStream zos = new ZipOutputStream(baos)) {
            for (Resource resource : resources) {
                String baseName = Objects.requireNonNull(resource.getFilename());
                String entryName = makeUniqueName(baseName, usedNames);

                zos.putNextEntry(new ZipEntry(entryName));
                resource.getInputStream().transferTo(zos);
                zos.closeEntry();
            }
        } catch (Exception e) {
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
