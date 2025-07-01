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
        System.out.println("Controller received prompt: " + request.getPrompt());  // BURAYA EKLE
        String response = geminiService.askGemini(request.getPrompt());
        System.out.println("Controller sending response: " + response);  // BURAYA EKLE
        return response;
    }

}
