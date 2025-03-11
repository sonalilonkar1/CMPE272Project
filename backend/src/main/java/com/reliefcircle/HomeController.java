package com.reliefcircle;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {

    @GetMapping("/")
    public String home() {
        return "Hello from ReliefCircle!";
    }

    @GetMapping("/app")
    public String appHome() {
        return "Welcome to the /app endpoint!";
    }
    
    @GetMapping("/test")
    public String testHome() {
        return "Welcome to the /test endpoint!";
    }
}
