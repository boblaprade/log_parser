package com.cjmware;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Parser{

    // Initializes SPRING JPA and invokes the com.ej.CommandLineProcessor class
    public static void main(String [] args) {
        SpringApplication.run(Parser.class, args);
    }
}
