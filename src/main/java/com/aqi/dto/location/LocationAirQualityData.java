package com.aqi.dto.location;

import com.aqi.util.AirQualityUnits;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class LocationAirQualityData {

    @Builder.Default
    private AirQualityUnits units = new AirQualityUnits();
    private CurrentData current;
    private FutureForecast hourly;
    private FutureForecast daily;

    @Data
    @Builder
    public static class CurrentData {
        private Integer aqi;
        private Double pm2_5;
        private Double pm10;
        private Double o3;
        private Double co;
        private Double no2;
        private Double so2;
    }

    @Data
    @Builder
    public static class FutureForecast {
        private List<Long> time;
        private List<Integer> aqi;
        private List<Double> pm2_5;
        private List<Double> pm10;
        private List<Double> o3;
        private List<Double> co;
        private List<Double> no2;
        private List<Double> so2;
    }
}
