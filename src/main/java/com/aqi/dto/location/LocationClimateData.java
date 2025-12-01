package com.aqi.dto.location;

import com.aqi.dto.report.PollutionReportDto;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
public class LocationClimateData extends LocationClimateBaseData {

    private LocationWeatherData weather;

    @JsonProperty("air_quality")
    private LocationAirQualityData airQuality;

    @JsonProperty("nearby_reports")
    private List<PollutionReportDto> nearbyReports;
}
