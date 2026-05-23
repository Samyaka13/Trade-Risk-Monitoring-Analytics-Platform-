package com.riskmanagement.mapper;

import com.riskmanagement.dto.response.TradeResponse;
import com.riskmanagement.entity.Trade;
import com.riskmanagement.entity.enums.TradeType;
import org.mapstruct.*;

import java.math.BigDecimal;
import java.util.List;

@Mapper(componentModel = "spring")
public interface TradeMapper {

    @Mapping(target = "traderId", source = "trader.id")
    @Mapping(target = "traderName", source = "trader.name")
    @Mapping(target = "unrealizedPnl", expression = "java(calculateUnrealizedPnl(trade))")
    @Mapping(target = "exposure", expression = "java(calculateExposure(trade))")
    TradeResponse toResponse(Trade trade);

    List<TradeResponse> toResponseList(List<Trade> trades);

    default BigDecimal calculateUnrealizedPnl(Trade trade) {
        if (trade.getCurrentPrice() == null || trade.getEntryPrice() == null || trade.getQuantity() == null) {
            return BigDecimal.ZERO;
        }
        BigDecimal priceDiff = trade.getTradeType() == TradeType.BUY
                ? trade.getCurrentPrice().subtract(trade.getEntryPrice())
                : trade.getEntryPrice().subtract(trade.getCurrentPrice());
        return priceDiff.multiply(trade.getQuantity());
    }

    default BigDecimal calculateExposure(Trade trade) {
        if (trade.getCurrentPrice() == null || trade.getQuantity() == null) {
            return BigDecimal.ZERO;
        }
        return trade.getQuantity().multiply(trade.getCurrentPrice()).abs();
    }
}
