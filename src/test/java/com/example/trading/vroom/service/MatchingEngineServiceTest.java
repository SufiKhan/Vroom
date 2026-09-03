package com.trading.vroom.service;

import com.trading.vroom.domain.MarketTick;
import com.trading.vroom.domain.OrderSide;
import com.trading.vroom.domain.UserAccount;
import com.trading.vroom.infrastructure.InMemoryAccountRepository;
import com.trading.vroom.infrastructure.InMemoryOrderBookRepository;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MatchingEngineServiceTest {

    @Test
    void executesRestingBuyOrderWhenTickCrossesLimitPrice() {
        InMemoryOrderBookRepository orderBookRepository = new InMemoryOrderBookRepository();
        InMemoryAccountRepository accountRepository = new InMemoryAccountRepository();
        MatchingEngineService matchingEngineService = new MatchingEngineService(orderBookRepository, accountRepository);

        matchingEngineService.submitLimitOrder(
                "user_1",
                "VROOM",
                OrderSide.BUY,
                BigDecimal.ONE,
                BigDecimal.valueOf(151.00)
        );

        MarketTick crossingTick = new MarketTick(
                "VROOM",
                BigDecimal.valueOf(150.50),
                BigDecimal.valueOf(20),
                Instant.now()
        );

        matchingEngineService.onMarketTick(crossingTick);

        UserAccount account = matchingEngineService.getOrCreateAccount("user_1");
        assertEquals(0L, matchingEngineService.countOpenOrders("VROOM"));
        assertEquals(BigDecimal.valueOf(99849.50).setScale(2), account.getCashBalance());
        assertEquals(BigDecimal.ONE, account.getPositionsSnapshot().get("VROOM"));
    }
}
