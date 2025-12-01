package com.aqi.dto.report;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ReportType {
    @JsonProperty("BURNING")
    BURNING("Garbage Burning"),

    @JsonProperty("INDUSTRIAL")
    INDUSTRIAL("Industrial Smoke"),

    @JsonProperty("VEHICLE")
    VEHICLE("Excessive Vehicle Smoke"),

    @JsonProperty("CONSTRUCTION")
    CONSTRUCTION("Construction Dust"),

    @JsonProperty("OTHER")
    OTHER("Other Hazard");

    private final String displayName;
}
