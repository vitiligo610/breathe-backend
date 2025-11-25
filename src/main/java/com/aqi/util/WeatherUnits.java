package com.aqi.util;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class WeatherUnits {
    private String temperature = "°";

    @JsonProperty("weather_code")
    private String weatherCode = "wmo code";

    private String humidity = "%";

    @JsonProperty("wind_speed")
    private String windSpeed = "km/h";

    @JsonProperty("wind_direction")
    private String windDirection = "°";
}
