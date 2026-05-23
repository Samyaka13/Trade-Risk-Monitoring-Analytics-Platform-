package com.riskmanagement.mapper;

import com.riskmanagement.dto.response.PositionResponse;
import com.riskmanagement.entity.Position;
import org.mapstruct.*;

import java.math.BigDecimal;
import java.util.List;

@Mapper(componentModel = "spring")
public interface PositionMapper {

    @Mapping(target = "traderId", source = "trader.id")
    @Mapping(target = "traderName", source = "trader.name")
    @Mapping(target = "direction", expression = "java(getDirection(position))")
    PositionResponse toResponse(Position position);

    List<PositionResponse> toResponseList(List<Position> positions);

    default String getDirection(Position position) {
        if (position.getNetQuantity() == null) return "FLAT";
        int cmp = position.getNetQuantity().compareTo(BigDecimal.ZERO);
        if (cmp > 0) return "LONG";
        if (cmp < 0) return "SHORT";
        return "FLAT";
    }
}
