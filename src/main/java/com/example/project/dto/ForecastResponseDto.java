package com.example.project.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ForecastResponseDto(
        @JsonProperty("productCode") String productCode,
        @JsonProperty("expectedNextMonthDemand") Double expectedNextMonthDemand,
        @JsonProperty("calculatedSafetyStock") Double calculatedSafetyStock,
        @JsonProperty("leadTimeWeeks") Integer leadTimeWeeks,
        @JsonProperty("suggestedUnitsToBuy") Integer suggestedUnitsToBuy
) {}
