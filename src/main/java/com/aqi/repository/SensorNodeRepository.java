package com.aqi.repository;

import com.aqi.entity.SensorNode;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SensorNodeRepository extends JpaRepository<SensorNode, String> {
    Optional<SensorNode> findById(String id);
}
