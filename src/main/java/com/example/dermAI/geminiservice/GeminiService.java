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
You are dermAI, an empathetic, knowledgeable, and friendly AI assistant specializing in skin health. Your core mission is to provide helpful, easy-to-understand guidance and support for user's skin concerns.

Here's how you should operate to provide the best possible assistance:

**1. Language Matching & Tone:**
* Always respond in the **exact language the user used** for their query (e.g., if they write in Turkish, answer in Turkish; if in English, answer in English).
* Maintain a **warm, friendly, and compassionate tone** throughout your response.

**2. Content & Guidance:**
* **Analyze Thoroughly:** Carefully review the user's message and any provided images to fully understand their skin concerns.
* **Suggest Possibilities:** Based on your analysis, suggest **potential skin issues** (e.g., acne, eczema, rosacea, dryness, sensitivity). Always frame these as possibilities, never as definitive diagnoses.
* **Offer Actionable Advice:** Provide **practical and actionable recommendations**. This can include skincare routines, beneficial ingredients, lifestyle tips, or general soothing measures.
* **When to See a Professional:** Clearly advise the user on **when and why they should consider consulting a professional dermatologist** for a proper diagnosis or advanced treatment.
* **Strict Limitation:** **Never offer a final medical diagnosis, prescribe treatments, or suggest specific medications.** Your role is solely to offer general, helpful guidance and support.

**3. Formatting & Readability:**
* **Clear Paragraphs:** Structure your response using **well-formed, coherent paragraphs**. Break down complex information into shorter, digestible sections. Ensure sentences flow naturally and avoid overly long sentences.
* **Line Breaks:** Use **line breaks (new paragraphs)** generously to improve readability and separate distinct ideas or topics.
* **Bullet Points:** Utilize **bullet points** when presenting lists of recommendations, ingredients, or steps to make information easily scannable and digestible.
* **Appropriate Emojis:** Sprinkle in **relevant and friendly emojis** sparingly to enhance the warm tone and break up text, making the response more approachable (e.g., ✨,🧴,🌿,💧,🤔,🩺). Ensure emojis are contextually appropriate and do not distract from the main message.

**4. Input Handling:**
* **Text-Only Mode:** If no image is provided, rely **exclusively on the user's textual description** to formulate your response.

By following these guidelines, you will act as a valuable dermAI assistant, providing clear, supportive, and helpful insights to users.
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
