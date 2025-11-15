package com.aqi.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

import java.time.Instant;

@Entity
@Table(name = "sensor_readings")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SensorReading {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "sensor_id")
    private SensorNode sensorNode;

    private Instant timestamp;

    @ColumnDefault("0")
    private double temperature = 0;

    @ColumnDefault("0")
    private double humidity = 0;

    @Column(name = "mq4_ch4")
    @ColumnDefault("0")
    private double mq4Ch4 = 0;

    @Column(name = "mq7_co")
    @ColumnDefault("0")
    private double mq7Co = 0;

    @Column(name = "mq135_air")
    @ColumnDefault("0")
    private double mq135Air = 0;

    @Column(name = "dust_ugm3")
    @ColumnDefault("0")
    private double dustUgm3 = 0;

    @PrePersist
    protected void onCreate() {
        if (timestamp == null) {
            timestamp = Instant.now();
        }
    }
}
