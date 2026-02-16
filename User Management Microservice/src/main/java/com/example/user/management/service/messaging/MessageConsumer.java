package com.example.user.management.service.messaging;

import com.example.user.management.service.dtos.UserDTO;
import com.example.user.management.service.services.UserService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MessageConsumer {
    private final UserService userService;

    @Autowired
    public MessageConsumer(UserService userService) {
        this.userService = userService;
    }

    @RabbitListener(queues = "${rabbitmq.queue.user}")
    public void receiveMessage(UserMessageType message) {
        switch (message.getType()) {
            case INSERT -> {
                UserDTO user = message.getUser();
                user.setId(message.getId());
                userService.insert(user);
            }
            case DELETE -> userService.delete(message.getId());
        }
    }
}
