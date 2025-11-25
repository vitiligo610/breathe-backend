package com.aqi.dto.geocoding;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ReverseGeocodingResponse {

    private String city;
    private String locality;
    private String principalSubdivision;
    private String countryName;
    private String continent;

    public String getFormattedName() {
        if (city != null && !city.isEmpty()) return city;
        if (locality != null && !locality.isEmpty()) return locality;
        if (principalSubdivision != null && !principalSubdivision.isEmpty()) return principalSubdivision;
        if (countryName != null) return countryName;
        return "Unknown Location";
    }
}