package com.example.monitoring.service.messaging;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class MessageProducer {
    private final RabbitTemplate rabbitTemplate;

    @Value("${RABBITMQ_ALERT_QUEUE}")
    private String ALERT_QUEUE;

    @Autowired
    public MessageProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void sendDeviceAlert(DeviceAlertMessageType message) {
        rabbitTemplate.convertAndSend(ALERT_QUEUE, message);
    }
}
