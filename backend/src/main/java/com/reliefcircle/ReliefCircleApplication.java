package com.reliefcircle;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("com.reliefcircle") 
public class ReliefCircleApplication {

	public static void main(String[] args) {
		SpringApplication.run(ReliefCircleApplication.class, args);
	}

}
