package com.riskmanagement.controller;

import com.riskmanagement.dto.response.ApiResponse;
import com.riskmanagement.dto.response.RiskMetricsResponse;
import com.riskmanagement.riskengine.RiskEngineService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/risk")
@RequiredArgsConstructor
@Tag(name = "Risk Engine", description = "Risk exposure, PnL, VaR calculations and breach monitoring")
public class RiskController {

    private final RiskEngineService riskEngineService;

    @GetMapping("/exposure/{traderId}")
    @Operation(summary = "Get exposure metrics for a trader",
            description = "Returns total exposure, risk limit utilization, and breach status")
    public ResponseEntity<ApiResponse<RiskMetricsResponse>> getExposure(@PathVariable UUID traderId) {
        RiskMetricsResponse metrics = riskEngineService.getRiskMetrics(traderId);
        return ResponseEntity.ok(ApiResponse.success(metrics));
    }

    @GetMapping("/pnl/{traderId}")
    @Operation(summary = "Get PnL for a trader",
            description = "Returns unrealized PnL, realized PnL, and total PnL")
    public ResponseEntity<ApiResponse<RiskMetricsResponse>> getPnL(@PathVariable UUID traderId) {
        RiskMetricsResponse metrics = riskEngineService.getRiskMetrics(traderId);
        return ResponseEntity.ok(ApiResponse.success(metrics));
    }

    @GetMapping("/var/{traderId}")
    @Operation(summary = "Get VaR estimate for a trader",
            description = "Returns the parametric Value-at-Risk estimate (95% confidence, 1-day)")
    public ResponseEntity<ApiResponse<RiskMetricsResponse>> getVaR(@PathVariable UUID traderId) {
        RiskMetricsResponse metrics = riskEngineService.getRiskMetrics(traderId);
        return ResponseEntity.ok(ApiResponse.success(metrics));
    }

    @GetMapping("/breaches")
    @Operation(summary = "Get all risk limit breaches",
            description = "Returns all traders currently in breach of their risk limits")
    public ResponseEntity<ApiResponse<List<RiskMetricsResponse>>> getBreaches() {
        List<RiskMetricsResponse> breaches = riskEngineService.getBreaches();
        return ResponseEntity.ok(ApiResponse.success(breaches));
    }

    @PostMapping("/recalculate")
    @Operation(summary = "Trigger risk recalculation for all traders",
            description = "Manually trigger a full risk recalculation. Normally runs automatically on trade events.")
    public ResponseEntity<ApiResponse<String>> recalculateAll() {
        riskEngineService.recalculateAllTraders();
        return ResponseEntity.ok(ApiResponse.success("Risk recalculation triggered for all active traders"));
    }
}
