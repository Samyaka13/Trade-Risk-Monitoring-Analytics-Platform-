package com.riskmanagement.dto.response;

import com.riskmanagement.entity.enums.AlertType;
import com.riskmanagement.entity.enums.Severity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RiskAlertResponse {

    private UUID id;
    private UUID traderId;
    private String traderName;
    private AlertType alertType;
    private Severity severity;
    private String message;
    private Boolean acknowledged;
    private LocalDateTime createdAt;
}
