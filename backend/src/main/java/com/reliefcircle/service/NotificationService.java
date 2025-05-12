package com.reliefcircle.service;
import lombok.extern.slf4j.Slf4j;  
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

    public void sendNotification(String recipient, String subject, String message) {
        // Implement your notification logic here (e.g., send an email or push notification)
        log.info("Sending notification to {}: {}", recipient, message);
        // Example: EmailService.sendEmail(recipient, subject, message);
    }
}
