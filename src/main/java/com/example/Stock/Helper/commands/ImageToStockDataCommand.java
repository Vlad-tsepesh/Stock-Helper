package com.example.Stock.Helper.commands;

import com.example.Stock.Helper.application.service.ImageService;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

@ShellComponent
public class ImageToStockDataCommand {

    private final ImageService imageService;

    public ImageToStockDataCommand(ImageService imageService) {
        this.imageService = imageService;
    }

    @ShellMethod(key = "image", value = "Generate stock metadata from an image path")
    public void setPathToImage(String pathToImage) throws Exception {

        imageService.addMetadata(pathToImage);
    }
}