package com.ayush.EmailResponseGenerator.controller;

import com.ayush.EmailResponseGenerator.DTO.EmailRequest;
import com.ayush.EmailResponseGenerator.service.EmailService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/email")
public class EmailGeneratorController {

    private final EmailService emailService;

    public EmailGeneratorController(EmailService emailService) {
        this.emailService = emailService;
    }

    @PostMapping("/generate")
    public ResponseEntity<String> generateEmailReply(@RequestBody EmailRequest request){
        String response = emailService.generateEmailReply(request);
        return ResponseEntity.ok(response);
    }
}
