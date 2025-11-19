package com.aqi.dto.omw;

import lombok.Data;

import java.util.List;

@Data
public class AirPollutionResponse {
    private Coord coord;
    private List<AirPollutionEntry> list;

    @Data
    public static class Coord {
        private Double lon;
        private Double lat;
    }

    @Data
    public static class AirPollutionEntry {
        private Main main;
        private Components components;
        private Long dt;
    }

    @Data
    public static class Main {
        private Integer aqi;
    }

    @Data
    public static class Components {
        private Double co;
        private Double no;
        private Double no2;
        private Double o3;
        private Double so2;
        private Double pm2_5;
        private Double pm10;
        private Double nh3;
    }
}