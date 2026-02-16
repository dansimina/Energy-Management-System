package com.example.device.management.service.messaging;

import com.example.device.management.service.dtos.UserDTO;
import com.example.device.management.service.services.UserService;
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

    @RabbitListener(queues = "${rabbitmq.queue.device}")
    public void receiveMessage(UserIdMessageType message) {
        switch (message.getType()) {
            case INSERT -> {
                UserDTO user = new UserDTO(message.getId());
                userService.insert(user);
            }
            case DELETE -> userService.delete(message.getId());
        }
    }

    @RabbitListener(queues = "${rabbitmq.queue.alert}")
    public void receiveDeviceAlert(DeviceAlertMessageType message) {
        userService.notifyUser(message.getDeviceId(), message.getValue(), message.getTimestamp());
    }
}
