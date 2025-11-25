package com.aqi.util;

import lombok.Data;

@Data
public class AirQualityUnits {

    private String time = AllUnits.TIME;
    private String aqi = AllUnits.AQI;
    private String pm2_5 = AllUnits.PM2_5;
    private String pm10 = AllUnits.PM10;
    private String o3 = AllUnits.O3;
    private String co = AllUnits.CO;
    private String no2 = AllUnits.NO2;
    private String so2 = AllUnits.SO2;
}
