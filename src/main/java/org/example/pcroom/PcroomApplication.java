package org.example.pcroom;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class PcroomApplication {

        public static void main(String[] args) {
            SpringApplication.run(PcroomApplication.class, args);
        }
    }