package com.example.dermAI.controller;

import com.example.dermAI.geminiservice.GeminiService;
import com.example.dermAI.geminiservice.GeminiPromptRequest;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import java.util.Map;
import com.example.dermAI.geminiservice.SkinController;
import org.springframework.web.client.RestTemplate;
import java.util.HashMap;

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

    @PostMapping("/ai/diagnose")
    public ResponseEntity<?> diagnose(@RequestBody Map<String, String> body) {
        String base64Image = body.get("image");
        String flaskUrl = "http://127.0.0.1:5001/predict";

        RestTemplate restTemplate = new RestTemplate();
        Map<String, String> req = new HashMap<>();
        req.put("image", base64Image);

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(flaskUrl, req, Map.class);
            return ResponseEntity.ok(response.getBody());
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "Flask API hatası: " + e.getMessage()));
        }
    }

}
