package com.aqi.dto.report;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PollutionReportDto {
    private Long id;
    private Double latitude;
    private Double longitude;

    @JsonProperty("report_type")
    private ReportType reportType;

    @JsonProperty("display_type")
    private String displayType;

    private String description;

    @JsonProperty("reported_at")
    private Long reportedAt;

    @JsonProperty("user_name")
    private String userName;
}
