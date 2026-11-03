package com.example.project.dto; // Updated to match your project tree

import com.fasterxml.jackson.annotation.JsonProperty;

public record ProcurementSuggestionDto(
        @JsonProperty("productCode") String productCode,
        @JsonProperty("expectedNextMonthDemand") Double expectedNextMonthDemand,
        @JsonProperty("calculatedSafetyStock") Double calculatedSafetyStock,
        @JsonProperty("leadTimeWeeks") Integer leadTimeWeeks,
        @JsonProperty("suggestedUnitsToBuy") Integer suggestedUnitsToBuy
) {}
