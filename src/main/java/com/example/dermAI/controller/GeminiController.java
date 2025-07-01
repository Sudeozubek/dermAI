package com.example.dermAI.controller;

import com.example.dermAI.geminiservice.GeminiService;
import com.example.dermAI.geminiservice.GeminiPromptRequest;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/gemini")
public class GeminiController {

    private final GeminiService geminiService;

    // Constructor Injection
    public GeminiController(GeminiService geminiService) {
        this.geminiService = geminiService;
    }

    @PostMapping("/ask")
    public String askGemini(@RequestBody GeminiPromptRequest request) {
        String prompt = request.getPrompt();
        String imageBase64 = request.getImage();

        // Log ekle
        System.out.println("Gelen prompt: " + prompt);
        if (imageBase64 != null && !imageBase64.isEmpty()) {
            System.out.println("Gelen image base64 uzunluğu: " + imageBase64.length());
            System.out.println("Base64 başlangıcı: " + imageBase64.substring(0, Math.min(100, imageBase64.length())));
        } else {
            System.out.println("Görsel gelmedi.");
        }

        if (imageBase64 == null || imageBase64.isEmpty()) {
            return geminiService.askGemini(prompt);
        }

        return geminiService.askGeminiWithImage(prompt, imageBase64);
    }

}
