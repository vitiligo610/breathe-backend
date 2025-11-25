package com.aqi.dto.location;

import com.aqi.util.WeatherUnits;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Data
@Builder
public class LocationWeatherData {

    @Builder.Default
    private WeatherUnits units = new WeatherUnits();
    private CurrentData current;
    private HourlyForecast hourly;
    private DailyForecast daily;

    @Data
    @Builder
    public static class CurrentData {
        private Double temperature;
        private Double humidity;

        @JsonProperty("weather_code")
        private Integer weatherCode;

        @JsonProperty("wind_speed")
        private Double windSpeed;

        @JsonProperty("wind_direction")
        private Integer windDirection;
    }

    @Data
    @SuperBuilder
    public static class FutureForecast {
        protected List<Long> time;
        protected List<Double> humidity;

        @JsonProperty("weather_code")
        protected List<Integer> weatherCode;

        @JsonProperty("wind_speed")
        protected List<Double> windSpeed;

        @JsonProperty("wind_direction")
        protected List<Integer> windDirection;
    }

    @EqualsAndHashCode(callSuper = true)
    @Data
    @SuperBuilder
    public static class HourlyForecast extends FutureForecast {
        private List<Double> temperature;
    }

    @EqualsAndHashCode(callSuper = true)
    @Data
    @SuperBuilder
    public static class DailyForecast extends FutureForecast {
        @JsonProperty("temperature_max")
        private List<Double> temperatureMax;

        @JsonProperty("temperature_min")
        private List<Double> temperatureMin;
    }
}
