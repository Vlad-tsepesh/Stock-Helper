package com.example.stockhelper.infrastructure.xmp;

import com.example.stockhelper.application.port.out.XmpUpdaterPort;
import com.example.stockhelper.application.exceptions.XmpUpdateException;
import com.example.stockhelper.domain.model.ImageDescription;
import org.apache.commons.imaging.formats.jpeg.xmp.JpegXmpRewriter;
import org.apache.xmpbox.XMPMetadata;
import org.apache.xmpbox.schema.DublinCoreSchema;
import org.apache.xmpbox.xml.XmpSerializer;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@Component
public class XmpServiceAdapter implements XmpUpdaterPort {

    @Override
    public Resource updateXmp(MultipartFile file, ImageDescription imageDescription) {
        validateInput(file, imageDescription);

        try (InputStream is = file.getInputStream();
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {

            String xmpXml = serializeXmp(buildXmp(imageDescription));
            new JpegXmpRewriter().updateXmpXml(is, outputStream, xmpXml);

            return new ByteArrayResource(outputStream.toByteArray()) {
                @Override
                public String getFilename() {
                    return sanitizeFilename(imageDescription.title());
                }
            };

        } catch (Exception e) {
            throw new XmpUpdateException("Failed to update XMP metadata", e);
        }
    }

    private void validateInput(MultipartFile file, ImageDescription desc) {
        if (file.isEmpty()) throw new IllegalArgumentException("File is empty");
        if (desc.title() == null || desc.title().isBlank()) throw new IllegalArgumentException("Title cannot be empty");
        if (desc.description() == null) throw new IllegalArgumentException("Description cannot be null");
        if (desc.keywords() == null || desc.keywords().isEmpty()) throw new IllegalArgumentException("Keywords cannot be empty");
    }

    private XMPMetadata buildXmp(ImageDescription desc) {
        XMPMetadata xmp = XMPMetadata.createXMPMetadata();
        DublinCoreSchema dc = xmp.createAndAddDublinCoreSchema();
        dc.setTitle(desc.title());
        dc.setDescription(desc.description());
        desc.keywords().forEach(dc::addSubject);
        return xmp;
    }

    private String serializeXmp(XMPMetadata xmp) throws Exception {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            new XmpSerializer().serialize(xmp, baos, true);
            return baos.toString(StandardCharsets.UTF_8);
        }
    }

    private String sanitizeFilename(String title) {
        return title.replaceAll("[^a-zA-Z0-9\\-_\\.]", "_") + ".jpg";
    }
}
