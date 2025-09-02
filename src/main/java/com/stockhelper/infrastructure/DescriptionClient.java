package com.stockhelper.infrastructure;

import com.stockhelper.application.ports.out.ImageDescriptionGeneratorPort;
import com.stockhelper.domain.model.ImageDescription;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@RequiredArgsConstructor
public class DescriptionClient implements ImageDescriptionGeneratorPort {

    private final WebClient webClient;

    public ImageDescription generateDescription(Resource resource) {

        MultipartBodyBuilder builder = new MultipartBodyBuilder();
        builder.part("image", resource)
                .filename("resized.png") // WebClient requires a filename for multipart
                .contentType(MediaType.IMAGE_PNG);

        return webClient.post()
                .uri("https://description-service.onrender.com/generate-description") // your microservice endpoint
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .bodyValue(builder.build())
                .retrieve()
                .bodyToMono(ImageDescription.class)
                .block(); // blocking for simplicity; remove in fully reactive flow
    }
}
