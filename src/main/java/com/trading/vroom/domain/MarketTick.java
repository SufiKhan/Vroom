package com.example.trading.vroom.domain;

import java.math.BigDecimal;
import java.time.Instant;

public record MarketTick(
        String symbol,
        BigDecimal price,
        BigDecimal quantity,
        Instant timestamp
) {
}
