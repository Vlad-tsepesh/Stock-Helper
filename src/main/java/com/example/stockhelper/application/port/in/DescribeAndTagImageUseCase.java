package com.example.stockhelper.application.port.in;

import com.example.stockhelper.domain.model.ImageRequest;
import org.springframework.core.io.Resource;

public interface DescribeAndTagImageUseCase {
    Resource process(ImageRequest file);
}
