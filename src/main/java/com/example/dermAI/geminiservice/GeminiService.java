package com.example.dermAI.geminiservice;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;
import java.util.List;

@Service
public class GeminiService {

    @Value("${gemini.api.key}")
    private String apiKey;

    private final WebClient webClient;

    public GeminiService(WebClient.Builder builder) {
        this.webClient = builder
                .baseUrl("https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-pro:generateContent")
                .build();
    }

    public String askGemini(String prompt) {
        Map<String, Object> body = Map.of(
                "contents", List.of(
                        Map.of("parts", List.of(
                                Map.of("text", prompt)
                        ))
                )
        );

        try {
            String rawResponse = webClient.post()
                    .uri(uriBuilder -> uriBuilder.queryParam("key", apiKey).build())
                    .bodyValue(body)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
            System.out.println("🎯 RAW RESPONSE:");
            System.out.println(rawResponse);

            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(rawResponse);

            return jsonNode
                    .path("candidates")
                    .get(0)
                    .path("content")
                    .path("parts")
                    .get(0)
                    .path("text")
                    .asText();

        } catch (Exception e) {
            System.err.println("❌ GeminiService HATASI: " + e.getMessage());
            return "Hata oluştu: " + e.getMessage();
        }


    }
}
