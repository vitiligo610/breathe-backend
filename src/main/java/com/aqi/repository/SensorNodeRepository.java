package com.aqi.repository;

import com.aqi.entity.SensorNode;
import org.springframework.data.domain.Example;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

public interface SensorNodeRepository extends JpaRepository<SensorNode, String>, JpaSpecificationExecutor<SensorNode> {
}
