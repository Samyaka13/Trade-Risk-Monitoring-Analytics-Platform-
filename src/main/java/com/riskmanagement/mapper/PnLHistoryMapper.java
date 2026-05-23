package com.riskmanagement.mapper;

import com.riskmanagement.dto.response.PnLHistoryResponse;
import com.riskmanagement.entity.PnLHistory;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PnLHistoryMapper {

    @Mapping(target = "traderId", source = "trader.id")
    @Mapping(target = "traderName", source = "trader.name")
    PnLHistoryResponse toResponse(PnLHistory pnlHistory);

    List<PnLHistoryResponse> toResponseList(List<PnLHistory> pnlHistories);
}
