package com.aqi.dto.location;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
public class LocationClimateData extends LocationClimateBase {

    private LocationWeatherData weather;

    @JsonProperty("air_quality")
    private LocationAirQualityData airQuality;
}
