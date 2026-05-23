package com.riskmanagement.service;

import com.riskmanagement.dto.request.TradeCreateRequest;
import com.riskmanagement.dto.request.TradeUpdateRequest;
import com.riskmanagement.dto.response.TradeResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface TradeService {

    TradeResponse createTrade(TradeCreateRequest request);

    TradeResponse updateTrade(UUID tradeId, TradeUpdateRequest request);

    TradeResponse closeTrade(UUID tradeId);

    TradeResponse getTradeById(UUID tradeId);

    Page<TradeResponse> getAllTrades(Pageable pageable);
}
