package com.example.dermAI.config;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice(annotations = Controller.class)
public class GlobalModelAttributes {

    @ModelAttribute("isAuthenticated")
    public boolean isAuthenticated(Authentication authentication) {
        return authentication != null
                && authentication.isAuthenticated()
                // anonymousUser kontrolü, eğer kullanıyorsanız
                && !"anonymousUser".equals(authentication.getName());
    }

    @ModelAttribute("currentUsername")
    public String currentUsername(Authentication authentication) {
        return (authentication != null
                && authentication.isAuthenticated()
                && !"anonymousUser".equals(authentication.getName()))
                ? authentication.getName()
                : null;
    }
}
