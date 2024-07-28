package com.example.titanic1;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
public class Titanic1Application {

    public static void main(String[] args) {
        SpringApplication.run(Titanic1Application.class, args);
    }

}
