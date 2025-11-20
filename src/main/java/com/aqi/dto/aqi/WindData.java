package com.aqi.dto.aqi;

import lombok.Data;
import lombok.Builder;

@Data
@Builder
public class WindData {
    private Double speedMps;
    private Double angle;
}
