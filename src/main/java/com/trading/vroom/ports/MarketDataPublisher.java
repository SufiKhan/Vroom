package com.trading.vroom.ports;

import com.trading.vroom.domain.MarketTick;
import reactor.core.publisher.Flux;

public interface MarketDataPublisher {
    void publish(MarketTick tick);

    Flux<MarketTick> stream();
}
