package com.aqi.dto.meteo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class AirQualityResponse extends MeteoApiResponse {

    @JsonProperty("current_units")
    private Units currentUnits;

    @JsonProperty("current")
    private CurrentData current;

    @JsonProperty("hourly_units")
    private Units hourlyUnits;

    @JsonProperty("hourly")
    private HourlyData hourly;

    @Data
    public static class Units {
        private String time;

        @JsonProperty("us_aqi")
        private String usAqi;

        private String pm10;
        private String pm2_5;

        @JsonProperty("ozone")
        private String o3;

        @JsonProperty("carbon_monoxide")
        private String co;

        @JsonProperty("nitrogen_dioxide")
        private String no2;

        @JsonProperty("sulphur_dioxide")
        private String so2;
    }

    @Data
    public static class CurrentData {
        private Long time;
        private Integer interval;

        @JsonProperty("us_aqi")
        private Integer usAqi;

        private Double pm10;
        private Double pm2_5;

        @JsonProperty("ozone")
        private Double o3;

        @JsonProperty("carbon_monoxide")
        private Double co;

        @JsonProperty("nitrogen_dioxide")
        private Double no2;

        @JsonProperty("sulphur_dioxide")
        private Double so2;
    }

    @Data
    public static class HourlyData {
        private List<Long> time;

        @JsonProperty("us_aqi")
        private List<Integer> usAqi;

        private List<Double> pm10;
        private List<Double> pm2_5;

        @JsonProperty("ozone")
        private List<Double> o3;

        @JsonProperty("carbon_monoxide")
        private List<Double> co;

        @JsonProperty("nitrogen_dioxide")
        private List<Double> no2;

        @JsonProperty("sulphur_dioxide")
        private List<Double> so2;
    }
}
