package com.example.dermAI.controller.User;

import com.example.dermAI.controller.User.contract.UserContract;
import com.example.dermAI.dto.User.request.RegisterRequest;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/auth")
public class AuthController {

    private final UserContract userContract;

    public AuthController(UserContract userContract) {
        this.userContract = userContract;
    }

    @ModelAttribute("registerRequest")
    public RegisterRequest registerRequest() {
        return new RegisterRequest();
    }

    @GetMapping("/login")
    public String login() {
        return "auth/login";
    }

    @GetMapping("/register")
    public String register() {
        return "auth/register";
    }

    @PostMapping("/register")
    public String registerUser(@Valid @ModelAttribute("registerRequest") RegisterRequest registerRequest, BindingResult bindingResult, RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            // validasyon hatalarını Thymeleaf template’ine iletir
            return "auth/register";
        }

        try {
            userContract.registerUser(registerRequest);
            redirectAttributes.addFlashAttribute("success", "Kayıt başarılı! Giriş yapabilirsiniz.");
            return "redirect:/auth/login";
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
            return "redirect:/auth/register";
        }
    }
}
