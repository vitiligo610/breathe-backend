package com.aqi.dto.meteo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class WeatherForecastResponse extends MeteoApiResponse {

    @JsonProperty("current_units")
    private Units currentUnits;

    @JsonProperty("current")
    private CurrentData current;

    @JsonProperty("hourly_units")
    private Units hourlyUnits;

    @JsonProperty("hourly")
    private HourlyData hourly;

    @JsonProperty("daily_units")
    private DailyUnits dailyUnits;

    @JsonProperty("daily")
    private DailyData daily;

    @Data
    public static class CurrentData {
        private Long time;
        private Integer interval;

        @JsonProperty("temperature_2m")
        private Double temperature2m;

        @JsonProperty("relative_humidity_2m")
        private Double relativeHumidity2m;

        @JsonProperty("weather_code")
        private Integer weatherCode;

        @JsonProperty("wind_speed_10m")
        private Double windSpeed10m;

        @JsonProperty("wind_direction_10m")
        private Integer windDirection10m;
    }

    @Data
    public static class HourlyData {
        private List<Long> time;

        @JsonProperty("temperature_2m")
        private List<Double> temperature2m;

        @JsonProperty("relative_humidity_2m")
        private List<Double> relativeHumidity2m;

        @JsonProperty("weather_code")
        private List<Integer> weatherCode;

        @JsonProperty("wind_speed_10m")
        private List<Double> windSpeed10m;

        @JsonProperty("wind_direction_10m")
        private List<Integer> windDirection10m;
    }

    @Data
    public static class DailyData {
        private List<Long> time;

        @JsonProperty("temperature_2m_max")
        private List<Double> temperature2mMax;

        @JsonProperty("temperature_2m_min")
        private List<Double> temperature2mMin;

        @JsonProperty("relative_humidity_2m_max")
        private List<Double> relativeHumidity2mMax;

        @JsonProperty("weather_code")
        private List<Integer> weatherCode;

        @JsonProperty("wind_speed_10m_max")
        private List<Double> windSpeed10mMax;

        @JsonProperty("wind_direction_10m_dominant")
        private List<Integer> windDirection10mDominant;
    }

    @Data
    public static class Units {
        private String time;
        @JsonProperty("temperature_2m") private String temperature2m;
        @JsonProperty("relative_humidity_2m") private String relativeHumidity2m;
        @JsonProperty("weather_code") private String weatherCode;
        @JsonProperty("wind_speed_10m") private String windSpeed10m;
        @JsonProperty("wind_direction_10m") private String windDirection10m;
    }

    @Data
    public static class DailyUnits {
        private String time;
        @JsonProperty("temperature_2m_max") private String temperature2mMax;
        @JsonProperty("temperature_2m_min") private String temperature2mMin;
        @JsonProperty("weather_code") private String weatherCode;
        @JsonProperty("wind_speed_10m_max") private String windSpeed10mMax;
        @JsonProperty("wind_direction_10m_dominant") private String windDirection10mDominant;
        @JsonProperty("relative_humidity_2m_max") private String relativeHumidity2mMax;
    }
}
