package com.example.dermAI.controller.User;

import com.example.dermAI.dao.UserRepository;
import com.example.dermAI.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/login")
    public String login() {
        return "auth/login";
    }

    @GetMapping("/register")
    public String register() {
        return "auth/register";
    }

    @PostMapping("/register")
    public String registerUser(@RequestParam String fullName,
                              @RequestParam String username,
                              @RequestParam String password,
                              @RequestParam String confirmPassword,
                              RedirectAttributes redirectAttributes) {
        
        // Şifre kontrolü
        if (!password.equals(confirmPassword)) {
            redirectAttributes.addFlashAttribute("error", "Şifreler eşleşmiyor!");
            return "redirect:/auth/register";
        }

        // Kullanıcı adı kontrolü
        if (userRepository.findByUsername(username).isPresent()) {
            redirectAttributes.addFlashAttribute("error", "Bu e-posta adresi zaten kullanılıyor!");
            return "redirect:/auth/register";
        }

        // Yeni kullanıcı oluştur
        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole("USER");
        
        userRepository.save(user);
        
        redirectAttributes.addFlashAttribute("success", "Kayıt başarılı! Giriş yapabilirsiniz.");
        return "redirect:/auth/login";
    }
}
