/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.Stock.Helper;

import org.apache.xmlgraphics.xmp.Metadata;
import org.apache.xmlgraphics.xmp.XMPSchema;
import java.io.FileOutputStream;

import org.apache.commons.imaging.Imaging;
import org.apache.commons.imaging.ImagingException;
import org.apache.commons.imaging.common.ImageMetadata;
import org.apache.commons.imaging.formats.jpeg.JpegImageMetadata;
import org.apache.commons.imaging.formats.jpeg.exif.ExifRewriter;
import org.apache.commons.imaging.formats.tiff.TiffImageMetadata;
import org.apache.commons.imaging.formats.tiff.constants.MicrosoftTagConstants;
import org.apache.commons.imaging.formats.tiff.write.TiffOutputDirectory;
import org.apache.commons.imaging.formats.tiff.write.TiffOutputSet;

import java.io.*;

public class WriteExifMetadataExample {

    public static void main(String[] args) throws ImagingException, IOException {
        new WriteExifMetadataExample().setExifGPSTag();
    }

    public void setExifGPSTag() throws IOException, ImagingException {
        String input = "C:\\Users\\bcris\\OneDrive\\OneSyncFiles\\Java\\Stock-Helper\\src\\main\\resources\\planet.jpg";
        String output = "C:\\Users\\bcris\\OneDrive\\OneSyncFiles\\Java\\Stock-Helper\\src\\main\\resources\\new-planet.jpg";
        try (FileOutputStream fos = new FileOutputStream(output); OutputStream os = new BufferedOutputStream(fos)) {
            TiffOutputSet outputSet = null;

            // note that metadata might be null if no metadata is found.
            final ImageMetadata metadata = Imaging.getMetadata(new File(input));
            final JpegImageMetadata jpegMetadata = (JpegImageMetadata) metadata;
            if (null != jpegMetadata) {
                final TiffImageMetadata exif = jpegMetadata.getExif();

                if (null != exif) {

                    outputSet = exif.getOutputSet();
                }
            }

            if (null == outputSet) {
                outputSet = new TiffOutputSet();
            }

            TiffOutputDirectory exifDir = outputSet.getOrCreateExifDirectory();


            exifDir.add(MicrosoftTagConstants.EXIF_TAG_XPKEYWORDS, "Keywords");
            exifDir.add(MicrosoftTagConstants.EXIF_TAG_XPTITLE, "Title");
            exifDir.add(MicrosoftTagConstants.EXIF_TAG_XPCOMMENT, "Comment");

            new ExifRewriter().updateExifMetadataLossless(new File(input), os, outputSet);
        }
    }
}