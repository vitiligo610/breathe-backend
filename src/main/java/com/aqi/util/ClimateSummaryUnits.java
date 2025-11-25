package com.aqi.util;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ClimateSummaryUnits {
    private String time = AllUnits.TIME;
    private String temperature = AllUnits.TEMPERATURE;

    @JsonProperty("weather_code")
    private String weatherCode = AllUnits.WEATHER_CODE;

    @JsonProperty("temperature_max")
    private String temperatureMax = AllUnits.TEMPERATURE;

    @JsonProperty("temperature_min")
    private String temperatureMin = AllUnits.TEMPERATURE;

    private String aqi = AllUnits.AQI;
}
