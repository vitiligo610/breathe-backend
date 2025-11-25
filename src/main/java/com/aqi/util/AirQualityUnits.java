package com.aqi.util;

import lombok.Data;

@Data
public class AirQualityUnits {

    private String aqi = "US AQI";
    private String pm2_5 = "μg/m³";
    private String pm10 = "μg/m³";
    private String o3 = "μg/m³";
    private String co = "μg/m³";
    private String no2 = "μg/m³";
    private String so2 = "μg/m³";
}
