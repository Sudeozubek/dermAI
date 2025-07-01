import com.example.dermAI.geminiservice.GeminiService;
import org.springframework.web.reactive.function.client.WebClient;

public class GeminiTest {
    public static void main(String[] args) {
        GeminiService service = new GeminiService(WebClient.builder());
        String prompt = "Merhaba, nasılsın?";
        System.out.println("Gönderilen Soru: " + prompt);
        String cevap = service.askGemini(prompt);
        System.out.println("Aldığım Cevap: " + cevap);
    }
}
