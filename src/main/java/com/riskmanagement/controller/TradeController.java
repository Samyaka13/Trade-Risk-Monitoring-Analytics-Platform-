package com.riskmanagement.controller;

import com.riskmanagement.dto.request.TradeCreateRequest;
import com.riskmanagement.dto.request.TradeUpdateRequest;
import com.riskmanagement.dto.response.ApiResponse;
import com.riskmanagement.dto.response.TradeResponse;
import com.riskmanagement.service.TradeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/trades")
@RequiredArgsConstructor
@Tag(name = "Trade Capture", description = "Trade lifecycle management — create, update, close, and query trades")
public class TradeController {

    private final TradeService tradeService;

    @PostMapping
    @Operation(summary = "Book a new trade",
            description = "Create and book a new trade. Automatically triggers position recalculation and risk engine update.")
    public ResponseEntity<ApiResponse<TradeResponse>> createTrade(
            @Valid @RequestBody TradeCreateRequest request) {
        TradeResponse trade = tradeService.createTrade(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Trade booked successfully", trade));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a trade",
            description = "Update mutable fields of an OPEN trade. Closed/cancelled trades cannot be modified.")
    public ResponseEntity<ApiResponse<TradeResponse>> updateTrade(
            @PathVariable UUID id,
            @Valid @RequestBody TradeUpdateRequest request) {
        TradeResponse trade = tradeService.updateTrade(id, request);
        return ResponseEntity.ok(ApiResponse.success("Trade updated successfully", trade));
    }

    @PostMapping("/{id}/close")
    @Operation(summary = "Close a trade",
            description = "Close an open trade at current market price. Locks in realized PnL.")
    public ResponseEntity<ApiResponse<TradeResponse>> closeTrade(@PathVariable UUID id) {
        TradeResponse trade = tradeService.closeTrade(id);
        return ResponseEntity.ok(ApiResponse.success("Trade closed successfully", trade));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get trade by ID")
    public ResponseEntity<ApiResponse<TradeResponse>> getTradeById(@PathVariable UUID id) {
        TradeResponse trade = tradeService.getTradeById(id);
        return ResponseEntity.ok(ApiResponse.success(trade));
    }

    @GetMapping
    @Operation(summary = "Get all trades (paginated)",
            description = "Retrieve all trades with pagination support. Default: page=0, size=20.")
    public ResponseEntity<ApiResponse<Page<TradeResponse>>> getAllTrades(
            @PageableDefault(size = 20) Pageable pageable) {
        Page<TradeResponse> trades = tradeService.getAllTrades(pageable);
        return ResponseEntity.ok(ApiResponse.success(trades));
    }
}
