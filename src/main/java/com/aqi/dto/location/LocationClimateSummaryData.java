package com.aqi.dto.location;

import com.aqi.util.ClimateSummaryUnits;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
public class LocationClimateSummaryData extends LocationClimateBaseData {

    @Builder.Default
    private ClimateSummaryUnits units = new ClimateSummaryUnits();

    private CurrentData current;
    private ForecastData forecast;

    @Data
    @Builder
    public static class CurrentData {
        private Long time;
        private Double temperature;

        @JsonProperty("weather_code")
        private Integer weatherCode;

        private Integer aqi;
    }

    @Data
    @Builder
    public static class ForecastData {
        private List<Long> time;

        @JsonProperty("temperature_max")
        private List<Double> temperatureMax;

        @JsonProperty("temperature_min")
        private List<Double> temperatureMin;

        @JsonProperty("weather_code")
        private List<Integer> weatherCode;

        private List<Integer> aqi;
    }
}