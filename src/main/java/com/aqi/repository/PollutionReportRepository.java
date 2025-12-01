package com.aqi.repository;

import com.aqi.entity.PollutionReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PollutionReportRepository extends JpaRepository<PollutionReport, Long> {

    @Query("SELECT p FROM PollutionReport p WHERE p.latitude BETWEEN :minLat AND :maxLat AND p.longitude BETWEEN :minLon AND :maxLon")
    List<PollutionReport> findReportsInBounds(
            @Param("minLat") Double minLat,
            @Param("maxLat") Double maxLat,
            @Param("minLon") Double minLon,
            @Param("maxLon") Double maxLon
    );

    @Query(value = """
        SELECT * FROM pollution_reports p 
        WHERE (6371 * acos(cos(radians(:lat)) * cos(radians(p.latitude)) * cos(radians(p.longitude) - radians(:lon)) + sin(radians(:lat)) * sin(radians(p.latitude)))) < :radius
        ORDER BY p.reported_at DESC
        """, nativeQuery = true)
    List<PollutionReport> findReportsNearLocation(
            @Param("lat") Double latitude,
            @Param("lon") Double longitude,
            @Param("radius") Double radiusKm
    );
}
