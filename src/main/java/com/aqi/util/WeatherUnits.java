package com.aqi.util;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class WeatherUnits {
    private String temperature = AllUnits.TEMPERATURE;

    @JsonProperty("weather_code")
    private String weatherCode = AllUnits.WEATHER_CODE;

    private String humidity = AllUnits.HUMIDITY;

    @JsonProperty("wind_speed")
    private String windSpeed = AllUnits.WIND_SPEED;

    @JsonProperty("wind_direction")
    private String windDirection = AllUnits.WIND_DIRECTION;
}
