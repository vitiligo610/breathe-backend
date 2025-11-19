package com.aqi.util;

import com.aqi.dto.aqi.AqiResult;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import com.aqi.dto.aqi.Breakpoint;
import com.aqi.dto.aqi.AqiCategory;

/**
 * Service to calculate US AQI based on OpenWeatherMap pollutant data.
 * Reference: US EPA AQI Breakpoints.
 */
@Service
public class AqiCalculator {
    // Molecular Weights for unit conversion (g/mol)
    private static final double MW_CO = 28.01;
    private static final double MW_NO2 = 46.0055;
    private static final double MW_SO2 = 64.066;
    private static final double MW_O3 = 48.00;

    // Ideal Gas Law constant factor at 25 degrees C and 1 atm pressure (standard EPA conditions)
    private static final double MOLAR_VOLUME = 24.45;

    /**
     * Main entry point to calculate AQI.
     * @param pollutants Map of pollutant names to values in µg/m3 (OWM default).
     * Keys: "co", "no", "no2", "o3", "so2", "pm2_5", "pm10", "nh3"
     * @return AqiResult containing the max AQI, category, and dominant pollutant.
     */
    public AqiResult calculateAqi(Map<String, Double> pollutants) {
        int maxAqi = 0;
        String dominantPollutant = "Unknown";

        // 1. Extract and Convert Units
        // PM2.5 and PM10 are already in µg/m3, which matches the table.
        double pm25 = pollutants.getOrDefault("pm2_5", 0.0);
        double pm10 = pollutants.getOrDefault("pm10", 0.0);

        // Gases need conversion from µg/m3 to ppm or ppb
        // CO requires ppm
        double coUg = pollutants.getOrDefault("co", 0.0);
        double coPpm = convertUgToPpm(coUg, MW_CO);

        // NO2, SO2, O3 require ppb
        double no2Ug = pollutants.getOrDefault("no2", 0.0);
        double no2Ppb = convertUgToPpb(no2Ug, MW_NO2); // convert to ppb

        double so2Ug = pollutants.getOrDefault("so2", 0.0);
        double so2Ppb = convertUgToPpb(so2Ug, MW_SO2);

        double o3Ug = pollutants.getOrDefault("o3", 0.0);
        double o3Ppb = convertUgToPpb(o3Ug, MW_O3);

        // 2. Calculate AQI for each relevant pollutant
        // Note: NO (Nitric Oxide) and NH3 are in OWM data but not in US AQI standards. They are ignored.

        int aqiPm25 = calculatePollutantAqi("PM2.5", pm25);
        if (aqiPm25 > maxAqi) { maxAqi = aqiPm25; dominantPollutant = "PM2.5"; }

        int aqiPm10 = calculatePollutantAqi("PM10", pm10);
        if (aqiPm10 > maxAqi) { maxAqi = aqiPm10; dominantPollutant = "PM10"; }

        int aqiCo = calculatePollutantAqi("CO", coPpm);
        if (aqiCo > maxAqi) { maxAqi = aqiCo; dominantPollutant = "CO"; }

        int aqiSo2 = calculatePollutantAqi("SO2", so2Ppb);
        if (aqiSo2 > maxAqi) { maxAqi = aqiSo2; dominantPollutant = "SO2"; }

        int aqiNo2 = calculatePollutantAqi("NO2", no2Ppb);
        if (aqiNo2 > maxAqi) { maxAqi = aqiNo2; dominantPollutant = "NO2"; }

        int aqiO3 = calculatePollutantAqi("O3", o3Ppb);
        if (aqiO3 > maxAqi) { maxAqi = aqiO3; dominantPollutant = "O3"; }

        AqiCategory category = getAqiCategory(maxAqi);

        return new AqiResult(maxAqi, category, dominantPollutant);
    }

    /**
     * Converts µg/m3 to ppm using molecular weight.
     */
    private double convertUgToPpm(double ugM3, double molecularWeight) {
        if (molecularWeight == 0) return 0;
        // Formula: ppm = (µg/m3 * 24.45) / (MolecularWeight * 1000)
        // But for ppb, we don't divide by 1000.
        return (ugM3 * MOLAR_VOLUME) / (molecularWeight * 1000.0);
    }

    /**
     * Converts µg/m3 to ppb.
     * (1 ppm = 1000 ppb)
     */
    private double convertUgToPpb(double ugM3, double molecularWeight) {
        return convertUgToPpm(ugM3, molecularWeight) * 1000.0;
    }

    /**
     * Calculates AQI for a single pollutant based on breakpoints.
     */
    private int calculatePollutantAqi(String pollutant, double concentration) {
        // Rounding conventions based on EPA standards (usually truncation to specific decimals)
        // For simplicity in this implementation, we keep double precision for comparison ranges.

        List<Breakpoint> breakpoints = getBreakpoints(pollutant);

        for (Breakpoint bp : breakpoints) {
            if (concentration >= bp.getLowConc() && concentration <= bp.getHighConc()) {
                return calculateLinearEquation(concentration, bp.getLowConc(), bp.getHighConc(), bp.getLowIndex(), bp.getHighIndex());
            }
        }

        // If concentration exceeds the highest defined breakpoint, use the last known slope or cap at max.
        if (!breakpoints.isEmpty() && concentration > breakpoints.getLast().getHighConc()) {
            // Use the logic of the highest bracket
            Breakpoint last = breakpoints.getLast();
            return calculateLinearEquation(concentration, last.getLowConc(), last.getHighConc(), last.getLowIndex(), last.getHighIndex());
        }

        return 0;
    }

