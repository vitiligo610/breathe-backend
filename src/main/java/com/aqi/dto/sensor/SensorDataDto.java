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
public class SensorDataDto {
    private String node_id;
    private Instant timestamp;
    private double temperature;
    private double humidity;
    private double mq4_ch4;
    private double mq7_co;
    private double mq135_air;
    private double dust_ugm3;
}
