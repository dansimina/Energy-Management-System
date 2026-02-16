package org.example.loadbalancingmicroservice.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Properties;

@Service
public class QueueLoadMonitor {

    private static final Logger logger = LoggerFactory.getLogger(QueueLoadMonitor.class);

    private final RabbitAdmin rabbitAdmin;
    private final String[] queues;

    @Autowired
    public QueueLoadMonitor(
            RabbitAdmin rabbitAdmin,
            @Value("${rabbitmq.queue.monitoring.service.1}") String queue1,
            @Value("${rabbitmq.queue.monitoring.service.2}") String queue2,
            @Value("${rabbitmq.queue.monitoring.service.3}") String queue3,
            @Value("${rabbitmq.queue.monitoring.service.4}") String queue4) {
        this.rabbitAdmin = rabbitAdmin;
        this.queues = new String[]{queue1, queue2, queue3, queue4};
    }

    public long getQueueMessageCount(String queueName) {
        try {
            Properties props = rabbitAdmin.getQueueProperties(queueName);
            if (props != null) {
                Object count = props.get("QUEUE_MESSAGE_COUNT");
                return Long.parseLong(count.toString());
            }
        } catch (Exception e) {
            logger.error("Error getting queue count for {}: {}", queueName, e.getMessage());
        }
        return 0L;
    }

    public String getLeastLoadedQueue() {
        String leastLoadedQueue = queues[0];
        long minLoad = Long.MAX_VALUE;

        for (String queue : queues) {
            long load = getQueueMessageCount(queue);
            logger.debug("Queue {} has {} messages", queue, load);

            if (load < minLoad) {
                minLoad = load;
                leastLoadedQueue = queue;
            }
        }

        logger.info("Selected queue: {} (load: {})", leastLoadedQueue, minLoad);
        return leastLoadedQueue;
    }
}