    /**
     * Implementation of the linear interpolation equation provided in documentation.
     * I = ((I_high - I_low) / (C_high - C_low)) * (C - C_low) + I_low
     */
    private int calculateLinearEquation(double c, double cLow, double cHigh, int iLow, int iHigh) {
        double result = ((double)(iHigh - iLow) / (cHigh - cLow)) * (c - cLow) + iLow;
        return (int) Math.round(result);
    }

    /**
     * Determines the category based on AQI score (Image 1).
     */
    public AqiCategory getAqiCategory(int aqiScore) {
        if (aqiScore <= 50) return new AqiCategory("Good", "Green");
        if (aqiScore <= 100) return new AqiCategory("Moderate", "Yellow");
        if (aqiScore <= 150) return new AqiCategory("Unhealthy for sensitive groups", "Orange");
        if (aqiScore <= 200) return new AqiCategory("Unhealthy", "Red");
        if (aqiScore <= 300) return new AqiCategory("Very unhealthy", "Purple");
        if (aqiScore <= 500) return new AqiCategory("Hazardous", "Maroon");
        return new AqiCategory("Very Hazardous", "Brown");
    }

    /**
     * Returns the breakpoint list for a specific pollutant based on the provided image table.
     */
    private List<Breakpoint> getBreakpoints(String pollutant) {
        List<Breakpoint> list = new ArrayList<>();

        switch (pollutant) {
            case "PM2.5": // Units: µg/m3
                list.add(new Breakpoint(0.0, 12.0, 0, 50));
                list.add(new Breakpoint(12.1, 35.4, 51, 100));
                list.add(new Breakpoint(35.5, 55.4, 101, 150));
                list.add(new Breakpoint(55.5, 150.4, 151, 200));
                list.add(new Breakpoint(150.5, 250.4, 201, 300));
                list.add(new Breakpoint(250.5, 350.4, 301, 400));
                list.add(new Breakpoint(350.5, 500.4, 401, 500));
                break;

            case "PM10": // Units: µg/m3
                list.add(new Breakpoint(0, 54, 0, 50));
                list.add(new Breakpoint(55, 154, 51, 100));
                list.add(new Breakpoint(155, 254, 101, 150));
                list.add(new Breakpoint(255, 354, 151, 200));
                list.add(new Breakpoint(355, 424, 201, 300));
                list.add(new Breakpoint(425, 504, 301, 400));
                list.add(new Breakpoint(505, 604, 401, 500));
                break;

            case "CO": // Units: ppm
                list.add(new Breakpoint(0.0, 4.4, 0, 50));
                list.add(new Breakpoint(4.5, 9.4, 51, 100));
                list.add(new Breakpoint(9.5, 12.4, 101, 150));
                list.add(new Breakpoint(12.5, 15.4, 151, 200));
                list.add(new Breakpoint(15.5, 30.4, 201, 300));
                list.add(new Breakpoint(30.5, 40.4, 301, 400));
                list.add(new Breakpoint(40.5, 50.4, 401, 500));
                break;

            case "SO2": // Units: ppb (1-hr for lower, 24-hr for higher as per table)
                list.add(new Breakpoint(0, 35, 0, 50));
                list.add(new Breakpoint(36, 75, 51, 100));
                list.add(new Breakpoint(76, 185, 101, 150));
                list.add(new Breakpoint(186, 304, 151, 200));
                list.add(new Breakpoint(305, 604, 201, 300));
                list.add(new Breakpoint(605, 804, 301, 400));
                list.add(new Breakpoint(805, 1004, 401, 500));
                break;

            case "NO2": // Units: ppb
                list.add(new Breakpoint(0, 53, 0, 50));
                list.add(new Breakpoint(54, 100, 51, 100));
                list.add(new Breakpoint(101, 360, 101, 150));
                list.add(new Breakpoint(361, 649, 151, 200));
                list.add(new Breakpoint(650, 1249, 201, 300));
                list.add(new Breakpoint(1250, 1649, 301, 400));
                list.add(new Breakpoint(1650, 2049, 401, 500));
                break;

            case "O3": // Units: ppb
                // Special Logic: The table separates 8-hr and 1-hr.
                // Since OWM data is typically instantaneous/hourly, we prioritize the available columns.
                // The table shows 8-hr used for 0-100 AQI, and 1-hr available for 101+.
                // We combine them into a single lookup for calculating "Current" AQI.

                // 8-hour column mappings (0-85 ppb)
                list.add(new Breakpoint(0, 54, 0, 50));
                list.add(new Breakpoint(55, 70, 51, 100));
                list.add(new Breakpoint(71, 85, 101, 150));

                // 1-hour column mappings (125+ ppb)
                // Note: There is a gap between 85 and 125 in the table logic where AQI is technically
                // derived from 8-hr averages, but for this calculator, we assume if > 125 we hit the 1-hr logic.
                list.add(new Breakpoint(125, 164, 101, 150));
                list.add(new Breakpoint(165, 204, 151, 200));
                list.add(new Breakpoint(205, 404, 201, 300));
                list.add(new Breakpoint(405, 504, 301, 400));
                list.add(new Breakpoint(505, 604, 401, 500));
                break;
        }
        return list;
    }
}