package com.example.Stock.Helper.model.service;

import com.example.Stock.Helper.model.record.ImageDescription;
import org.apache.commons.imaging.formats.jpeg.xmp.JpegXmpRewriter;
import java.io.ByteArrayOutputStream;
import org.apache.xmpbox.XMPMetadata;
import org.apache.xmpbox.schema.DublinCoreSchema;
import org.apache.xmpbox.xml.XmpSerializer;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@Service
public class XmpService {

    public Resource updateXmp(MultipartFile file, ImageDescription imageDescription) throws Exception {

        XMPMetadata xmp = XMPMetadata.createXMPMetadata();
        DublinCoreSchema dc = xmp.createAndAddDublinCoreSchema();

        dc.setDescription(imageDescription.description());
        dc.setTitle(imageDescription.title());

        for (String keyword : imageDescription.keywords()) {
            dc.addSubject(keyword);
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        new XmpSerializer().serialize(xmp, baos, true);
        String xmpXml = baos.toString(StandardCharsets.UTF_8);


        // Write updated XMP to a new byte array
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try (InputStream is = file.getInputStream()) {
            new JpegXmpRewriter().updateXmpXml(is, outputStream, xmpXml);
        }

        // Return as in-memory resource
        return new ByteArrayResource(outputStream.toByteArray()) {
            @Override
            public String getFilename() {
                return imageDescription.title() + ".jpg";
            }
        };
    }
}
