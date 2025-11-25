package com.aqi.dto.location;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
public class LocationClimateBaseData {
    protected String name;
    protected String country;
    protected Double latitude;
    protected Double longitude;
    protected String timezone;
    protected Long timestamp;

    @JsonProperty("utc_offset_seconds")
    protected Integer utcOffsetSeconds;
}
