package com.trading.vroom.service;

import java.math.BigDecimal;

public interface PriceEvolutionStrategy {
    BigDecimal nextPrice(BigDecimal currentPrice);
}
