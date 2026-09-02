package com.example.trading.vroom.api;

import com.example.trading.vroom.api.dto.MarketTickResponse;
import com.example.trading.vroom.ports.MarketDataPublisher;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/api/market-data")
public class MarketDataController {

    private final MarketDataPublisher marketDataPublisher;

    public MarketDataController(MarketDataPublisher marketDataPublisher) {
        this.marketDataPublisher = marketDataPublisher;
    }

    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<MarketTickResponse> streamMarketData() {
        return marketDataPublisher.stream()
                .map(tick -> new MarketTickResponse(
                        tick.price(),
                        tick.quantity(),
                        tick.timestamp()
                ));
    }
}
