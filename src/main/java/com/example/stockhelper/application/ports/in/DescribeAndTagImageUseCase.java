package com.example.stockhelper.application.ports.in;

import com.example.stockhelper.domain.model.ImageRequest;
import org.springframework.core.io.Resource;

import java.util.List;

public interface DescribeAndTagImageUseCase {
    Resource process(List<ImageRequest> file);
}
