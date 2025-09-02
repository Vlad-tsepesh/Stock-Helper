package com.stockhelper.api.controller;

import com.stockhelper.application.ports.in.DescribeAndTagImageUseCase;
import com.stockhelper.domain.model.ImageRequest;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class ImageController {

    private static final Logger logger = LoggerFactory.getLogger(ImageController.class);
    private final DescribeAndTagImageUseCase useCase;

    @GetMapping("/")
    public String index() {
        logger.info("GET / - returning index page");
        return "index"; // returns old.html
    }

    @PostMapping("/upload")
    public ResponseEntity<Resource> uploadFiles(MultipartFile[] images) throws IOException {
        logger.info("POST /upload - received {} files", images.length);
        List<ImageRequest> inputImages = Arrays.stream(images)
                .map(file -> {
                    logger.debug("Preparing ImageRequest for file: {}", file.getOriginalFilename());
                    return new ImageRequest(file.getOriginalFilename(), file.getResource());
                })
                .toList();


        Resource zipFile = useCase.process(inputImages);


        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"images.zip\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(zipFile);
    }
}

