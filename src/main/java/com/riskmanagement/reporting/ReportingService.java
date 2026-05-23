package com.riskmanagement.reporting;

import com.riskmanagement.dto.response.ExposureSummaryResponse;
import com.riskmanagement.dto.response.PnLSummaryResponse;
import com.riskmanagement.dto.response.TopRiskyTraderResponse;

import java.util.List;

/**
 * Reporting Module — generates risk and PnL reports.
 * <p>
 * In investment banking, risk reports are critical for:
 * - Senior management oversight
 * - Board-level risk committee reviews
 * - Regulatory reporting (to central banks, financial regulators)
 * - Internal audit and compliance
 */
public interface ReportingService {

    List<TopRiskyTraderResponse> getTopRiskyTraders(int limit);

    List<ExposureSummaryResponse> getExposureSummaryByDesk();

    List<PnLSummaryResponse> getPnLSummary();
}
