package org.example.websocketmicroservice.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class ChatbotService {
    @Value("${support.service.host}")
    private String supportServiceUrl;

    private final WebClient webClient;

    @Autowired
    public ChatbotService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl(supportServiceUrl).build();
    }

    public Mono<String> processMessage(String message) {
        return webClient.post()
                .uri(supportServiceUrl + "/internal/ask")
                .contentType(MediaType.TEXT_PLAIN)
                .bodyValue(message)
                .retrieve()
                .bodyToMono(String.class);
    }
}
