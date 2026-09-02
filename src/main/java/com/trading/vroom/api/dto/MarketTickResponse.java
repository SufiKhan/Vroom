package com.example.trading.vroom.api.dto;

import java.math.BigDecimal;
import java.time.Instant;

public record MarketTickResponse(
        BigDecimal price,
        BigDecimal quantity,
        Instant timestamp
) {
}
