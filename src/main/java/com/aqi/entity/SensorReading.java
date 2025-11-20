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

    @ColumnDefault("EXTRACT(EPOCH FROM NOW())")
    private Long timestamp;

    @ColumnDefault("0")
    private Double temperature;

    @ColumnDefault("0")
    private Double humidity;

    @Column(name = "mq4_ch4")
    @ColumnDefault("0")
    private Double mq4Ch4;

    @Column(name = "mq7_co")
    @ColumnDefault("0")
    private Double mq7Co;

    @Column(name = "mq135_air")
    @ColumnDefault("0")
    private Double mq135Air;

    @Column(name = "dust_ugm3")
    @ColumnDefault("0")
    private Double dustUgm3;

    @PrePersist
    protected void onCreate() {
        if (timestamp == null) {
            timestamp = Instant.now().getEpochSecond();
        }
    }
}
