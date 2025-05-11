package com.reliefcircle;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

import io.github.cdimascio.dotenv.Dotenv;

@SpringBootApplication
@ComponentScan("com.reliefcircle") 
public class ReliefCircleApplication {

	public static void main(String[] args) {

		// Load .env file
        Dotenv dotenv = Dotenv.configure().load();

		 // Set system properties for Spring Boot to resolve
    	dotenv.entries().forEach(entry -> System.setProperty(entry.getKey(), entry.getValue()));

		// Start the Spring application
		SpringApplication.run(ReliefCircleApplication.class, args);
	}

}
