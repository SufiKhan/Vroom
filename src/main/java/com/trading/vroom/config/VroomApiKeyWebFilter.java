package com.trading.vroom.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Component
public class VroomApiKeyWebFilter implements WebFilter {

    private final String expectedApiKey;

    public VroomApiKeyWebFilter(@Value("${vroom.api-key:default-secret-key}") String expectedApiKey) {
        this.expectedApiKey = expectedApiKey;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        if (exchange.getRequest().getMethod() == org.springframework.http.HttpMethod.OPTIONS) {
            return chain.filter(exchange);
        }

        String path = exchange.getRequest().getPath().value();
        if (requiresApiKey(path)) {
            String requestApiKey = exchange.getRequest().getHeaders().getFirst("X-API-KEY");
            if (requestApiKey == null || !requestApiKey.equals(expectedApiKey)) {
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                exchange.getResponse().getHeaders().add("X-Error", "Missing or invalid X-API-KEY");
                return exchange.getResponse().setComplete();
            }
        }

        return chain.filter(exchange);
    }

    private boolean requiresApiKey(String path) {
        return path.startsWith("/api/orders")
                || path.startsWith("/api/accounts")
                || path.startsWith("/api/wallet") 
                || path.startsWith("/api/market-data/stream");
    }
}
