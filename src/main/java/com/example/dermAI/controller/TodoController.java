package com.example.dermAI.controller;

import com.example.dermAI.geminiservice.GeminiService;
import org.springframework.beans.factory.annotation.Autowired;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.example.dermAI.entity.Routine;
import com.example.dermAI.entity.User;
import com.example.dermAI.dao.RoutineRepository;
import com.example.dermAI.dao.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import java.time.LocalDateTime;
import java.util.Optional;
import org.springframework.http.ResponseEntity;

@Controller
public class TodoController {

    private final GeminiService geminiService;

    @Autowired
    private RoutineRepository routineRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    public TodoController(GeminiService geminiService) {
        this.geminiService = geminiService;
    }

    @PostMapping("/todo")
    public String createRoutine(@RequestParam("skin_type") String skinType,
                                @RequestParam("urunler") String urunlerJson,
                                Model model) {
        try {
            // Loginli kullanıcıyı bul
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String username = auth.getName();
            User user = userRepository.findByUsername(username).orElse(null);
            if (user == null) {
                model.addAttribute("routine", "Kullanıcı bulunamadı.");
                return "todo";
            }

            // Son 7 gün içinde oluşturulmuş rutin var mı?
            LocalDateTime oneWeekAgo = LocalDateTime.now().minusDays(7);
            Optional<Routine> recentRoutineOpt = routineRepository.findFirstByUserAndCreatedAtAfterOrderByCreatedAtDesc(user, oneWeekAgo);

            if (recentRoutineOpt.isPresent()) {
                model.addAttribute("routine", recentRoutineOpt.get().getRoutineText());
            } else {
                // Ürünleri JSON stringinden listeye çevir
                ObjectMapper objectMapper = new ObjectMapper();
                List<String> urunler = objectMapper.readValue(urunlerJson, List.class);

                // Gemini promptunu hazırla
                StringBuilder prompt = new StringBuilder();
                prompt.append("Kullanıcının cilt tipi: ").append(skinType).append(". ");
                prompt.append("Elindeki cilt bakım ürünleri: ");
                for (String urun : urunler) {
                    prompt.append(urun).append(", ");
                }
                prompt.append("\nLütfen aşağıdaki formatta, haftalık bir cilt bakım rutini öner: ");
                prompt.append("Her gün için ayrı kutu olacak şekilde, sabah ve akşam ürünlerini sıralı ve madde madde yaz. Sadece aşağıdaki gibi yaz, açıklama ekleme:\n\n");
                prompt.append("PAZARTESİ\nSABAH:\n- ürün1\n- ürün2\nAKŞAM:\n- ürün3\n- ürün4\n\nSALI\nSABAH:\n- ürün1\n...\n\nSadece bu formatta, tablo veya açıklama ekleme.");

                String routineText = geminiService.askGemini(prompt.toString());

                // Yeni rutin kaydet
                Routine routine = new Routine();
                routine.setUser(user);
                routine.setRoutineText(routineText);
                routine.setCreatedAt(LocalDateTime.now());
                routineRepository.save(routine);

                model.addAttribute("routine", routineText);
                model.addAttribute("newRoutine", true); // Yeni rutin işareti
            }

            // Profil bilgilerini ekle (hem GET hem POST için)
            model.addAttribute("user", user);
            model.addAttribute("puan", user.getPuan());

        } catch (Exception e) {
            model.addAttribute("routine", "Bir hata oluştu: " + e.getMessage());
        }
        return "todo";
    }

    @PostMapping("/todo/delete")
    public ResponseEntity<String> deleteRoutine() {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String username = auth.getName();
            User user = userRepository.findByUsername(username).orElse(null);
            
            if (user == null) {
                return ResponseEntity.status(401).body("Kullanıcı bulunamadı");
            }
            
            // Kullanıcının tüm rutinlerini sil
            routineRepository.deleteByUser(user);
            
            return ResponseEntity.ok("Rutin silindi");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Hata: " + e.getMessage());
        }
    }
} 