package com.example.stockhelper.api.controller;

import com.example.stockhelper.application.ports.in.DescribeAndTagImageUseCase;
import com.example.stockhelper.domain.model.ImageRequest;
import lombok.RequiredArgsConstructor;
import org.apache.tomcat.util.http.fileupload.impl.FileCountLimitExceededException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class ImageController {

    private final DescribeAndTagImageUseCase useCase;
    private static final Logger logger = LoggerFactory.getLogger(ImageController.class);

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


        Resource zipFile;
        try {
            zipFile = useCase.process(inputImages);
            if (zipFile != null) {
                logger.info("ZIP archive created successfully with {} images", inputImages.size());
            } else {
                logger.warn("ZIP archive creation returned null");
            }
        } catch (Exception e) {
            logger.error("Error processing uploaded images", e);
            throw e;
        }

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"images.zip\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(zipFile);
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<String> handleMaxSizeException(MaxUploadSizeExceededException exc) {
        logger.warn("Upload exceeded maximum size: {}", exc.getMessage());
        return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE)
                .body("File size exceeds limit!");
    }

    @ExceptionHandler(IOException.class)
    public ResponseEntity<String> handleIOException(IOException exc) {
        logger.error("IO exception during upload: {}", exc.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Failed to process uploaded files.");
    }

    @ExceptionHandler(MultipartException.class)
    public ResponseEntity<String> handleMultipartException(MultipartException ex) {
        Throwable cause = ex.getCause();
        if (cause instanceof FileCountLimitExceededException fileEx) {
            logger.warn("File upload count limit exceeded: {}", fileEx.getMessage());
            return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE)
                    .body("File upload limit exceeded: " + fileEx.getMessage());
        }
        logger.error("Multipart exception: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("Invalid multipart request");
    }
}
