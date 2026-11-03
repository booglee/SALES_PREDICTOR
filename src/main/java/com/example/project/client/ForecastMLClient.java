package com.example.project.client;

import com.example.project.dto.ForecastRequestDto;
import com.example.project.dto.ForecastResponseDto;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class ForecastMLClient {

    private final RestTemplate restTemplate;
    private final String ANALYTICS_SERVICE_URL = "http://localhost:8000/api/v1/analytics/procure-units";

    public ForecastMLClient() {
        this.restTemplate = new RestTemplate();
    }

    /**
     * Dispatches an optimization payload synchronously to the Python predictive analytics service.
     */
    public ForecastResponseDto fetchProcurementSuggestion(ForecastRequestDto requestDto) {
        try {
            return restTemplate.postForObject(ANALYTICS_SERVICE_URL, requestDto, ForecastResponseDto.class);
        } catch (Exception error) {
            System.err.println("Predictive subsystem integration fault: " + error.getMessage());
            // Returns a safe fallback empty container to prevent backend service crashes
            return new ForecastResponseDto(requestDto.productCode(), 0.0, 5.0, requestDto.leadTimeWeeks(), 0);
        }
    }
}
