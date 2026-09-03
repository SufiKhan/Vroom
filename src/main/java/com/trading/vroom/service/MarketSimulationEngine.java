package com.trading.vroom.service;

import com.trading.vroom.config.MarketSimulationProperties;
import com.trading.vroom.domain.MarketTick;
import com.trading.vroom.ports.MarketDataPublisher;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.stereotype.Service;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicReference;

@Service
public class MarketSimulationEngine {

    private final PriceEvolutionStrategy priceEvolutionStrategy;
    private final MarketDataPublisher marketDataPublisher;
    private final MatchingEngineService matchingEngineService;
    private final MarketSimulationProperties properties;
    private final AtomicReference<BigDecimal> currentPrice;

    private Disposable streamLoop;

    public MarketSimulationEngine(
            PriceEvolutionStrategy priceEvolutionStrategy,
            MarketDataPublisher marketDataPublisher,
            MatchingEngineService matchingEngineService,
            MarketSimulationProperties properties
    ) {
        this.priceEvolutionStrategy = priceEvolutionStrategy;
        this.marketDataPublisher = marketDataPublisher;
        this.matchingEngineService = matchingEngineService;
        this.properties = properties;
        this.currentPrice = new AtomicReference<>(BigDecimal.valueOf(properties.getInitialPrice()));
    }

    @PostConstruct
    public void start() {
        streamLoop = Flux.interval(Duration.ofSeconds(1))
                .doOnNext(ignored -> generateAndPublishTick())
                .subscribe();
    }

    @PreDestroy
    public void stop() {
        if (streamLoop != null && !streamLoop.isDisposed()) {
            streamLoop.dispose();
        }
    }

    private void generateAndPublishTick() {
        BigDecimal newPrice = priceEvolutionStrategy.nextPrice(currentPrice.get());
        currentPrice.set(newPrice);

        int quantityValue = ThreadLocalRandom.current().nextInt(
                properties.getMinQuantity(),
                properties.getMaxQuantity() + 1
        );

        MarketTick tick = new MarketTick(
                properties.getSymbol(),
                newPrice,
                BigDecimal.valueOf(quantityValue),
                Instant.now()
        );

        matchingEngineService.onMarketTick(tick);
        marketDataPublisher.publish(tick);
    }
}
