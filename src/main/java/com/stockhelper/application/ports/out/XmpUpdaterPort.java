package com.stockhelper.application.ports.out;

import com.stockhelper.domain.model.ImageRequest;
import com.stockhelper.domain.model.ImageDescription;
import org.springframework.core.io.Resource;

public interface XmpUpdaterPort {
    Resource updateXmp(ImageRequest file, ImageDescription description);
}
