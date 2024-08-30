package com.email.email.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.email.email.service.EmailService;

@RestController
@RequestMapping("/email")
public class EmailController {

    @Autowired
    private EmailService emailService;

    @PostMapping("/send")
    public String sendEmail(@RequestBody EmailRequest emailRequest) {
        System.out.println("Received email request: " + emailRequest);
        
        // Vérifiez si l'email existe
        if (!emailService.emailExists(emailRequest.getTo())) {
            return "Email does not exist in the database!";
        }

        // Envoyer l'email
        emailService.sendEmail(emailRequest.getTo(), emailRequest.getSubject(), emailRequest.getBody());
        return "Email sent successfully!";
    }

    public static class EmailRequest {
        private String to;
        private String subject;
        private String body;

        public String getTo() { return to; }
        public void setTo(String to) { this.to = to; }

        public String getSubject() { return subject; }
        public void setSubject(String subject) { this.subject = subject; }

        public String getBody() { return body; }
        public void setBody(String body) { this.body = body; }
    }
}
