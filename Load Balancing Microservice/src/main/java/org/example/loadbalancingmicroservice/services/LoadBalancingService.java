package org.example.loadbalancingmicroservice.services;

import org.example.loadbalancingmicroservice.messaging.MeasurementMessageType;
import org.example.loadbalancingmicroservice.messaging.MessageProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LoadBalancingService {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoadBalancingService.class);

    private final MessageProducer messageProducer;
    private final QueueLoadMonitor queueLoadMonitor;

    @Autowired
    public LoadBalancingService(MessageProducer messageProducer, QueueLoadMonitor queueLoadMonitor) {
        this.messageProducer = messageProducer;
        this.queueLoadMonitor = queueLoadMonitor;
    }

    public void forwardMessage(MeasurementMessageType message) {
        String targetQueue = queueLoadMonitor.getLeastLoadedQueue();
        LOGGER.info("Routing device {} to {}", message.getDeviceId(), targetQueue);
        messageProducer.sendMessage(targetQueue, message);
    }
}
