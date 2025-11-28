package com.aqi.mapper;

import com.aqi.dto.geocoding.ReverseGeocodingResponse;
import com.aqi.dto.location.*;
import com.aqi.dto.meteo.AirQualityResponse;
import com.aqi.dto.meteo.WeatherForecastResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.ZoneId;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@Slf4j
public class OpenMeteoMapper {

    @Value("${app.open-meteo.summary-forecast-days:3}")
    private Integer summaryForecastDays;

    @Value("${app.open-meteo.forecast-days:5}")
    private Integer forecastDays;

    public LocationClimateData mapToClimateData(WeatherForecastResponse weather, AirQualityResponse aqi, ReverseGeocodingResponse geo) {

        return LocationClimateData.builder()
                .latitude(weather.getLatitude())
                .longitude(weather.getLongitude())
                .timezone(weather.getTimezone())
                .timestamp(Instant.now().getEpochSecond())
                .utcOffsetSeconds(weather.getUtcOffsetSeconds())
                .name(geo != null ? geo.getFormattedName() : "Unknown")
                .country(geo != null ? geo.getCountryName() : null)
                .weather(mapToWeatherData(weather))
                .airQuality(mapToAirQualityData(aqi, weather))
                .build();
    }

    public LocationWeatherData mapToWeatherData(WeatherForecastResponse response) {
        if (response == null) return null;

        var current = LocationWeatherData.CurrentData.builder()
                .temperature(response.getCurrent().getTemperature2m())
                .humidity(response.getCurrent().getRelativeHumidity2m())
                .weatherCode(response.getCurrent().getWeatherCode())
                .windSpeed(response.getCurrent().getWindSpeed10m())
                .windDirection(response.getCurrent().getWindDirection10m())
                .build();

        int hourlyLimit = Math.min(response.getHourly().getTime().size(), 24);
        int currentHour = getCurrentHourFromTimestamp(response.getCurrent().getTime(), response.getUtcOffsetSeconds());
        int startIndex = Math.max(0, currentHour);
        int endIndex = startIndex + hourlyLimit;

        var hourly = LocationWeatherData.HourlyForecast.builder()
                .time(response.getHourly().getTime().subList(startIndex, endIndex))
                .temperature(response.getHourly().getTemperature2m().subList(startIndex, endIndex))
                .humidity(response.getHourly().getRelativeHumidity2m().subList(startIndex, endIndex))
                .weatherCode(response.getHourly().getWeatherCode().subList(startIndex, endIndex))
                .windSpeed(response.getHourly().getWindSpeed10m().subList(startIndex, endIndex))
                .windDirection(response.getHourly().getWindDirection10m().subList(startIndex, endIndex))
                .build();

        int dailyLimit = Math.min(response.getDaily().getTime().size(), forecastDays);

        var daily = LocationWeatherData.DailyForecast.builder()
                .time(response.getDaily().getTime().subList(0, dailyLimit))
                .temperatureMax(response.getDaily().getTemperature2mMax().subList(0, dailyLimit))
                .temperatureMin(response.getDaily().getTemperature2mMin().subList(0, dailyLimit))
                .humidity(response.getDaily().getRelativeHumidity2mMax().subList(0, dailyLimit))
                .weatherCode(response.getDaily().getWeatherCode().subList(0, dailyLimit))
                .windSpeed(response.getDaily().getWindSpeed10mMax().subList(0, dailyLimit))
                .windDirection(response.getDaily().getWindDirection10mDominant().subList(0, dailyLimit))
                .build();

        return LocationWeatherData.builder()
                .current(current)
                .hourly(hourly)
                .daily(daily)
                .build();
    }

