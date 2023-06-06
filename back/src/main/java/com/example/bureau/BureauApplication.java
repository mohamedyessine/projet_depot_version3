package com.example.bureau;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration;

@SpringBootApplication(exclude = {SecurityFilterAutoConfiguration.class})
public class BureauApplication {

    public static void main(String[] args) {
        SpringApplication.run(BureauApplication.class, args);
    }

}
