package com.example.Stock.Helper.application.service;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface ImageService {
    Resource changeMetadata(MultipartFile file) throws Exception;
}
