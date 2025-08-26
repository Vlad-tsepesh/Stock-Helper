package com.example.stockhelper.application.port.out;

import org.springframework.core.io.Resource;
import java.util.List;

public interface ImageArchivePort {
    Resource createArchive(List<Resource> resources);
}
