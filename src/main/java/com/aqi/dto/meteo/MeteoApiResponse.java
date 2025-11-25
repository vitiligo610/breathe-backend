package com.aqi.dto.meteo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class MeteoApiResponse {
    private Double latitude;
    private Double longitude;
    private String timezone;

    @JsonProperty("generationtime_ms")
    private Double generationTimeMs;

    @JsonProperty("utc_offset_seconds")
    private Integer utcOffsetSeconds;

    @JsonProperty("timezone_abbreviation")
    private String timezoneAbbreviation;

    private Double elevation;
}
