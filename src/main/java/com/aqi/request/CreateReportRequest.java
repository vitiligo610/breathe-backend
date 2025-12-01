package com.aqi.request;

import com.aqi.dto.report.ReportType;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateReportRequest {

    private Double latitude;
    private Double longitude;

    @JsonProperty("report_type")
    private ReportType reportType;

    private String description;

    @JsonProperty("user_name")
    private String userName;
}
