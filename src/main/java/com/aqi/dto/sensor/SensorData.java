package com.aqi.dto.sensor;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SensorData {
    private String node_id;
    private Long timestamp;
    private Double temperature;
    private Double humidity;
    private Double mq4_ch4;
    private Double mq7_co;
    private Double mq135_air;
    private Double dust_ugm3;
}
