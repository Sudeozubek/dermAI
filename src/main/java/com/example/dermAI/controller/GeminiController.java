package com.example.dermAI.controller;

import com.example.dermAI.geminiservice.GeminiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/gemini")
public class GeminiController {

    @Autowired
    private GeminiService geminiService;

    @PostMapping("/ask")
    public String askGemini(@RequestBody String prompt) {
        return "Gemini received your prompt: " + prompt;//geçici
    }
}
