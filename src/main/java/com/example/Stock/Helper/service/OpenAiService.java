package com.example.Stock.Helper.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.content.Media;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.MimeTypeUtils;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Service
public class OpenAiService {

    private final OpenAiChatModel chatModel;
    private final String stockDescriptionPrompt;

    public OpenAiService(OpenAiChatModel chatModel, String stockDescriptionPrompt) {
        this.chatModel = chatModel;
        this.stockDescriptionPrompt = stockDescriptionPrompt;
    }

    public Map<String, Object> generateDescription(Resource image) {

        var userMessage = UserMessage.builder()
                .text(stockDescriptionPrompt)
                .media(List.of(new Media(MimeTypeUtils.IMAGE_PNG, image)))
                .build();

         String response = chatModel.call(
                new Prompt(List.of(userMessage),
                        OpenAiChatOptions.builder().model("gpt-4o").build()).getUserMessage()
        );

         return jsonParser(response);
    }

    private Map<String, Object> jsonParser(String jsonString) {
        ObjectMapper objectMapper = new ObjectMapper();

        Map<String, Object> metadata = null;
        try {
            // Convert JSON string to Map
            metadata = objectMapper.readValue(jsonString, Map.class);

        } catch (
                IOException e) {
            e.printStackTrace();
        }

        return metadata;
    }
}
