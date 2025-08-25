package com.example.Stock.Helper.model.service;

import com.example.Stock.Helper.model.record.ImageDescription;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.content.Media;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.MimeTypeUtils;

import java.util.List;

@Service
public class OpenAiService {

    private final OpenAiChatModel chatModel;
    private final String stockDescriptionPrompt;
    private final ObjectMapper objectMapper;

    public OpenAiService(OpenAiChatModel chatModel, String stockDescriptionPrompt, ObjectMapper objectMapper) {
        this.chatModel = chatModel;
        this.stockDescriptionPrompt = stockDescriptionPrompt;
        this.objectMapper = objectMapper;
    }

    public ImageDescription generateDescription(Resource image) {

        var userMessage = UserMessage.builder().text(stockDescriptionPrompt).media(List.of(new Media(MimeTypeUtils.IMAGE_PNG, image))).build();

        String response = chatModel.call(new Prompt(List.of(userMessage), OpenAiChatOptions.builder().model("gpt-4o").build()).getUserMessage());

        ImageDescription description = jsonParser(response);

        validateDescription(description, image);

        return description;
    }

    private ImageDescription jsonParser(String jsonString) {
        try {
            return objectMapper.readValue(jsonString, ImageDescription.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to parse OpenAI JSON response", e);
        }
    }

    private void validateDescription(ImageDescription description, Resource image) {
        if (description.title() == null || description.keywords() == null) {
            throw new IllegalStateException("Invalid AI response: missing required fields for image" + image.getFilename());
        }
    }

}
