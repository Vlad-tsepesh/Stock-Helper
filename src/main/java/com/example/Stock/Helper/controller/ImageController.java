package com.example.Stock.Helper.controller;

import org.springframework.ui.Model;
import com.example.Stock.Helper.service.ImageSizeService;
import com.example.Stock.Helper.service.OpenAiService;
import com.example.Stock.Helper.service.XmpService;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Controller
public class ImageController {

    private final OpenAiService openAiService;
    private final ImageSizeService imageSizeService;
    private final XmpService xmpService;

    public ImageController(OpenAiService openAiService, ImageSizeService imageSizeService, XmpService xmpService) {
        this.openAiService = openAiService;
        this.imageSizeService = imageSizeService;
        this.xmpService = xmpService;
    }

    @GetMapping("/")
    public String index() {
        return "index"; // returns index.html
    }

    @PostMapping("/showFile")
    public String showFile(@RequestParam("file") MultipartFile file, Model model) throws IOException {
        if (file.isEmpty()) {
            model.addAttribute("message", "No file selected!");
            return "result";
        }

//        Resource resizedResource = imageSizeService.resizeImage(path, 500);
//        Map<String, Object> metadata = openAiService.generateDescription(resizedResource);
//        xmpService.updateXmp(path, metadata);


        model.addAttribute("message", "File selected: " + file.getOriginalFilename());
        return "result";
    }
}
