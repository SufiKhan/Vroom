package com.trading.vroom.service;

import com.trading.vroom.config.MarketSimulationProperties;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertTrue;

class RandomWalkPriceEvolutionStrategyTest {

    @Test
    void generatedPriceStaysWithinConfiguredPercentageBounds() {
        MarketSimulationProperties properties = new MarketSimulationProperties();
        properties.setMinPercentChange(-1.5);
        properties.setMaxPercentChange(1.5);

        RandomWalkPriceEvolutionStrategy strategy = new RandomWalkPriceEvolutionStrategy(properties);
        BigDecimal current = BigDecimal.valueOf(100);

        for (int i = 0; i < 500; i++) {
            BigDecimal next = strategy.nextPrice(current);
            BigDecimal minBound = current.multiply(BigDecimal.valueOf(0.985));
            BigDecimal maxBound = current.multiply(BigDecimal.valueOf(1.015));

            assertTrue(next.compareTo(minBound) >= 0, "tick should not fall below -1.5% from last price");
            assertTrue(next.compareTo(maxBound) <= 0, "tick should not rise above +1.5% from last price");

            current = next;
        }
    }
}
