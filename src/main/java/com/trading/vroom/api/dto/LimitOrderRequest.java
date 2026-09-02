package com.example.trading.vroom.api.dto;

import com.example.trading.vroom.domain.OrderSide;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record LimitOrderRequest(
        @NotBlank String userId,
        @NotBlank String symbol,
        @NotNull OrderSide side,
        @NotNull @DecimalMin("0.0001") BigDecimal quantity,
        @NotNull @DecimalMin("0.01") BigDecimal limitPrice
) {
}
