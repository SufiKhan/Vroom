package com.example.trading.vroom.config;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import static org.junit.jupiter.api.Assertions.assertEquals;

class VroomApiKeyWebFilterTest {

    @Test
    void rejectsRequestsWithoutApiKeyHeader() {
        VroomApiKeyWebFilter filter = new VroomApiKeyWebFilter("vroom-access-key");
        MockServerWebExchange exchange = MockServerWebExchange.from(
                MockServerHttpRequest.post("/api/orders/limit").build()
        );

        filter.filter(exchange, chainFor(exchange)).block();

        assertEquals(HttpStatus.UNAUTHORIZED, exchange.getResponse().getStatusCode());
    }

    @Test
    void acceptsRequestsWithValidApiKeyHeader() {
        VroomApiKeyWebFilter filter = new VroomApiKeyWebFilter("vroom-access-key");
        MockServerWebExchange exchange = MockServerWebExchange.from(
                MockServerHttpRequest.post("/api/orders/limit")
                        .header("X-API-KEY", "vroom-access-key")
                        .build()
        );

        filter.filter(exchange, chainFor(exchange)).block();

        assertEquals(HttpStatus.OK, exchange.getResponse().getStatusCode());
    }

    private WebFilterChain chainFor(MockServerWebExchange exchange) {
        return exchange1 -> {
            exchange1.getResponse().setStatusCode(HttpStatus.OK);
            return Mono.empty();
        };
    }
}
