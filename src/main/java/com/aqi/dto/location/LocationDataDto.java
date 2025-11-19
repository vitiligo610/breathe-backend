package com.aqi.dto.location;

import com.aqi.dto.aqi.AqiCategory;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@Builder
public class LocationDataDto {
    private String name;
    private String locationId;
    private double latitude;
    private double longitude;
    private Instant timestamp;

    private Integer aqiUs;
    private AqiCategory aqiCategory;

    private Double temperatureC;
    private Double humidityPercent;
    private Double windSpeedMps;
    private String weatherDescription;

    private Map<String, Double> pollutants;

    private Double mq4Ch4;
    private Double mq7Co;
    private Double dustUgm3;
}
