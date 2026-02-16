package org.example.loadbalancingmicroservice.config;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.DefaultJackson2JavaTypeMapper;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {
    @Value("${rabbitmq.queue.data.collection}")
    private String DATA_COLLECTION_QUEUE;

    @Value("${rabbitmq.queue.monitoring.service.1}")
    private String MONITORING_SERVICE_QUEUE_1;

    @Value("${rabbitmq.queue.monitoring.service.2}")
    private String MONITORING_SERVICE_QUEUE_2;

    @Value("${rabbitmq.queue.monitoring.service.3}")
    private String MONITORING_SERVICE_QUEUE_3;

    @Value("${rabbitmq.queue.monitoring.service.4}")
    private String MONITORING_SERVICE_QUEUE_4;

    @Bean
    public Queue dataCollectionQueue() {
        return new Queue(DATA_COLLECTION_QUEUE, true);
    }

    @Bean
    public Queue monitoringServiceQueue1() {
        return new Queue(MONITORING_SERVICE_QUEUE_1, true);
    }

    @Bean
    public Queue monitoringServiceQueue2() {
        return new Queue(MONITORING_SERVICE_QUEUE_2, true);
    }

    @Bean
    public Queue monitoringServiceQueue3() {
        return new Queue(MONITORING_SERVICE_QUEUE_3, true);
    }

    @Bean
    public Queue monitoringServiceQueue4() {
        return new Queue(MONITORING_SERVICE_QUEUE_4, true);
    }

    @Bean
    public RabbitAdmin rabbitAdmin(ConnectionFactory connectionFactory) {
        return new RabbitAdmin(connectionFactory);
    }

    @Bean
    @SuppressWarnings("removal")
    public Jackson2JsonMessageConverter messageConverter() {
        Jackson2JsonMessageConverter converter = new Jackson2JsonMessageConverter();
        DefaultJackson2JavaTypeMapper typeMapper = new DefaultJackson2JavaTypeMapper();
        typeMapper.setTrustedPackages("*");
        converter.setJavaTypeMapper(typeMapper);
        return converter;
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(messageConverter());
        return template;
    }
}