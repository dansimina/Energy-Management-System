package com.example.device.management.service.messaging;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class MessageProducer {
    private final RabbitTemplate rabbitTemplate;

    @Value("${rabbitmq.queue.monitoring}")
    private String MONITORING_QUEUE;

    @Value("${rabbitmq.queue.user.notification}")
    private String USER_NOTIFICATION_QUEUE;

    @Autowired
    public MessageProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void sendToMonitoringQueue(DeviceIdMessageType message) {
        rabbitTemplate.convertAndSend(MONITORING_QUEUE, message);
    }

    public void sendToUserNotificationQueue(UserNotificationMessageType message) {
        rabbitTemplate.convertAndSend(USER_NOTIFICATION_QUEUE, message);
    }
}
