package com.example.Stock.Helper.commands;

import com.example.Stock.Helper.service.ImageSizeService;
import com.example.Stock.Helper.service.OpenAiService;
import com.example.Stock.Helper.service.XmpService;
import org.springframework.core.io.Resource;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

@ShellComponent
public class ImageToStockDataCommand {

    private final OpenAiService openAiService;
    private final ImageSizeService imageSizeService;
    private final XmpService xmpService;

    public ImageToStockDataCommand(OpenAiService descriptionService, ImageSizeService imageSizeService, XmpService xmpService) {
        this.openAiService = descriptionService;
        this.imageSizeService = imageSizeService;
        this.xmpService = xmpService;
    }

    @ShellMethod(key = "image", value = "Generate stock metadata from an image path")
    public void analyzeImages(String pathToImage) throws Exception {

        System.out.println(pathToImage);
        Resource resizedResource = imageSizeService.resizeImage(pathToImage, 500);
        Map<String, Object> metadata = openAiService.generateDescription(resizedResource);
        xmpService.updateXmp(pathToImage, metadata);
    }
}
