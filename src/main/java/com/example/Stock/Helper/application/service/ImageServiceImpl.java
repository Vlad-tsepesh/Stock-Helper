package com.example.Stock.Helper.application.service;

import com.example.Stock.Helper.model.record.ImageDescription;
import com.example.Stock.Helper.model.service.ImageSizeService;
import com.example.Stock.Helper.model.service.OpenAiService;
import com.example.Stock.Helper.model.service.XmpService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class ImageServiceImpl implements ImageService{

    private final OpenAiService openAiService;
    private final ImageSizeService imageSizeService;
    private final XmpService xmpService;

    @Override
    public Resource changeMetadata(MultipartFile file) throws Exception {
        System.out.println(file.getOriginalFilename());
        Resource resizedResource = imageSizeService.resizeImage(file, 500);
        ImageDescription imageDescription = openAiService.generateDescription(resizedResource);
        return xmpService.updateXmp(file, imageDescription);
    }
}
