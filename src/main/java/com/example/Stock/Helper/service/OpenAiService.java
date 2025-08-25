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

    public OpenAiService(OpenAiChatModel chatModel) {
        this.chatModel = chatModel;
    }

    public Map<String, Object> generateDescription(Resource image) {
        String prompt = """
                Generates professional stock descriptions for Adobe Stock, with titles, descriptions, and keywords.
                    Instructions: This GPT will provide professional image descriptions for stock image submissions to Adobe Stock. It will generate concise, clear titles, descriptions, and keywords in English, aimed at selling the images effectively on stock websites. The title should be between 1 to 7 words, the description should be no longer than 200 characters, and the keywords must be between 30 and 48, separated by commas. The descriptions will follow best practices used by top stock contributors, emphasizing relevance, color, composition, and potential commercial usage. It will focus on universal appeal and visual storytelling, avoiding overuse of technical terms or subjective interpretations. The goal is to describe the image in a way that appeals to both creative professionals and commercial buyers, ensuring wide usability. Keywords will be carefully chosen to maximize visibility in search results and cover various use cases for the image. The tone will be professional but approachable, guiding the user smoothly through each step of the process.
                    Output a **valid JSON object only**, with exactly these keys:
                    - "title": a short descriptive title for the image
                    - "description": a detailed description of the image
                    - "keywords": an array of keywords relevant to the image
                    Do NOT include any extra text, explanations, markdown, or code fences.
                    The JSON must start with { and end with } and be directly parseable.
            """;

        var userMessage = UserMessage.builder()
                .text(prompt)
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
