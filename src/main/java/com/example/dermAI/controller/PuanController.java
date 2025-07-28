package com.example.dermAI.controller;

import com.example.dermAI.dao.UserRepository;
import com.example.dermAI.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Optional;

@RestController
@RequestMapping("/api/puan")
public class PuanController {

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/artir")
    public ResponseEntity<Integer> artirPuan() {
        // Loginli kullanıcıyı bul
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        User user = userRepository.findByUsername(username).orElse(null);
        if (user == null) {
            return ResponseEntity.status(401).build();
        }
        user.setPuan(user.getPuan() + 1);
        userRepository.save(user);
        return ResponseEntity.ok(user.getPuan());
    }

    @PostMapping("/azalt")
    public ResponseEntity<Integer> azaltPuan() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        User user = userRepository.findByUsername(username).orElse(null);
        if (user == null) {
            return ResponseEntity.status(401).build();
        }
        // Puanı sıfırın altına düşürme
        int yeniPuan = Math.max(0, user.getPuan() - 1);
        user.setPuan(yeniPuan);
        userRepository.save(user);
        return ResponseEntity.ok(user.getPuan());
    }

    @GetMapping("/todo")
    public String todo(Model model) {
        // Loginli kullanıcıyı bul
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        User user = userRepository.findByUsername(username).orElse(null);
        int puan = (user != null) ? user.getPuan() : 0;
        model.addAttribute("puan", puan);
        return "todo";
    }

    @GetMapping("/mevcut")
    public ResponseEntity<Integer> mevcutPuan() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        User user = userRepository.findByUsername(username).orElse(null);
        if (user == null) {
            return ResponseEntity.status(401).build();
        }
        return ResponseEntity.ok(user.getPuan());
    }
}
