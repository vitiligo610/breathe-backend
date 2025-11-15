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
    private int humidity;
    private int mq4_ch4;
    private int mq7_co;
    private int mq135_air;
    private double dust_ugm3;
}
