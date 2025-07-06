package com.example.dermAI.controller.User.contract;

import com.example.dermAI.dao.UserRepository;
import com.example.dermAI.entity.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserContractImpl implements UserContract {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    public UserContractImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void registerUser(String fullName, String username, String password, String confirmPassword) {
        if (!password.equals(confirmPassword)) {
            throw new IllegalArgumentException("Şifreler eşleşmiyor!");
        }
        if (userRepository.findByUsername(username).isPresent()) {
            throw new IllegalArgumentException("Bu e-posta adresi zaten kullanılıyor!");
        }
        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole("USER");
        user.setFullName(fullName);
        userRepository.save(user);
    }
}
