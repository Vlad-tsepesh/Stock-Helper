package com.example.Stock.Helper.commands;

import com.example.Stock.Helper.ImageResizer;
import com.example.Stock.Helper.service.StockDescriptionService;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.core.io.Resource;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

import java.io.IOException;

@ShellComponent
public class ImageToStockDataCommand {

    private final StockDescriptionService descriptionService;

    public ImageToStockDataCommand(StockDescriptionService descriptionService) {
        this.descriptionService = descriptionService;
    }

    @ShellMethod(key = "generate-metadata", value = "Generate stock metadata from an image path")
    public ChatResponse analyzeImages(@ShellOption(defaultValue = "C:\\Users\\bcris\\OneDrive\\OneSyncFiles\\Java\\Stock-Helper\\src\\main\\resources\\bike.jpg") String arg) throws IOException {
        Resource resizedResource = ImageResizer.resize(arg, 500);
        return descriptionService.generateDescription(resizedResource);
    }
}
