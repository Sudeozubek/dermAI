package com.example.dermAI.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.beans.factory.annotation.Value;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/sephora")
public class SephoraController {

    @Value("${sephora.api.key:3540611e10msh3a4fe4c1fc293ccp10358ejsn784fc89dbfff}")
    private String apiKey;

    private final RestTemplate restTemplate = new RestTemplate();

    @GetMapping("/products")
    public ResponseEntity<?> getProducts(@RequestParam String query, 
                                       @RequestParam(defaultValue = "6") int pageSize,
                                       @RequestParam(defaultValue = "1") int currentPage) {
        try {
            System.out.println("Sephora products endpoint çağrıldı - Query: " + query + ", PageSize: " + pageSize + ", CurrentPage: " + currentPage);
            
            String url = String.format("https://sephora.p.rapidapi.com/us/products/v2/search?q=%s&pageSize=%d&currentPage=%d",
                    query, pageSize, currentPage);

            // Headers
            org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
            headers.set("x-rapidapi-key", apiKey);
            headers.set("x-rapidapi-host", "sephora.p.rapidapi.com");

            org.springframework.http.HttpEntity<String> entity = new org.springframework.http.HttpEntity<>(headers);

            ResponseEntity<Map> response = restTemplate.exchange(url, 
                    org.springframework.http.HttpMethod.GET, 
                    entity, 
                    Map.class);

            System.out.println("Sephora API yanıtı başarılı: " + response.getBody());
            return ResponseEntity.ok(response.getBody());

        } catch (Exception e) {
            System.err.println("Sephora API Hatası: " + e.getMessage());
            e.printStackTrace();
            Map<String, String> error = new HashMap<>();
            error.put("error", "Sephora API'den veri alınamadı: " + e.getMessage());
            return ResponseEntity.status(500).body(error);
        }
    }

    @GetMapping("/test")
    public ResponseEntity<?> testApi() {
        try {
            String url = "https://sephora.p.rapidapi.com/us/products/v2/search?q=moisturizer&pageSize=6&currentPage=1";

            org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
            headers.set("x-rapidapi-key", apiKey);
            headers.set("x-rapidapi-host", "sephora.p.rapidapi.com");

            org.springframework.http.HttpEntity<String> entity = new org.springframework.http.HttpEntity<>(headers);

            ResponseEntity<Map> response = restTemplate.exchange(url, 
                    org.springframework.http.HttpMethod.GET, 
                    entity, 
                    Map.class);

            return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "Sephora API çalışıyor",
                "products", response.getBody().get("products") // Ürünleri direkt döndür
            ));

        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of(
                "status", "error",
                "message", "Sephora API hatası: " + e.getMessage()
            ));
        }
    }
} 