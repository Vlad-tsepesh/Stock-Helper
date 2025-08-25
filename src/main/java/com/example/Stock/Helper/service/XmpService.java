package com.example.Stock.Helper.service;

import org.apache.commons.imaging.formats.jpeg.xmp.JpegXmpRewriter;
import java.io.ByteArrayOutputStream;
import org.apache.xmpbox.XMPMetadata;
import org.apache.xmpbox.schema.DublinCoreSchema;
import org.apache.xmpbox.xml.XmpSerializer;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


import java.io.FileOutputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

@Service
public class XmpService {

    public Resource updateXmp(MultipartFile file, Map<String, Object> metadata) throws Exception {
        String title = (String) metadata.get("title");
        String description = (String) metadata.get("description");
        List<String> keywords = (List<String>) metadata.get("keywords");

        XMPMetadata xmp = XMPMetadata.createXMPMetadata();
        DublinCoreSchema dc = xmp.createAndAddDublinCoreSchema();
        dc.setDescription(description);
        dc.setTitle(title);

        for (String keyword : keywords) {
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
                return title + ".jpg";
            }
        };
    }
}
