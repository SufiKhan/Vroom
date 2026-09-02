package com.example.trading.vroom.domain;

import java.math.BigDecimal;
import java.time.Instant;

public record LimitOrder(
        String id,
        String userId,
        String symbol,
        OrderSide side,
        BigDecimal quantity,
        BigDecimal limitPrice,
        Instant createdAt
) {
}
