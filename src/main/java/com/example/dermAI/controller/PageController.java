package com.example.dermAI.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.ui.Model;
import com.example.dermAI.entity.Routine;
import com.example.dermAI.entity.User;
import com.example.dermAI.dao.RoutineRepository;
import com.example.dermAI.dao.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.beans.factory.annotation.Autowired;
import java.time.LocalDateTime;
import java.util.Optional;

@Controller
public class PageController {

    @Autowired
    private RoutineRepository routineRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/")
    public String home() {
        return "index";
    }

    @GetMapping("/skintypes")
    public String skintypes() {
        return "skintypes";
    }

    @GetMapping("/about")
    public String about() {
        return "about";
    }

    @GetMapping("/user/profil")
    public String profil() {
        return "user/profil";
    }

    @GetMapping("/todo")
    public String todo(Model model) {
        try {
            // Loginli kullanıcıyı bul
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String username = auth.getName();
            User user = userRepository.findByUsername(username).orElse(null);
            
            if (user != null) {
                // Son 7 gün içinde oluşturulmuş rutin var mı?
                LocalDateTime oneWeekAgo = LocalDateTime.now().minusDays(7);
                Optional<Routine> recentRoutineOpt = routineRepository.findFirstByUserAndCreatedAtAfterOrderByCreatedAtDesc(user, oneWeekAgo);
                
                if (recentRoutineOpt.isPresent()) {
                    model.addAttribute("routine", recentRoutineOpt.get().getRoutineText());
                }
                
                // Kullanıcı bilgilerini ekle
                model.addAttribute("user", user);
                model.addAttribute("puan", user.getPuan());
            }
            
        } catch (Exception e) {
            // Hata durumunda sessizce devam et
        }
        
        return "todo";
    }
} 