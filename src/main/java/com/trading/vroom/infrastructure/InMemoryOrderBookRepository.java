package com.trading.vroom.infrastructure;

import com.trading.vroom.domain.LimitOrder;
import com.trading.vroom.domain.OrderSide;
import com.trading.vroom.ports.OrderBookRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Repository
public class InMemoryOrderBookRepository implements OrderBookRepository {

    private final CopyOnWriteArrayList<LimitOrder> openOrders = new CopyOnWriteArrayList<>();

    @Override
    public void add(LimitOrder order) {
        openOrders.add(order);
    }

    @Override
    public List<LimitOrder> findExecutable(String symbol, BigDecimal marketPrice) {
        return openOrders.stream()
                .filter(order -> order.symbol().equalsIgnoreCase(symbol))
                .filter(order -> isExecutable(order, marketPrice))
                .sorted(Comparator.comparing(LimitOrder::createdAt))
                .toList();
    }

    @Override
    public boolean remove(String orderId) {
        return openOrders.removeIf(order -> order.id().equals(orderId));
    }

    @Override
    public long countOpenOrders(String symbol) {
        return openOrders.stream()
                .filter(order -> order.symbol().equalsIgnoreCase(symbol))
                .count();
    }

    private boolean isExecutable(LimitOrder order, BigDecimal marketPrice) {
        if (order.side() == OrderSide.BUY) {
            return marketPrice.compareTo(order.limitPrice()) <= 0;
        }
        return marketPrice.compareTo(order.limitPrice()) >= 0;
    }
}
