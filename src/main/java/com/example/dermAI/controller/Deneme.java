package com.example.dermAI.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class Deneme {

    // Bu sınıf, örnek bir deneme sınıfıdır.
    // İleride kullanılacak veya test edilecek metotlar eklenebilir.

    // Örnek bir metot
    @GetMapping("/deneme")
    public String exampleMethod() {
        return "index";
    }
}
