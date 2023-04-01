package com.demo.skyros;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class CredentialServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(CredentialServiceApplication.class, args);
    }

}
