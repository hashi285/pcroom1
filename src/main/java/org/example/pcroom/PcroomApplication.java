package org.example.pcroom;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.net.InetAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

//예시
@SpringBootApplication
public class PcroomApplication {

        public static void main(String[] args) {
            SpringApplication.run(PcroomApplication.class, args);
        }
    }