package com.example.stockhelper.infrastructure.adapters;

import com.example.stockhelper.domain.model.ImageRequest;
import com.example.stockhelper.application.ports.out.XmpUpdaterPort;
import com.example.stockhelper.application.exceptions.XmpUpdateException;
import com.example.stockhelper.domain.model.ImageDescription;
import org.apache.commons.imaging.formats.jpeg.xmp.JpegXmpRewriter;
import org.apache.xmpbox.XMPMetadata;
import org.apache.xmpbox.schema.DublinCoreSchema;
import org.apache.xmpbox.xml.XmpSerializer;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class XmpServiceAdapter implements XmpUpdaterPort {
    private static final Logger log = LoggerFactory.getLogger(XmpServiceAdapter.class);

    @Override
    public Resource updateXmp(ImageRequest image, ImageDescription imageDescription) {
        log.info("Updating XMP metadata for image: {}", image.content().getFilename());
        log.debug("ImageDescription title: {}", imageDescription.title());

        try (InputStream is = image.content().getInputStream();
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {

            String xmpXml = serializeXmp(buildXmp(imageDescription));
            log.debug("Serialized XMP metadata: {}", xmpXml);
            log.info("Writing XMP metadata into image stream...");
            new JpegXmpRewriter().updateXmpXml(is, outputStream, xmpXml);

            log.info("XMP metadata updated successfully, returning resource: {}", sanitizeFilename(imageDescription.title()));
            return new ByteArrayResource(outputStream.toByteArray()) {
                @Override
                public String getFilename() {
                    return sanitizeFilename(imageDescription.title());
                }
            };

        } catch (Exception e) {
            log.error("Failed to update XMP metadata for image: {}", image.content().getFilename(), e);
            throw new XmpUpdateException("Failed to update XMP metadata", e);
        }
    }

    private XMPMetadata buildXmp(ImageDescription desc) {
        XMPMetadata xmp = XMPMetadata.createXMPMetadata();
        DublinCoreSchema dc = xmp.createAndAddDublinCoreSchema();
        dc.setTitle(desc.description());
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
        return title.replaceAll("[^a-zA-Z0-9\\-_.]", " ") + ".jpg";
    }
}
