package com.americobarber;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class AmericoBarberApplication {

    public static void main(String[] args) {
        SpringApplication.run(AmericoBarberApplication.class, args);
    }
}
