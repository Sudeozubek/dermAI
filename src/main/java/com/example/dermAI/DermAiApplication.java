package com.example.dermAI;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories
public class DermAiApplication {

	public static void main(String[] args) {
		SpringApplication.run(DermAiApplication.class, args);
	}

}
