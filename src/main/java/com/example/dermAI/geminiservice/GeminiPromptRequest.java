package com.example.dermAI.geminiservice;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
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

    private static final String SYSTEM_PROMPT = """
You are a dermatologist AI assistant named dermAI.

Your responsibilities:
1. Analyze the user's skin concerns based on their message or image.
2. Suggest possible skin issues (e.g., acne, eczema, rosacea).
3. Recommend routines, skincare ingredients, or when to see a dermatologist.
4. Never make a final diagnosis; only give helpful guidance.
5. Respond in a warm, friendly and structured tone.
6. If no image is uploaded, rely solely on the text description.
7. Always respond in the same language as the user.
8. Format your response as clearly structured paragraphs or numbered bullet points to ensure easy readability.
If user writes in Turkish, answer in Turkish. If user writes in English, answer in English.
""";

    public String askGemini(String userMessage) {
        //Kullanıcı mesajıyla prompt'u birleştir
        String fullPrompt = SYSTEM_PROMPT + "\n\nUser: " + userMessage;

        Map<String, Object> body = Map.of(
                "contents", List.of(
                        Map.of(
                                "parts", List.of(
                                        Map.of("text", fullPrompt)
                                )
                        )
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

    public String askGeminiWithImage(String userMessage, String imageBase64) {
        try {
            //Base64 verisini ayıkla
            String base64Data = imageBase64;
            if (base64Data.contains(",")) {
                base64Data = base64Data.substring(base64Data.indexOf(",") + 1);
            }

            String endpoint = "https://generativelanguage.googleapis.com/v1/models/gemini-1.5-flash:generateContent?key=" + apiKey;

            //Görsel + metin ile prompt
            String fullPrompt = SYSTEM_PROMPT + "\n\nUser: " + userMessage;

            String json = "{\n" +
                    "  \"contents\": [\n" +
                    "    {\n" +
                    "      \"parts\": [\n" +
                    "        { \"text\": \"" + fullPrompt.replace("\"", "\\\"") + "\" },\n" +
                    "        { \"inline_data\": { \"mime_type\": \"image/png\", \"data\": \"" + base64Data + "\" } }\n" +
                    "      ]\n" +
                    "    }\n" +
                    "  ]\n" +
                    "}";

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(endpoint))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();

            HttpClient client = HttpClient.newHttpClient();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            return response.body();

        } catch (Exception e) {
            e.printStackTrace();
            return "Görsel analizi sırasında hata oluştu: " + e.getMessage();
        }
    }
}
