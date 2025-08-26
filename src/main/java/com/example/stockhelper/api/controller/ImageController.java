package com.example.stockhelper.api.controller;

import com.example.stockhelper.application.port.in.DescribeAndTagImageUseCase;
import com.example.stockhelper.domain.model.ImageRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Controller
@RequiredArgsConstructor
public class ImageController {

    private final DescribeAndTagImageUseCase useCase;

    @GetMapping("/")
    public String index() {
        return "index"; // returns index.html
    }

    @PostMapping("/upload")
    public ResponseEntity<Resource> uploadFiles(MultipartFile[] images) throws IOException {
        List<ImageRequest> inputImages = Arrays.stream(images)
                .map(file -> new ImageRequest(file.getOriginalFilename(), file.getResource()))
                .toList();


        Resource zipFile = useCase.process(inputImages);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"images.zip\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(zipFile);
    }
}
