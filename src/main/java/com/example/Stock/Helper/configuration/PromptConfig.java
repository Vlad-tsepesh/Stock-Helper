package com.example.Stock.Helper.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.beans.factory.annotation.Value;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Configuration
public class PromptConfig {

    @Value("classpath:prompts/stock_description_prompt.txt")
    private Resource stockPromptResource;

    @Bean
    public String stockDescriptionPrompt() throws IOException {
        return new String(stockPromptResource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
    }
}
