package com.aqi.dto.aqi;

import lombok.Data;
import lombok.Builder;

@Data
@Builder
public class WeatherData {
    private Double temperatureC;
    private Double humidityPercent;

    private WindData wind;
    private String weatherDescription;
    private String weatherIcon;
}

