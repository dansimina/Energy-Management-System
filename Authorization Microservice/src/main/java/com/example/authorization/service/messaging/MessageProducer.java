package com.example.authorization.service.messaging;

import com.example.authorization.service.dtos.UserDTO;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class MessageProducer {
    private final RabbitTemplate rabbitTemplate;

    @Value("${rabbitmq.queue.user}")
    private String USER_QUEUE;

    @Value("${rabbitmq.queue.device}")
    private String DEVICE_QUEUE;

    @Autowired
    public MessageProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void sendToUserQueue(UserMessageType message) {
        rabbitTemplate.convertAndSend(USER_QUEUE, message);
    }

    public void sendToDeviceQueue(UserIdMessageType message) {
        rabbitTemplate.convertAndSend(DEVICE_QUEUE, message);
    }
}
