package com.aqi.scheduler;

import com.aqi.repository.AqiDataPointRepository;
import com.aqi.service.NotificationService;
import com.aqi.service.OpenWeatherApiClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class AqiDataPoller {
    
    @Autowired
    private OpenWeatherApiClient openWeatherApiClient;
    
    @Autowired
    private AqiDataPointRepository aqiDataPointRepository;
    
    @Autowired
    private NotificationService notificationService;

    // Poll every hour (3600000 milliseconds)
    @Scheduled(fixedRate = 3600000)
    public void pollAqiData() {

    }
}

