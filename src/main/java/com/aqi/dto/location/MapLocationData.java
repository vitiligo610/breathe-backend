package com.aqi.dto.location;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MapLocationData {

    private Double latitude;
    private Double longitude;

    @JsonProperty("utc_offset_seconds")
    private Integer utcOffsetSeconds;

    // aqi or report
    @JsonProperty("pin_type")
    private String pinType;

    private Integer aqi;

    @JsonProperty("is_cluster")
    private boolean isCluster;

    @JsonProperty("point_count")
    private Integer pointCount;

    @JsonProperty("report_id")
    private Long reportId;

    @JsonProperty("report_type")
    private String reportType;

    @JsonProperty("report_description")
    private String reportDescription;

    @JsonProperty("reported_at")
    private Long reportedAt;
}
