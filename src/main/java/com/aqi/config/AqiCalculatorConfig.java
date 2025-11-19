package com.aqi.config;

import com.aqi.util.AqiCalculator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AqiCalculatorConfig {
    @Bean
    public AqiCalculator aqiCalculator() {
        return new AqiCalculator();
    }
}
