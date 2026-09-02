package com.example.trading.vroom.infrastructure;

import com.example.trading.vroom.domain.MarketTick;
import com.example.trading.vroom.ports.MarketDataPublisher;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

@Component
public class ReactiveMarketDataPublisher implements MarketDataPublisher {
    private final Sinks.Many<MarketTick> sink = Sinks.many().multicast().directBestEffort();

    @Override
    public void publish(MarketTick tick) {
        sink.emitNext(tick, Sinks.EmitFailureHandler.busyLooping(java.time.Duration.ofMillis(10)));
    }

    @Override
    public Flux<MarketTick> stream() {
        return sink.asFlux();
    }
}
