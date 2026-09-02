package com.example.trading.vroom.api.dto;

import java.math.BigDecimal;
import java.util.Map;

public record AccountResponse(
        String userId,
        BigDecimal cashBalance,
        Map<String, BigDecimal> positions
) {
}
