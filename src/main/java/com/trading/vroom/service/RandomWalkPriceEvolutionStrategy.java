package com.trading.vroom.service;

import com.trading.vroom.config.MarketSimulationProperties;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class RandomWalkPriceEvolutionStrategy implements PriceEvolutionStrategy {

    private final MarketSimulationProperties properties;

    public RandomWalkPriceEvolutionStrategy(MarketSimulationProperties properties) {
        this.properties = properties;
    }

    @Override
    public BigDecimal nextPrice(BigDecimal currentPrice) {
        double min = properties.getMinPercentChange();
        double max = properties.getMaxPercentChange();
        double randomPercent = ThreadLocalRandom.current().nextDouble(min, max);

        BigDecimal multiplier = BigDecimal.ONE.add(BigDecimal.valueOf(randomPercent).movePointLeft(2));
        BigDecimal next = currentPrice.multiply(multiplier).setScale(2, RoundingMode.HALF_UP);

        BigDecimal minBound = currentPrice.multiply(BigDecimal.valueOf(1d + (min / 100d))).setScale(2, RoundingMode.HALF_UP);
        BigDecimal maxBound = currentPrice.multiply(BigDecimal.valueOf(1d + (max / 100d))).setScale(2, RoundingMode.HALF_UP);

        if (next.compareTo(minBound) < 0) {
            next = minBound;
        }
        if (next.compareTo(maxBound) > 0) {
            next = maxBound;
        }

        if (next.compareTo(BigDecimal.valueOf(0.01)) < 0) {
            return BigDecimal.valueOf(0.01);
        }

        return next;
    }
}
