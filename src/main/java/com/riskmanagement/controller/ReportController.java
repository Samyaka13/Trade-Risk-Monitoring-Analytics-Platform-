package com.riskmanagement.controller;

import com.riskmanagement.dto.response.*;
import com.riskmanagement.reporting.ReportingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/reports")
@RequiredArgsConstructor
@Tag(name = "Reporting", description = "Risk and PnL reports for management and regulatory purposes")
public class ReportController {

    private final ReportingService reportingService;

    @GetMapping("/top-risky-traders")
    @Operation(summary = "Get top risky traders",
            description = "Traders ranked by risk limit utilization percentage. Default: top 10.")
    public ResponseEntity<ApiResponse<List<TopRiskyTraderResponse>>> getTopRiskyTraders(
            @RequestParam(defaultValue = "10") int limit) {
        List<TopRiskyTraderResponse> traders = reportingService.getTopRiskyTraders(limit);
        return ResponseEntity.ok(ApiResponse.success(traders));
    }

    @GetMapping("/exposure-summary")
    @Operation(summary = "Get exposure summary by desk",
            description = "Aggregated exposure and PnL broken down by trading desk")
    public ResponseEntity<ApiResponse<List<ExposureSummaryResponse>>> getExposureSummary() {
        List<ExposureSummaryResponse> summary = reportingService.getExposureSummaryByDesk();
        return ResponseEntity.ok(ApiResponse.success(summary));
    }

    @GetMapping("/pnl-summary")
    @Operation(summary = "Get PnL summary by trader",
            description = "PnL ranking showing best/worst performers with daily statistics")
    public ResponseEntity<ApiResponse<List<PnLSummaryResponse>>> getPnLSummary() {
        List<PnLSummaryResponse> summary = reportingService.getPnLSummary();
        return ResponseEntity.ok(ApiResponse.success(summary));
    }
}
