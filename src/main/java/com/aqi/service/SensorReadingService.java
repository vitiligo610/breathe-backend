package com.aqi.service;

import com.aqi.dto.sensor.SensorDataDto;
import com.aqi.entity.SensorNode;
import com.aqi.entity.SensorReading;
import com.aqi.exception.ResourceNotFoundException;
import com.aqi.repository.SensorNodeRepository;
import com.aqi.repository.SensorReadingRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.Instant;

@Service
@RequiredArgsConstructor
public class SensorReadingService {
    private final SensorReadingRepository sensorReadingRepository;
    private final SensorNodeRepository sensorNodeRepository;
    private final ObjectMapper objectMapper;

    @Transactional
    public SensorReading processAndSaveReading(String jsonPayload) {
        SensorDataDto sensorData;

        try {
            sensorData = objectMapper.readValue(jsonPayload, SensorDataDto.class);
            sensorData.setTimestamp(Instant.now());
        } catch (IOException e) {
            throw new IllegalArgumentException("Invalid JSON format received from sensor: " + jsonPayload, e);
        }

        return saveReading(sensorData);
    }

    @Transactional
    public SensorReading saveReading(SensorDataDto sensorData) {
        String nodeId = sensorData.getNode_id();
        SensorNode sensorNode = sensorNodeRepository.findById(nodeId)
                .orElseThrow(() -> new ResourceNotFoundException("SensorNode not found for ID: " + nodeId));

        SensorReading reading = SensorReading.builder()
                .sensorNode(sensorNode)
                .timestamp(sensorData.getTimestamp())
                .temperature(sensorData.getTemperature())
                .humidity(sensorData.getHumidity())
                .mq4Ch4(sensorData.getMq4_ch4())
                .mq7Co(sensorData.getMq7_co())
                .mq135Air(sensorData.getMq135_air())
                .dustUgm3(sensorData.getDust_ugm3())
                .build();

        return sensorReadingRepository.save(reading);
    }

    @Transactional(readOnly = true)
    public Page<SensorDataDto> getSensorReadings(String id, Instant start, Instant end, Pageable pageable) {
        SensorNode sensorNode = sensorNodeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("SensorNode not found with id: " + id));

        Page<SensorReading> readingsPage;
        if (start != null && end != null) {
            readingsPage = sensorReadingRepository.findBySensorNodeAndTimestampBetween(sensorNode, start, end, pageable);
        } else {
            readingsPage = sensorReadingRepository.findBySensorNode(sensorNode, pageable);
        }

        return readingsPage.map(this::convertToDto);
    }

    private SensorDataDto convertToDto(SensorReading entity) {
        return new SensorDataDto(
                entity.getSensorNode().getId(),
                entity.getTimestamp(),
                entity.getTemperature(),
                entity.getHumidity(),
                entity.getMq4Ch4(),
                entity.getMq7Co(),
                entity.getMq135Air(),
                entity.getDustUgm3()
        );
    }
}