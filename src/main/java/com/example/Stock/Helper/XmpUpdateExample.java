package com.example.Stock.Helper;

import org.apache.commons.imaging.formats.jpeg.xmp.JpegXmpRewriter;
import org.apache.xmpbox.XMPMetadata;
import org.apache.xmpbox.schema.DublinCoreSchema;
import org.apache.xmpbox.xml.XmpSerializer;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class XmpUpdateExample {
    public static void main(String[] args) throws Exception {
        String in = "C:\\Users\\bcris\\OneDrive\\OneSyncFiles\\Java\\Stock-Helper\\src\\main\\resources\\waterfall.jpg";
        String out = "C:\\Users\\bcris\\OneDrive\\OneSyncFiles\\Java\\Stock-Helper\\src\\main\\resources\\new-waterfall.jpg";

        File input = new File(in);
        File output = new File(out);

        // Build XMP with XmpBox
        XMPMetadata xmp = XMPMetadata.createXMPMetadata();
        DublinCoreSchema dc = xmp.createAndAddDublinCoreSchema();
        dc.setDescription("description");
        dc.addCreator("Vladimir");
        dc.setTitle("newTitle");

        // Add keywords
        List<String> keywords = List.of("bike", "sport", "racing");
        for (String keyword : keywords) {
            dc.addSubject(keyword);
        }

        // Serialize XMP to string
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        new XmpSerializer().serialize(xmp, baos, true);
        String xmpXml = baos.toString(StandardCharsets.UTF_8); // Correct way

        // Replace XMP in JPEG
        try (FileOutputStream fos = new FileOutputStream(output)) {
            new JpegXmpRewriter().updateXmpXml(input, fos, xmpXml);
        }

        System.out.println("XMP updated successfully!");
    }
}
