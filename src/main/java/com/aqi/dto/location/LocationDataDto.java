package com.aqi.dto.location;

import com.aqi.dto.aqi.AqiData;
import com.aqi.dto.aqi.WeatherData;
import com.aqi.dto.sensor.SensorData;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@Builder
public class LocationDataDto {
    private String name;
    private String locationId;
    private Double latitude;
    private Double longitude;
    private Long timestamp;

    private AqiData aqi;
    private WeatherData weather;
    private SensorData sensors;
}
