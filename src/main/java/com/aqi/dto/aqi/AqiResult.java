package com.aqi.dto.aqi;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class AqiResult {
    private int aqi;
    private AqiCategory category;
    private String dominantPollutant;

    @Override
    public String toString() {
        return "AQI: " + aqi + " (" + category.getLevel() + "), Dominant: " + dominantPollutant;
    }
}
