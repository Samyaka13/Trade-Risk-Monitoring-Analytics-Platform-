package com.riskmanagement.controller;

import com.riskmanagement.dto.response.ApiResponse;
import com.riskmanagement.dto.response.RiskAlertResponse;
import com.riskmanagement.service.RiskAlertService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/alerts")
@RequiredArgsConstructor
@Tag(name = "Alert Management", description = "Risk alert monitoring and acknowledgement")
public class AlertController {

    private final RiskAlertService riskAlertService;

    @GetMapping
    @Operation(summary = "Get all active (unacknowledged) alerts")
    public ResponseEntity<ApiResponse<List<RiskAlertResponse>>> getAllAlerts() {
        List<RiskAlertResponse> alerts = riskAlertService.getAllAlerts();
        return ResponseEntity.ok(ApiResponse.success(alerts));
    }

    @GetMapping("/critical")
    @Operation(summary = "Get critical unacknowledged alerts",
            description = "Returns only CRITICAL severity alerts that haven't been acknowledged")
    public ResponseEntity<ApiResponse<List<RiskAlertResponse>>> getCriticalAlerts() {
        List<RiskAlertResponse> alerts = riskAlertService.getCriticalAlerts();
        return ResponseEntity.ok(ApiResponse.success(alerts));
    }

    @GetMapping("/trader/{traderId}")
    @Operation(summary = "Get alerts for a specific trader")
    public ResponseEntity<ApiResponse<List<RiskAlertResponse>>> getAlertsByTrader(
            @PathVariable UUID traderId) {
        List<RiskAlertResponse> alerts = riskAlertService.getAlertsByTrader(traderId);
        return ResponseEntity.ok(ApiResponse.success(alerts));
    }

    @PostMapping("/{alertId}/acknowledge")
    @Operation(summary = "Acknowledge a risk alert",
            description = "Mark an alert as reviewed by the risk team")
    public ResponseEntity<ApiResponse<String>> acknowledgeAlert(@PathVariable UUID alertId) {
        riskAlertService.acknowledgeAlert(alertId);
        return ResponseEntity.ok(ApiResponse.success("Alert acknowledged successfully"));
    }
}
