package com.aqi.dto.aqi;

import lombok.Data;
import lombok.Builder;

import java.util.Map;

@Data
@Builder
public class AqiData {
    private Integer aqiUs;
    private AqiCategory category;
    private Map<String, Double> pollutants;
    private String dominantPollutant;
}
