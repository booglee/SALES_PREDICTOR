package com.example.project.controller;

import com.example.project.client.ForecastMLClient;
import com.example.project.dto.ForecastRequestDto;
import com.example.project.dto.ForecastResponseDto;
import com.example.project.model.ProcurementOrder;
import com.example.project.repo.ProductRepository;
import com.example.project.repo.ProcurementOrderRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/forecast")
@CrossOrigin(origins = "http://localhost:63342") // Fixes the browser CORS block from IntelliJ preview
public class ProductForecastController {

    private final ForecastMLClient forecastMLClient;
    private final ProductRepository productRepository;
    private final ProcurementOrderRepository procurementOrderRepository;

    public ProductForecastController(ForecastMLClient forecastMLClient,
                                     ProductRepository productRepository,
                                     ProcurementOrderRepository procurementOrderRepository) {
        this.forecastMLClient = forecastMLClient;
        this.productRepository = productRepository;
        this.procurementOrderRepository = procurementOrderRepository;
    }

    @GetMapping("/units-to-buy")
    public ResponseEntity<ForecastResponseDto> evaluateProcurementUnits(
            @RequestParam String productCode,
            @RequestParam(defaultValue = "4") int leadTime,
            @RequestParam(defaultValue = "1.65") double serviceLevel) {

        long count = productRepository.count();

        ForecastRequestDto requestPayload = new ForecastRequestDto(productCode, leadTime, serviceLevel);
        ForecastResponseDto analysisResult = forecastMLClient.fetchProcurementSuggestion(requestPayload);

        // Save directly into the SQL database using your ProcurementOrder entity
        ProcurementOrder order = new ProcurementOrder(
                analysisResult.productCode(),
                analysisResult.expectedNextMonthDemand(),
                analysisResult.calculatedSafetyStock(),
                analysisResult.leadTimeWeeks(),
                analysisResult.suggestedUnitsToBuy()
        );
        procurementOrderRepository.save(order);

        return ResponseEntity.ok(analysisResult);
    }

    @GetMapping("/all-suggestions")
    public ResponseEntity<List<ProcurementOrder>> getAllSuggestions() {
        // Fetches all persisted rows directly from your database table
        return new ResponseEntity<>(procurementOrderRepository.findAll(), HttpStatus.OK);
    }

    @PostMapping("/approve/{id}")
    public ResponseEntity<Void> approveSuggestion(@PathVariable Long id) {
        return procurementOrderRepository.findById(id).map(order -> {
            order.setStatus("APPROVED");
            procurementOrderRepository.save(order);
            return ResponseEntity.ok().<Void>build();
        }).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/reject/{id}")
    public ResponseEntity<Void> rejectSuggestion(@PathVariable Long id) {
        return procurementOrderRepository.findById(id).map(order -> {
            order.setStatus("REJECTED");
            procurementOrderRepository.save(order);
            return ResponseEntity.ok().<Void>build();
        }).orElse(ResponseEntity.notFound().build());
    }
}