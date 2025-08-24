package com.example.Stock.Helper.controller;

import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.content.Media;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
public class ChatController {

    private final OpenAiChatModel chatModel;

    @Autowired
    public ChatController(OpenAiChatModel chatModel) {
        this.chatModel = chatModel;
    }

    @GetMapping("/ai/generate")
    public Map<String,String> generate(@RequestParam(value = "message", defaultValue = "Tell me a joke") String message) {
        return Map.of("generation", this.chatModel.call(message));
    }

    @GetMapping("/ai/generateDescription")
	public ChatResponse generateMetadata(@RequestParam(value = "path", defaultValue = "/bike.jpg") String path) {

        var imageData = new ClassPathResource(path);
        String prompt = """
    Generates professional stock descriptions for Adobe Stock, with titles, descriptions, and keywords.
    Instructions: This GPT will provide professional image descriptions for stock image submissions to Adobe Stock. It will generate concise, clear titles, descriptions, and keywords in English, aimed at selling the images effectively on stock websites. The title should be between 1 to 7 words, the description should be no longer than 200 characters, and the keywords should be between 20 and 48, separated by commas. The descriptions will follow best practices used by top stock contributors, emphasizing relevance, color, composition, and potential commercial usage. It will focus on universal appeal and visual storytelling, avoiding overuse of technical terms or subjective interpretations. The goal is to describe the image in a way that appeals to both creative professionals and commercial buyers, ensuring wide usability. Keywords will be carefully chosen to maximize visibility in search results and cover various use cases for the image. The tone will be professional but approachable, guiding the user smoothly through each step of the process.
    Output: Output should be a JSON with (title, description, keywords) as a key and the result of the prompt for the specific key as a value.
    """;

        var userMessage = UserMessage.builder()
                .text(prompt)
                .media(List.of(new Media(MimeTypeUtils.IMAGE_PNG, imageData)))
                .build();

        return this.chatModel
                .call(new Prompt(List.of(userMessage), OpenAiChatOptions.builder().model("gpt-4o").build()));
    }
}