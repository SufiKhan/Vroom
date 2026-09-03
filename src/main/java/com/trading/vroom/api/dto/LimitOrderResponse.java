package com.trading.vroom.api.dto;

public record LimitOrderResponse(
        String orderId,
        String status,
        String symbol,
        String side
) {
}
