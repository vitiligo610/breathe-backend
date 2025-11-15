package com.aqi.repository;

import com.aqi.entity.SensorNode;
import com.aqi.entity.SensorReading;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SensorReadingRepository extends JpaRepository<SensorReading, Long> {
    List<SensorReading> findBySensorNodeOrderByTimestampDesc(SensorNode sensorNode);
}
