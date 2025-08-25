package com.example.stockhelper.infrastructure.ai;

import com.example.stockhelper.application.port.out.ImageDescriptionGeneratorPort;
import com.example.stockhelper.domain.model.ImageDescription;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.content.Media;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.util.MimeTypeUtils;

import java.util.List;

@Component
@RequiredArgsConstructor
public class OpenAiAdapter implements ImageDescriptionGeneratorPort {

    private final OpenAiChatModel chatModel;
    private final String stockDescriptionPrompt;
    private final ObjectMapper objectMapper;

    @Override
    public ImageDescription generateDescription(Resource image) {
        var userMessage = UserMessage.builder().text(stockDescriptionPrompt).media(List.of(new Media(MimeTypeUtils.IMAGE_PNG, image))).build();

        String response = chatModel.call(new Prompt(List.of(userMessage), OpenAiChatOptions.builder().model("gpt-4o").build()).getUserMessage());

        ImageDescription description = parseJson(response);
        validate(description, image);
        return description;
    }

    private ImageDescription parseJson(String json) {
        try {
            return objectMapper.readValue(json, ImageDescription.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to parse OpenAI JSON response", e);
        }
    }

    private void validate(ImageDescription description, Resource image) {
        if (description.title() == null || description.keywords() == null) {
            throw new IllegalStateException("Invalid AI response: missing required fields for image " + image.getFilename());
        }
    }
}
