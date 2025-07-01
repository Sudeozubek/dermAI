package com.example.dermAI.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {

    @GetMapping("/blog")
    public String blog() {
        return "blog/blog";
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
} 