    public LocationAirQualityData mapToAirQualityData(AirQualityResponse response,  WeatherForecastResponse weather) {
        if (response == null) return null;

        var current = LocationAirQualityData.CurrentData.builder()
                .aqi(response.getCurrent().getUsAqi())
                .pm2_5(response.getCurrent().getPm2_5())
                .pm10(response.getCurrent().getPm10())
                .o3(response.getCurrent().getO3())
                .co(response.getCurrent().getCo())
                .no2(response.getCurrent().getNo2())
                .so2(response.getCurrent().getSo2())
                .build();

        int hourlyLimit = Math.min(response.getHourly().getTime().size(), 24);
        int currentHour = getCurrentHourFromTimestamp(response.getCurrent().getTime(), response.getUtcOffsetSeconds());
        int startIndex = Math.max(0, currentHour);
        int endIndex = startIndex + hourlyLimit;

        var hourly = LocationAirQualityData.Forecast.builder()
                .time(response.getHourly().getTime().subList(startIndex, endIndex))
                .aqi(response.getHourly().getUsAqi().subList(startIndex, endIndex))
                .pm2_5(response.getHourly().getPm2_5().subList(startIndex, endIndex))
                .pm10(response.getHourly().getPm10().subList(startIndex, endIndex))
                .o3(response.getHourly().getO3().subList(startIndex, endIndex))
                .co(response.getHourly().getCo().subList(startIndex, endIndex))
                .no2(response.getHourly().getNo2().subList(startIndex, endIndex))
                .so2(response.getHourly().getSo2().subList(startIndex, endIndex))
                .build();

        List<Long> dailyDates = extractDailyDates(weather, forecastDays);

        Integer utcOffset = response.getUtcOffsetSeconds();

        var daily = LocationAirQualityData.Forecast.builder()
                .time(dailyDates)
                .aqi(aggregateDailyInt(dailyDates, response.getHourly().getTime(), response.getHourly().getUsAqi(), utcOffset))
                .pm2_5(aggregateDailyDouble(dailyDates, response.getHourly().getTime(), response.getHourly().getPm2_5(), utcOffset))
                .pm10(aggregateDailyDouble(dailyDates, response.getHourly().getTime(), response.getHourly().getPm10(), utcOffset))
                .o3(aggregateDailyDouble(dailyDates, response.getHourly().getTime(), response.getHourly().getO3(), utcOffset))
                .co(aggregateDailyDouble(dailyDates, response.getHourly().getTime(), response.getHourly().getCo(), utcOffset))
                .no2(aggregateDailyDouble(dailyDates, response.getHourly().getTime(), response.getHourly().getNo2(), utcOffset))
                .so2(aggregateDailyDouble(dailyDates, response.getHourly().getTime(), response.getHourly().getSo2(), utcOffset))
                .build();


        return LocationAirQualityData.builder()
                .current(current)
                .hourly(hourly)
                .daily(daily)
                .build();
    }

    public LocationClimateSummaryData mapToLocationClimateSummaryData(WeatherForecastResponse weather, AirQualityResponse aqi, ReverseGeocodingResponse geo) {
        if (weather == null || aqi == null) return null;

        return LocationClimateSummaryData.builder()
                .latitude(weather.getLatitude())
                .longitude(weather.getLongitude())
                .timezone(weather.getTimezone())
                .timestamp(Instant.now().getEpochSecond())
                .utcOffsetSeconds(weather.getUtcOffsetSeconds())
                .name(geo != null ? geo.getFormattedName() : "Unknown")
                .country(geo != null ? geo.getCountryName() : null)
                .current(mapToLocationClimateCurrentData(weather, aqi))
                .forecast(mapToLocationClimateForecastData(weather, aqi))
                .build();
    }

    public LocationClimateSummaryData.CurrentData mapToLocationClimateCurrentData(WeatherForecastResponse weather, AirQualityResponse aqi) {
        if (weather == null || aqi == null) return null;

        return LocationClimateSummaryData.CurrentData.builder()
                .temperature(weather.getCurrent().getTemperature2m())
                .weatherCode(weather.getCurrent().getWeatherCode())
                .aqi(aqi.getCurrent().getUsAqi())
                .build();
    }

