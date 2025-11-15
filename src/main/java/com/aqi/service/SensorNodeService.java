package com.aqi.service;

import com.aqi.entity.SensorNode;
import com.aqi.exception.ResourceNotFoundException;
import com.aqi.repository.SensorNodeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SensorNodeService {
    private final SensorNodeRepository sensorNodeRepository;

    @Transactional
    public SensorNode addNode(String name, String location) {
        SensorNode newNode = new SensorNode();
        newNode.setName(name);
        newNode.setLocation(location);

        return sensorNodeRepository.save(newNode);
    }

    @Transactional
    public void removeNode(String id) {
        Optional<SensorNode> node = sensorNodeRepository.findById(id);
        if (node.isPresent()) {
            sensorNodeRepository.delete(node.get());
        } else {
            throw new ResourceNotFoundException("Sensor node with id " + id + " not found.");
        }
    }
}
