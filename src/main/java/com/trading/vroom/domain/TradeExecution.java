package com.example.trading.vroom.domain;

import java.math.BigDecimal;
import java.time.Instant;

public record TradeExecution(
        String orderId,
        String userId,
        String symbol,
        OrderSide side,
        BigDecimal quantity,
        BigDecimal executionPrice,
        BigDecimal notional,
        Instant timestamp
) {
}
