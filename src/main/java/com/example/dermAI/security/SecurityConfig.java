package com.example.dermAI.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/css/**", "/js/**", "/image/**").permitAll()
                        .requestMatchers("/auth/login", "/auth/kayit").permitAll()
                        .requestMatchers("/", "/user/about", "/blog/blog", "/user/skintypes").permitAll()
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/auth/login")              // Controller ile servis edilen login sayfanın mapping'i
                        .loginProcessingUrl("/login")           // Formun action'u (POST işlemi)
                        .defaultSuccessUrl("/deneme", true) // Başarılı login sonrası yönlendirme
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutSuccessUrl("/auth/login?logout")
                        .permitAll()
                );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
