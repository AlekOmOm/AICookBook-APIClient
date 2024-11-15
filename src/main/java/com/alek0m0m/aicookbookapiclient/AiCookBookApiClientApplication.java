package com.alek0m0m.aicookbookapiclient;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class AiCookBookApiClientApplication {

    public static void main(String[] args) {
        SpringApplication.run(AiCookBookApiClientApplication.class, args);
    }

    @Bean
    public Dotenv dotenv() {
        return Dotenv.load();
    }
}
