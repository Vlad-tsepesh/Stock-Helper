package com.example.stockhelper.application.port.out;

import com.example.stockhelper.domain.model.ImageDescription;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface XmpUpdaterPort {
    Resource updateXmp(MultipartFile file, ImageDescription description);
}
