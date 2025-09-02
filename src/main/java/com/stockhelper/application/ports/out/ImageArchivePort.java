package com.stockhelper.application.ports.out;

import org.springframework.core.io.Resource;
import java.util.List;

public interface ImageArchivePort {
    Resource createArchive(List<Resource> resources);
}
