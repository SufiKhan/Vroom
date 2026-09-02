package com.example.trading.vroom.api;

import com.example.trading.vroom.api.dto.AccountResponse;
import com.example.trading.vroom.api.dto.LimitOrderRequest;
import com.example.trading.vroom.api.dto.LimitOrderResponse;
import com.example.trading.vroom.domain.LimitOrder;
import com.example.trading.vroom.domain.UserAccount;
import com.example.trading.vroom.service.MatchingEngineService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final MatchingEngineService matchingEngineService;

    public OrderController(MatchingEngineService matchingEngineService) {
        this.matchingEngineService = matchingEngineService;
    }

    @PostMapping("/limit")
    public Mono<ResponseEntity<LimitOrderResponse>> submitLimitOrder(@Valid @RequestBody LimitOrderRequest request) {
        return Mono.fromCallable(() -> {
            LimitOrder order = matchingEngineService.submitLimitOrder(
                    request.userId(),
                    request.symbol(),
                    request.side(),
                    request.quantity(),
                    request.limitPrice()
            );

            LimitOrderResponse response = new LimitOrderResponse(
                    order.id(),
                    "ACCEPTED",
                    order.symbol(),
                    order.side().name()
            );

            return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
        });
    }

    @GetMapping("/accounts/{userId}")
    public Mono<AccountResponse> getAccount(@PathVariable String userId) {
        return Mono.fromCallable(() -> {
            UserAccount account = matchingEngineService.getOrCreateAccount(userId);
            return new AccountResponse(account.getUserId(), account.getCashBalance(), account.getPositionsSnapshot());
        });
    }
}
