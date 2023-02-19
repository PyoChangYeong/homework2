package com.example.homework2;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class Homework2Application {

    public static void main(String[] args) {
        SpringApplication.run(Homework2Application.class, args);
    }

}
