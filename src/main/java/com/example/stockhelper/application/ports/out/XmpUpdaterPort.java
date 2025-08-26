package com.example.stockhelper.application.ports.out;

import com.example.stockhelper.domain.model.ImageRequest;
import com.example.stockhelper.domain.model.ImageDescription;
import org.springframework.core.io.Resource;

public interface XmpUpdaterPort {
    Resource updateXmp(ImageRequest file, ImageDescription description);
}
