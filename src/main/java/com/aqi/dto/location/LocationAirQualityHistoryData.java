package com.aqi.dto.location;

import com.aqi.util.AirQualityUnits;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
public class LocationAirQualityHistoryData extends LocationClimateBaseData {

    @Builder.Default
    private AirQualityUnits units = new AirQualityUnits();

    private LocationAirQualityData.Forecast hourly;
    private LocationAirQualityData.Forecast daily;
}
