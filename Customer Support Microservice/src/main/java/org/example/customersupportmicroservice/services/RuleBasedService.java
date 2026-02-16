package org.example.customersupportmicroservice.services;

import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class RuleBasedService {

    private static final Map<String, String> RULES = Map.ofEntries(
            // Greetings
            Map.entry("hello", "Hello! Welcome to Energy Management System support. How can I help you today?"),
            Map.entry("hi", "Hi there! I'm your Energy Management assistant. What can I do for you?"),

            // Help
            Map.entry("help", "I can help you with: viewing energy consumption, managing devices, profile information, and contacting support. Just ask about any of these topics!"),

            // Consumption
            Map.entry("consumption", "To view your energy consumption: Go to My Profile, click View Consumption, and select a date to see hourly usage. You will see charts and detailed statistics of your energy usage. "),

            // Devices
            Map.entry("device", "Your devices are listed in My Profile. Each device shows its name, description, maximum consumption in Watts, and energy efficiency class from A+++ to G.  To add or remove devices, contact an administrator. "),

            // Profile
            Map.entry("profile", "Your profile shows your personal info, account role, and device statistics. Go to My Profile in the menu to view it."),

            // Billing
            Map.entry("bill", "To estimate costs, check your consumption data in My Profile then View Consumption. For billing questions, please contact an administrator."),

            // Contact/Support
            Map.entry("contact", "To contact support, chat with an administrator.  Look for users with the ADMIN badge in the chat list. "),
            Map.entry("admin", "Look for users with the ADMIN badge in the chat. Click on their name to start a conversation. "),

            // Technical
            Map.entry("login", "Having login trouble? Check your username and password and make sure Caps Lock is off. If you forgot your password, contact an administrator."),
            Map.entry("error", "If you are seeing an error, try refreshing the page.  If it persists, contact an administrator for help."),

            // Closing
            Map.entry("thanks", "You are welcome! Is there anything else I can help you with?"),
            Map.entry("thank you", "My pleasure! Let me know if you have more questions. "),
            Map.entry("bye", "Goodbye! Have a great day! "),

            // Menu
            Map.entry("menu", "Main sections: My Profile for your info and devices, Chat to talk to support, and Users or Devices for admin management.  What would you like to know more about?")
    );

    public String ask(String userMessage) {
        if (userMessage == null || userMessage.isBlank()) {
            return null;
        }

        String normalized = userMessage.toLowerCase().trim();

        return RULES.entrySet().stream()
                .filter(entry -> normalized.contains(entry.getKey()))
                .map(Map.Entry::getValue)
                .findFirst()
                .orElse(null);
    }
}