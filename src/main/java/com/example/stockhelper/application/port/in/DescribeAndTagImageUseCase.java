package com.example.stockhelper.application.port.in;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface DescribeAndTagImageUseCase {
    Resource process(MultipartFile file);
}
