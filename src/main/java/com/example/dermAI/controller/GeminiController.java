package com.example.dermAI.controller;

import com.example.dermAI.geminiservice.GeminiPromptRequest;
import com.example.dermAI.geminiservice.GeminiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/gemini")
public class GeminiController {

    private final GeminiService geminiService;

    @Autowired
    public GeminiController(GeminiService geminiService) {
        this.geminiService = geminiService;
    }

    @PostMapping("/ask")
    public String askGemini(@RequestBody GeminiPromptRequest request) {
        return geminiService.askGemini(request.getPrompt());
    }
}
