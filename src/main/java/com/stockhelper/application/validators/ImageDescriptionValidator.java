package com.stockhelper.application.validators;

import com.stockhelper.domain.model.ImageDescription;
import org.springframework.stereotype.Component;

@Component
public class ImageDescriptionValidator {

    public void validate(ImageDescription desc) {
        if (desc == null) throw new IllegalArgumentException("ImageDescription cannot be null");
        if (desc.title() == null || desc.title().isBlank()) throw new IllegalArgumentException("Title cannot be empty");
        if (desc.description() == null || desc.description().isBlank()) throw new IllegalArgumentException("Description cannot be empty");
        if (desc.keywords() == null || desc.keywords().size() < 10)
            throw new IllegalArgumentException("Keywords must contain at least 10 entries");
    }
}
