package com.example.Stock.Helper.service;

import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.content.Media;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.MimeTypeUtils;

import java.util.List;

@Service
public class StockDescriptionService {

    private final OpenAiChatModel chatModel;

    public StockDescriptionService(OpenAiChatModel chatModel) {
        this.chatModel = chatModel;
    }

    public ChatResponse generateDescription(Resource image) {
        String prompt = """
            Generates professional stock descriptions for Adobe Stock, with titles, descriptions, and keywords.
            Instructions: ...
            Output: Output should be a JSON with (title, description, keywords).
            """;

        var userMessage = UserMessage.builder()
                .text(prompt)
                .media(List.of(new Media(MimeTypeUtils.IMAGE_PNG, image)))
                .build();

        return chatModel.call(
                new Prompt(List.of(userMessage),
                        OpenAiChatOptions.builder().model("gpt-4o").build())
        );
    }
}
