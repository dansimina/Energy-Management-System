package org.example.customersupportmicroservice.controllers;

import org.example.customersupportmicroservice.services.GeminiService;
import org.example.customersupportmicroservice.services.RuleBasedService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/support-service")
public class ChatbotController {
    private final RuleBasedService ruleBasedService;
    private final GeminiService geminiService;

    @Autowired
    public ChatbotController(RuleBasedService ruleBasedService, GeminiService geminiService) {
        this.ruleBasedService = ruleBasedService;
        this.geminiService = geminiService;
    }


    @PostMapping("/internal/ask")
    public ResponseEntity<String> askQuestion(@RequestBody String question) {
        String response = ruleBasedService.ask(question);
        if (response == null) {
            response = geminiService.ask(question);
        }
        if (response == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(response);
    }
}