    public LocationClimateSummaryData.ForecastData mapToLocationClimateForecastData(WeatherForecastResponse weather, AirQualityResponse aqi) {
        if (weather == null || aqi == null) return null;

        List<Long> dailyDates = extractDailyDates(weather, summaryForecastDays);
        Integer utcOffset = weather.getUtcOffsetSeconds();

        return LocationClimateSummaryData.ForecastData.builder()
                .time(dailyDates)
                .temperatureMax(weather.getDaily().getTemperature2mMax())
                .temperatureMin(weather.getDaily().getTemperature2mMin())
                .weatherCode(weather.getDaily().getWeatherCode())
                .aqi(aggregateDailyInt(dailyDates, aqi.getHourly().getTime(), aqi.getHourly().getUsAqi(), utcOffset))
                .build();
    }

    public LocationAirQualityHistoryData mapToLocationAQHistoryData(AirQualityResponse aqi) {
        if (aqi == null || aqi.getHourly() == null) return null;

        long currentEpoch = Instant.now().getEpochSecond();
        long cutoffTime = currentEpoch - (48 * 60 * 60);

        List<Long> allTimes = aqi.getHourly().getTime();
        int startIndex = 0;

        for (int i = 0; i < allTimes.size(); i++) {
            if (allTimes.get(i) >= cutoffTime) {
                startIndex = i;
                break;
            }
        }

        var hourly = LocationAirQualityData.Forecast.builder()
                .time(sliceList(aqi.getHourly().getTime(), startIndex))
                .aqi(sliceList(aqi.getHourly().getUsAqi(), startIndex))
                .pm2_5(sliceList(aqi.getHourly().getPm2_5(), startIndex))
                .pm10(sliceList(aqi.getHourly().getPm10(), startIndex))
                .o3(sliceList(aqi.getHourly().getO3(), startIndex))
                .co(sliceList(aqi.getHourly().getCo(), startIndex))
                .no2(sliceList(aqi.getHourly().getNo2(), startIndex))
                .so2(sliceList(aqi.getHourly().getSo2(), startIndex))
                .build();

        int utcOffset = (aqi.getUtcOffsetSeconds() != null) ? aqi.getUtcOffsetSeconds() : 0;
        List<Long> dailyDates = buildAQDailyDates(aqi);

        var daily = LocationAirQualityData.Forecast.builder()
                .time(dailyDates)
                .aqi(aggregateDailyInt(dailyDates, allTimes, aqi.getHourly().getUsAqi(), utcOffset))
                .pm2_5(aggregateDailyDouble(dailyDates, allTimes, aqi.getHourly().getPm2_5(), utcOffset))
                .pm10(aggregateDailyDouble(dailyDates, allTimes, aqi.getHourly().getPm10(), utcOffset))
                .o3(aggregateDailyDouble(dailyDates, allTimes, aqi.getHourly().getO3(), utcOffset))
                .co(aggregateDailyDouble(dailyDates, allTimes, aqi.getHourly().getCo(), utcOffset))
                .no2(aggregateDailyDouble(dailyDates, allTimes, aqi.getHourly().getNo2(), utcOffset))
                .so2(aggregateDailyDouble(dailyDates, allTimes, aqi.getHourly().getSo2(), utcOffset))
                .build();

        return LocationAirQualityHistoryData.builder()
                .latitude(aqi.getLatitude())
                .longitude(aqi.getLongitude())
                .timezone(aqi.getTimezone())
                .timestamp(currentEpoch)
                .utcOffsetSeconds(utcOffset)
                .hourly(hourly)
                .daily(daily)
                .build();
    }

    private List<Long> buildAQDailyDates(AirQualityResponse aqi) {
        if (aqi == null || aqi.getHourly() == null) return Collections.emptyList();

        int utcOffset = (aqi.getUtcOffsetSeconds() != null) ? aqi.getUtcOffsetSeconds() : 0;

        Set<String> dailyDatesSet = new HashSet<>();
        List<Long> dailyDates = new ArrayList<>();

        aqi.getHourly().getTime().forEach(timestamp -> {
            var dateKey = getDayKey(timestamp, utcOffset);
            boolean isNewDate = dailyDatesSet.add(dateKey);
            if (isNewDate) dailyDates.add(timestamp);
        });

        return dailyDates;
    }

