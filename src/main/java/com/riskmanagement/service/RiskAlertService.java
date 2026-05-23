package com.riskmanagement.service;

import com.riskmanagement.dto.response.RiskAlertResponse;

import java.util.List;
import java.util.UUID;

public interface RiskAlertService {

    List<RiskAlertResponse> getAllAlerts();

    List<RiskAlertResponse> getCriticalAlerts();

    List<RiskAlertResponse> getAlertsByTrader(UUID traderId);

    void acknowledgeAlert(UUID alertId);
}
