package com.example.Stock.Helper.application.service;

import com.example.Stock.Helper.service.ImageSizeService;
import com.example.Stock.Helper.service.OpenAiService;
import com.example.Stock.Helper.service.XmpService;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

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
    public Resource changeMetadata(MultipartFile file) throws Exception {
        System.out.println(file.getOriginalFilename());
        Resource resizedResource = imageSizeService.resizeImage(file, 500);
        Map<String, Object> metadata = openAiService.generateDescription(resizedResource);
        return xmpService.updateXmp(file, metadata);
    }

}
