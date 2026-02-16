package org.example.customersupportmicroservice.services;

import com.google.genai.Client;
import com.google.genai.types.Content;
import com.google.genai.types.GenerateContentConfig;
import com.google.genai.types.GenerateContentResponse;
import com.google.genai.types.Part;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import jakarta.annotation.PostConstruct;

@Service
public class GeminiService {
    @Value("${google.genai.api-key}")
    private String apiKey;

    private Client client;

    private GenerateContentConfig config = null;

    private static final String AGENT_DESCRIPTION =
            "You are a helpful and knowledgeable customer support agent for an Energy Management System (EMS).  " +
                    "Your primary role is to assist users with questions about:\n" +
                    "- Energy consumption monitoring and analytics\n" +
                    "- Smart device management and integration\n" +
                    "- Billing inquiries and payment methods\n" +
                    "- Energy-saving tips and recommendations\n\n" +
                    "Guidelines for your responses:\n" +
                    "1. Be professional, polite, empathetic, and solution-oriented\n" +
                    "2.  Provide clear, concise answers (2-4 sentences max unless more detail is requested)\n" +
                    "3. If you're unsure about specific technical details or account information, " +
                    "recommend the user contact an administrator or check their dashboard\n" +
                    "4.   When discussing energy consumption, use practical examples and actionable advice\n" +
                    "5. Always prioritize user safety when discussing electrical devices\n" +
                    "6. If a question is outside your scope (e.g., specific account data, technical system issues), " +
                    "politely suggest connecting with an administrator through the chat system\n\n" +
                    "IMPORTANT: Always respond in plain text only. Never use markdown formatting such as " +
                    "**bold**, *italics*, # headers, bullet points with *, or any other markdown syntax. " +
                    "Write naturally as if you were typing a simple text message.\n\n" +
                    "Remember: You're here to help users understand and optimize their energy usage while providing " +
                    "excellent customer service. ";

    @PostConstruct
    public void init() {
        this.client = Client.builder().apiKey(apiKey).build();

        Content systemInstruction = Content.builder()
                .parts(java.util.List.of(Part.builder().text(AGENT_DESCRIPTION).build()))
                .build();

        config = GenerateContentConfig.builder()
                .systemInstruction(systemInstruction)
                .build();
    }

    public String ask(String prompt) {
        GenerateContentResponse response = client.models.generateContent(
                "gemini-2.5-flash",
                prompt,
                config
        );
        return response.text();
    }
}