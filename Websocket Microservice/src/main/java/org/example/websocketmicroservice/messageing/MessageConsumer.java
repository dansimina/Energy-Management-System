package org.example.websocketmicroservice.messageing;

import org.example.websocketmicroservice.services.NotificationManager;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MessageConsumer {
    private final NotificationManager notificationManager;

    @Autowired
    public MessageConsumer(NotificationManager notificationManager) {
        this.notificationManager = notificationManager;
    }

    @RabbitListener(queues = "${rabbitmq.queue.user.notification}")
    public void receiveMessage(UserNotificationMessageType message) {
        notificationManager.insertAlertMessage(message.getUserId(), message.getDeviceId(), message.getValue(), message.getTimestamp());
        System.out.println("Received message: " + message);
    }
}
