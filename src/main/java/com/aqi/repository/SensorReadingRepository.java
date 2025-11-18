package com.aqi.repository;

import com.aqi.entity.SensorNode;
import com.aqi.entity.SensorReading;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Optional;

@Repository
public interface SensorReadingRepository extends JpaRepository<SensorReading, Long> {
    Optional<SensorReading> findFirstBySensorNodeOrderByTimestampDesc(SensorNode sensorNode);
    Page<SensorReading> findBySensorNode(SensorNode sensorNode, Pageable pageable);
    Page<SensorReading> findBySensorNodeAndTimestampBetween(
            SensorNode sensorNode,
            Instant start,
            Instant end,
            Pageable pageable
    );
}