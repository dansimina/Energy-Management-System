package org.example.loadbalancingmicroservice.messaging;

import org.example.loadbalancingmicroservice.services.LoadBalancingService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MessageConsumer {
    private final LoadBalancingService loadBalancingService;

    @Autowired
    public MessageConsumer(LoadBalancingService loadBalancingService) {
        this.loadBalancingService = loadBalancingService;
    }

    @RabbitListener(queues = "${rabbitmq.queue.data.collection}")
    public void receiveMessage(MeasurementMessageType message) {
        loadBalancingService.forwardMessage(message);
    }
}
