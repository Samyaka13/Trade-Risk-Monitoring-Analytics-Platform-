package com.riskmanagement.service.impl;

import com.riskmanagement.dto.response.RiskAlertResponse;
import com.riskmanagement.entity.RiskAlert;
import com.riskmanagement.exception.ResourceNotFoundException;
import com.riskmanagement.mapper.RiskAlertMapper;
import com.riskmanagement.repository.RiskAlertRepository;
import com.riskmanagement.service.RiskAlertService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class RiskAlertServiceImpl implements RiskAlertService {

    private final RiskAlertRepository riskAlertRepository;
    private final RiskAlertMapper riskAlertMapper;

    @Override
    @Transactional(readOnly = true)
    public List<RiskAlertResponse> getAllAlerts() {
        List<RiskAlert> alerts = riskAlertRepository.findByAcknowledgedFalse();
        return riskAlertMapper.toResponseList(alerts);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RiskAlertResponse> getCriticalAlerts() {
        List<RiskAlert> criticalAlerts = riskAlertRepository.findCriticalActiveAlerts();
        return riskAlertMapper.toResponseList(criticalAlerts);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RiskAlertResponse> getAlertsByTrader(UUID traderId) {
        List<RiskAlert> alerts = riskAlertRepository.findByTraderId(traderId);
        return riskAlertMapper.toResponseList(alerts);
    }

    @Override
    public void acknowledgeAlert(UUID alertId) {
        RiskAlert alert = riskAlertRepository.findById(alertId)
                .orElseThrow(() -> new ResourceNotFoundException("RiskAlert", "id", alertId));
        alert.setAcknowledged(true);
        riskAlertRepository.save(alert);
        log.info("Alert acknowledged: {} (type: {}, severity: {})",
                alertId, alert.getAlertType(), alert.getSeverity());
    }
}
