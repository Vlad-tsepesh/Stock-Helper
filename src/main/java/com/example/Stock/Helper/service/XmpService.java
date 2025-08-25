package com.example.Stock.Helper.service;

import org.apache.commons.imaging.formats.jpeg.xmp.JpegXmpRewriter;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.xmpbox.XMPMetadata;
import org.apache.xmpbox.schema.DublinCoreSchema;
import org.apache.xmpbox.xml.XmpSerializer;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

@Service
public class XmpService {

    public void updateXmp(String input, Map<String, Object> metadata) throws Exception {
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

        Path path = Paths.get(input);
        Path parent = path.getParent();
        Path output = parent.resolve(title + ".jpg");

        try (FileOutputStream fos = new FileOutputStream(String.valueOf(output))) {
            new JpegXmpRewriter().updateXmpXml(new File(input), fos, xmpXml);
        }
    }
}
