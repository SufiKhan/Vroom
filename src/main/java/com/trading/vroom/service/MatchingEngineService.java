package com.example.trading.vroom.service;

import com.example.trading.vroom.domain.LimitOrder;
import com.example.trading.vroom.domain.MarketTick;
import com.example.trading.vroom.domain.OrderSide;
import com.example.trading.vroom.domain.TradeExecution;
import com.example.trading.vroom.domain.UserAccount;
import com.example.trading.vroom.ports.AccountRepository;
import com.example.trading.vroom.ports.OrderBookRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class MatchingEngineService {

    private static final BigDecimal DEFAULT_CASH_BALANCE = BigDecimal.valueOf(100_000);

    private final OrderBookRepository orderBookRepository;
    private final AccountRepository accountRepository;

    public MatchingEngineService(OrderBookRepository orderBookRepository, AccountRepository accountRepository) {
        this.orderBookRepository = orderBookRepository;
        this.accountRepository = accountRepository;
    }

    public LimitOrder submitLimitOrder(
            String userId,
            String symbol,
            OrderSide side,
            BigDecimal quantity,
            BigDecimal limitPrice
    ) {
        if (quantity.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("quantity must be greater than 0");
        }
        if (limitPrice.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("limitPrice must be greater than 0");
        }

        accountRepository.findOrCreate(userId, DEFAULT_CASH_BALANCE);

        LimitOrder order = new LimitOrder(
                UUID.randomUUID().toString(),
                userId,
                symbol,
                side,
                quantity,
                limitPrice.setScale(2, RoundingMode.HALF_UP),
                Instant.now()
        );
        orderBookRepository.add(order);
        return order;
    }

    public List<TradeExecution> onMarketTick(MarketTick tick) {
        List<LimitOrder> executable = orderBookRepository.findExecutable(tick.symbol(), tick.price());
        List<TradeExecution> executions = new ArrayList<>();

        for (LimitOrder order : executable) {
            UserAccount userAccount = accountRepository.findOrCreate(order.userId(), DEFAULT_CASH_BALANCE);
            boolean executed = execute(order, tick, userAccount);
            if (executed) {
                accountRepository.save(userAccount);
                orderBookRepository.remove(order.id());
                executions.add(new TradeExecution(
                        order.id(),
                        order.userId(),
                        order.symbol(),
                        order.side(),
                        order.quantity(),
                        tick.price(),
                        order.quantity().multiply(tick.price()).setScale(2, RoundingMode.HALF_UP),
                        tick.timestamp()
                ));
            }
        }

        return executions;
    }

    public UserAccount getOrCreateAccount(String userId) {
        return accountRepository.findOrCreate(userId, DEFAULT_CASH_BALANCE);
    }

    public void saveAccount(UserAccount account) {
        accountRepository.save(account);
    }

    public long countOpenOrders(String symbol) {
        return orderBookRepository.countOpenOrders(symbol);
    }

    private boolean execute(LimitOrder order, MarketTick tick, UserAccount account) {
        if (order.side() == OrderSide.BUY) {
            return account.applyBuy(order.symbol(), order.quantity(), tick.price());
        }
        return account.applySell(order.symbol(), order.quantity(), tick.price());
    }
}
