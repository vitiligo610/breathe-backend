package com.aqi.service;

import com.aqi.request.CreateReportRequest;
import com.aqi.dto.report.PollutionReportDto;
import com.aqi.entity.PollutionReport;
import com.aqi.repository.PollutionReportRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommunityReportService {

    private final PollutionReportRepository reportRepository;

    public PollutionReportDto createReport(CreateReportRequest request) {
        log.info("Creating pollution report at [{}, {}] Type: {}",
                request.getLatitude(), request.getLongitude(), request.getReportType());

        PollutionReport entity = PollutionReport.builder()
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .reportType(request.getReportType())
                .description(request.getDescription())
                .reportedAt(Instant.now())
                .userName(request.getUserName() != null && !request.getUserName().isBlank()
                        ? request.getUserName() : "Anonymous")
                .build();

        PollutionReport saved = reportRepository.save(entity);

        return mapToDto(saved);
    }

    public List<PollutionReportDto> getReportsInBoundingBox(List<Double> bbox) {
        if (bbox == null || bbox.size() != 4) {
            return Collections.emptyList();
        }

        double swLat = bbox.get(0);
        double swLon = bbox.get(1);
        double neLat = bbox.get(2);
        double neLon = bbox.get(3);

        List<PollutionReport> reports = reportRepository.findReportsInBounds(swLat, neLat, swLon, neLon);

        return reports.stream().map(this::mapToDto).collect(Collectors.toList());
    }

    public List<PollutionReportDto> getReportsNearLocation(Double latitude, Double longitude, Double radiusKm) {
        if (latitude == null || longitude == null) return Collections.emptyList();

        List<PollutionReport> reports = reportRepository.findReportsNearLocation(latitude, longitude, radiusKm);
        return reports.stream().map(this::mapToDto).collect(Collectors.toList());
    }

    private PollutionReportDto mapToDto(PollutionReport entity) {
        return PollutionReportDto.builder()
                .id(entity.getId())
                .latitude(entity.getLatitude())
                .longitude(entity.getLongitude())
                .reportType(entity.getReportType())
                .displayType(entity.getReportType().getDisplayName())
                .description(entity.getDescription())
                .reportedAt(entity.getReportedAt().getEpochSecond())
                .userName(entity.getUserName())
                .build();
    }
}