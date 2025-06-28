package com.example.dermAI.general;

import com.example.dermAI.dao.UserRepository;
import com.example.dermAI.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        // Eğer kullanıcı yoksa, örnek bir kullanıcı ekle
        if (userRepository.findByUsername("testuser@gmail.com").isEmpty()) {
            User user = new User();
            user.setUsername("testuser@gmail.com");
            user.setPassword(passwordEncoder.encode("testpass"));
            user.setRole("USER");
            userRepository.save(user);
        }
    }
}
