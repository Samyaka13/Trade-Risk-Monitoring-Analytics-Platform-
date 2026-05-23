package com.riskmanagement.mapper;

import com.riskmanagement.dto.response.RiskAlertResponse;
import com.riskmanagement.entity.RiskAlert;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface RiskAlertMapper {

    @Mapping(target = "traderId", source = "trader.id")
    @Mapping(target = "traderName", source = "trader.name")
    RiskAlertResponse toResponse(RiskAlert alert);

    List<RiskAlertResponse> toResponseList(List<RiskAlert> alerts);
}
