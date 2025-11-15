package com.aqi.config;

import com.hivemq.client.mqtt.MqttClient;
import com.hivemq.client.mqtt.mqtt3.Mqtt3AsyncClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.TrustManagerFactory;
import java.io.FileInputStream;
import java.security.KeyStore;

@Configuration
public class MqttClientConfig {
    @Value("${mqtt.host:localhost}")
    private String mqttHost;

    @Value("${mqtt.port:8883}")
    private int mqttPort;

    @Value("${mqtt.ssl.key-store-path}")
    private String keyStorePath;

    @Value("${mqtt.ssl.key-store-password}")
    private String keyStorePassword;

    @Value("${mqtt.ssl.trust-store-path}")
    private String trustStorePath;

    @Value("${mqtt.ssl.trust-store-password}")
    private String trustStorePassword;

    @Bean
    public KeyManagerFactory keyManagerFactory() throws Exception {
        KeyStore keyStore = KeyStore.getInstance("PKCS12");
        try (FileInputStream fis = new FileInputStream(keyStorePath)) {
            keyStore.load(fis, keyStorePassword.toCharArray());
        }

        KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        kmf.init(keyStore, keyStorePassword.toCharArray());
        return kmf;
    }

    @Bean
    public TrustManagerFactory trustManagerFactory() throws Exception {
        KeyStore trustStore = KeyStore.getInstance("JKS");
        try (FileInputStream fis = new FileInputStream(trustStorePath)) {
            trustStore.load(fis, trustStorePassword.toCharArray());
        }

        TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        tmf.init(trustStore);
        return tmf;
    }

    @Bean
    public Mqtt3AsyncClient mqttClient() throws Exception {
        return MqttClient.builder()
            .useMqttVersion3()
            .serverHost(mqttHost)
            .serverPort(mqttPort)
            .sslConfig()
                .keyManagerFactory(keyManagerFactory())
                .trustManagerFactory(trustManagerFactory())
                .applySslConfig()
            .automaticReconnect()
                .initialDelay(500, java.util.concurrent.TimeUnit.MILLISECONDS)
                .maxDelay(30, java.util.concurrent.TimeUnit.SECONDS)
                .applyAutomaticReconnect()
            .buildAsync();
    }
}
