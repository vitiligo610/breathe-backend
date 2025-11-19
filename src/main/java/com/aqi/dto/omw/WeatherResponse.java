package com.aqi.dto.omw;

import lombok.Data;
import java.util.List;

@Data
public class WeatherResponse {
    private Coord coord;
    private List<Weather> weather;
    private String base;
    private Main main;
    private Integer visibility;
    private Wind wind;
    private Clouds clouds;
    private Long dt;
    private Sys sys;
    private Integer timezone;
    private Long id;
    private String name;
    private Integer cod;

    @Data
    public static class Coord {
        private Double lon;
        private Double lat;
    }

    @Data
    public static class Weather {
        private Integer id;
        private String main;
        private String description;
        private String icon;
    }

    @Data
    public static class Main {
        private Double temp;
        private Double feels_like;
        private Double temp_min;
        private Double temp_max;
        private Integer pressure;
        private Integer humidity;
        private Integer sea_level;
        private Integer grnd_level;
    }

    @Data
    public static class Wind {
        private Double speed;
        private Integer deg;
        private Double gust;
    }

    @Data
    public static class Clouds {
        private Integer all;
    }

    @Data
    public static class Sys {
        private String country;
        private Long sunrise;
        private Long sunset;
    }
}