    private List<Long> extractDailyDates(WeatherForecastResponse response, int limit) {
        if (response == null || response.getDaily() == null) return Collections.emptyList();

        int dailyLimit = Math.min(response.getDaily().getTime().size(), limit);
        return response.getDaily().getTime().subList(0, dailyLimit);
    }

    private int getCurrentHourFromTimestamp(Long timestamp, Integer utcOffsetSeconds) {
        if (timestamp == null || utcOffsetSeconds == null) {
            return 0;
        }

        try {
            ZoneOffset offset = ZoneOffset.ofTotalSeconds(utcOffsetSeconds);
            Instant instant = Instant.ofEpochSecond(timestamp);
            OffsetDateTime offsetDateTime = OffsetDateTime.ofInstant(instant, offset);

            return offsetDateTime.getHour();
        } catch (Exception e) {
            return 0;
        }
    }

    private <T> List<T> sliceList(List<T> list, int start) {
        if (list == null) return Collections.emptyList();
        if (start >= list.size()) return Collections.emptyList();
        if (start <= 0) return list;

        return list.subList(start, list.size());
    }

    private String getDayKey(Long timestamp, Integer utcOffsetSeconds) {
        if (timestamp == null) return "";
        // If offset is null, assume UTC (0)
        int offset = (utcOffsetSeconds != null) ? utcOffsetSeconds : 0;
        return Instant.ofEpochSecond(timestamp + offset)
                .atZone(ZoneId.of("UTC"))
                .toLocalDate()
                .toString();
    }

    private List<Double> aggregateDailyDouble(List<Long> targetDailyDates, List<Long> hourlyTimes, List<Double> hourlyValues, Integer utcOffset) {
        return aggregateDailyGeneric(
                targetDailyDates,
                hourlyTimes,
                hourlyValues,
                utcOffset,
                (values) -> {
                    if (values.isEmpty()) return 0.0;
                    return values.stream()
                            .filter(Objects::nonNull)
                            .mapToDouble(Double::doubleValue)
                            .average()
                            .orElse(0.0);
                }
        );
    }

    private List<Integer> aggregateDailyInt(List<Long> targetDailyDates, List<Long> hourlyTimes, List<Integer> hourlyValues, Integer utcOffset) {
        return aggregateDailyGeneric(
                targetDailyDates,
                hourlyTimes,
                hourlyValues,
                utcOffset,
                (values) -> {
                    if (values.isEmpty()) return 0;
                    double avg = values.stream()
                            .filter(Objects::nonNull)
                            .mapToInt(Integer::intValue)
                            .average()
                            .orElse(0.0);
                    return (int) Math.round(avg);
                }
        );
    }

    private <T, R> List<R> aggregateDailyGeneric(
            List<Long> targetDailyDates,
            List<Long> hourlyTimes,
            List<T> hourlyValues,
            Integer utcOffset,
            Function<List<T>, R> aggregator
    ) {

        if (targetDailyDates == null || hourlyTimes == null || hourlyValues == null) {
            return Collections.emptyList();
        }

        Map<String, List<T>> dayToValues = new LinkedHashMap<>();

        for (int i = 0; i < hourlyTimes.size(); i++) {
            if (i >= hourlyValues.size()) break;

            String day = getDayKey(hourlyTimes.get(i), utcOffset);
            dayToValues.computeIfAbsent(day, k -> new ArrayList<>()).add(hourlyValues.get(i));
        }

        return targetDailyDates.stream().map(dateTimestamp -> {
            String targetDay = getDayKey(dateTimestamp, utcOffset);
            List<T> values = dayToValues.getOrDefault(targetDay, Collections.emptyList());

            return aggregator.apply(values);

        }).collect(Collectors.toList());
    }
}
