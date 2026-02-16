package com.example.monitoring.service.messaging;

import com.example.monitoring.service.dtos.DeviceDTO;
import com.example.monitoring.service.services.DeviceDataService;
import com.example.monitoring.service.services.DeviceService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MessageConsumer {
    private final DeviceService deviceService;
    private final DeviceDataService deviceDataService;

    @Autowired
    public MessageConsumer(DeviceService deviceService, DeviceDataService deviceDataService) {
        this.deviceService = deviceService;
        this.deviceDataService = deviceDataService;
    }

    @RabbitListener(queues = "${rabbitmq.queue.monitoring}")
    public void receiveMessageFromDeviceService(DeviceIdMessageType message) {
        switch (message.getType()) {
            case INSERT -> {
                DeviceDTO device = new DeviceDTO(message.getId(), message.getMaximumConsumptionValue());
                deviceService.insert(device);
            }
            case DELETE -> {
                deviceService.delete(message.getId());
            }
        }
    }

    @RabbitListener(queues = "${rabbitmq.queue.data.collection}")
    public void receiveMessageFromDataCollectionService(MeasurementMessageType message) {
        deviceDataService.insert(message.getDeviceId(), message.getTimestamp(), message.getValue());
    }
}
