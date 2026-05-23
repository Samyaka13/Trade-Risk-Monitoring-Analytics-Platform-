package com.riskmanagement.service;

import com.riskmanagement.dto.response.PositionResponse;

import java.util.List;
import java.util.UUID;

public interface PositionService {

    void recalculatePositions(UUID traderId, String assetSymbol);

    List<PositionResponse> getPositionsByTrader(UUID traderId);

    List<PositionResponse> getLargestPositions(int limit);
}
