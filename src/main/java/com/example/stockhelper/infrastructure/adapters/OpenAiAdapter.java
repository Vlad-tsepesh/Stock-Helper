package com.example.stockhelper.infrastructure.adapters;

import com.example.stockhelper.application.ports.out.ImageDescriptionGeneratorPort;
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.ResourceAccessException;

@Component
@RequiredArgsConstructor
public class OpenAiAdapter implements ImageDescriptionGeneratorPort {
    private static final Logger log = LoggerFactory.getLogger(OpenAiAdapter.class);

    private final OpenAiChatModel chatModel;
    private final String stockDescriptionPrompt;
    private final ObjectMapper objectMapper;

    @Override
    public ImageDescription generateDescription(Resource image) {

        log.debug("Using stockDescriptionPrompt: {}", stockDescriptionPrompt);
        var userMessage = UserMessage.builder().text(stockDescriptionPrompt).media(List.of(new Media(MimeTypeUtils.IMAGE_PNG, image))).build();
        log.debug("Calling OpenAI chat model with prompt...");
        String response;
        try {
            response = chatModel.call(new Prompt(List.of(userMessage), OpenAiChatOptions.builder().model("gpt-4o").build()).getUserMessage());
        }catch (ResourceAccessException e) {
            log.error("I/O error when calling OpenAI API", e);
            response = "";
        }
        log.debug("Received AI response: {}", response);
        return parseJson(response);
    }

    private ImageDescription parseJson(String json) {
        try {
            log.info("Parsed AI response successfully into ImageDescription");
            return objectMapper.readValue(json, ImageDescription.class);
        } catch (JsonProcessingException e) {
            log.error("Failed to parse AI response JSON: {}", json, e);
            throw new RuntimeException("Failed to parse AI response", e);
        }
    }
}
