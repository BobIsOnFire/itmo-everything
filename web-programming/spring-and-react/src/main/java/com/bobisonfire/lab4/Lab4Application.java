package com.bobisonfire.lab4;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Lab4Application {

    public static void main(String[] args) throws InterruptedException {
        SpringApplication.run(Lab4Application.class, args);
        Thread.sleep(Long.MAX_VALUE);
    }
}