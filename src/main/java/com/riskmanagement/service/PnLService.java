package com.riskmanagement.service;

import com.riskmanagement.dto.response.PnLHistoryResponse;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface PnLService {

    void recordDailyPnL(UUID traderId);

    List<PnLHistoryResponse> getPnLHistory(UUID traderId);

    List<PnLHistoryResponse> getPnLHistoryBetween(UUID traderId, LocalDate start, LocalDate end);
}
