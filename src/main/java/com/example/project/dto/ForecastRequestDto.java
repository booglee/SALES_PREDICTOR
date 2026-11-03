package com.example.project.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ForecastRequestDto(
        @JsonProperty("product_code") String productCode,
        @JsonProperty("lead_time_weeks") Integer leadTimeWeeks,
        @JsonProperty("service_level_coefficient") Double serviceLevelCoefficient
) {
    // Convenience constructor using production-grade defaults (4-week lead time, 95% service level factor)
    public ForecastRequestDto(String productCode) {
        this(productCode, 4, 1.65);
    }
}
