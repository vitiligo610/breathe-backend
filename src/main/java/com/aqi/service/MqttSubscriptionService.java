package com.aqi.service;

import com.hivemq.client.mqtt.datatypes.MqttQos;
import com.hivemq.client.mqtt.mqtt3.Mqtt3AsyncClient;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class MqttSubscriptionService {
    private final Mqtt3AsyncClient mqttClient;
    private final SensorReadingService sensorReadingService;

    @PostConstruct
    public void startMqttClient() {
        mqttClient.connect()
            .whenComplete((connAck, throwable) -> {
                if (throwable != null) {
                    log.error("Failed to connect to MQTT Broker: {}", throwable.getMessage());
                } else {
                    log.info("Successfully connected to MQTT Broker.");
                    subscribeToTopics();
                }
            });
    }

    private void subscribeToTopics() {
        final String topicFilter = "sensors/+/data";

        mqttClient.subscribeWith()
            .topicFilter(topicFilter)
            .qos(MqttQos.AT_LEAST_ONCE)
            .callback(publish -> {
                String topic = publish.getTopic().toString();
                String payload = new String(publish.getPayloadAsBytes(), java.nio.charset.StandardCharsets.UTF_8);

                log.info("Message received on [{}]: {}", topic, payload);
                sensorReadingService.processAndSaveReading(payload);
            })
            .send()
            .whenComplete((subAck, throwable) -> {
                if (throwable != null) {
                    log.error("Failed to subscribe to topic " + topicFilter + ": {}", throwable.getMessage());
                } else {
                    log.info("Successfully subscribed to topic: {}", topicFilter);
                }
            });
    }
}
