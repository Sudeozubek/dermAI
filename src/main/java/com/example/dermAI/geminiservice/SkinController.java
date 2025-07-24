package com.example.dermAI.geminiservice;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import java.util.HashMap;
import java.util.Map;

@RestController
public class SkinController {

    @PostMapping("/api/skin/predict")
    public ResponseEntity<?> predictSkin(@RequestBody Map<String, String> body) {
        String imageBase64 = body.get("image");
        String flaskUrl = "http://127.0.0.1:5001/predict";

        RestTemplate restTemplate = new RestTemplate();
        Map<String, String> req = new HashMap<>();
        req.put("image", imageBase64);

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(flaskUrl, req, Map.class);
            return ResponseEntity.ok(response.getBody());
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "Flask API hatası: " + e.getMessage()));
        }
    }
}