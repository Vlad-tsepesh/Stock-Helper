package com.example.Stock.Helper.application.service;

import com.example.Stock.Helper.service.ImageSizeService;
import com.example.Stock.Helper.service.OpenAiService;
import com.example.Stock.Helper.service.XmpService;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class ImageServiceImpl implements ImageService{

    private final OpenAiService openAiService;
    private final ImageSizeService imageSizeService;
    private final XmpService xmpService;

    public ImageServiceImpl(OpenAiService openAiService, ImageSizeService imageSizeService, XmpService xmpService) {
        this.openAiService = openAiService;
        this.imageSizeService = imageSizeService;
        this.xmpService = xmpService;
    }


    @Override
    public void addMetadata(String pathToImage) throws Exception {
        System.out.println(pathToImage);
        Resource resizedResource = imageSizeService.resizeImage(pathToImage, 500);
        Map<String, Object> metadata = openAiService.generateDescription(resizedResource);
        xmpService.updateXmp(pathToImage, metadata);
    }
}
