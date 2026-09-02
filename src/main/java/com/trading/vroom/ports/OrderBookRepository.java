package com.example.trading.vroom.ports;

import com.example.trading.vroom.domain.LimitOrder;

import java.math.BigDecimal;
import java.util.List;

public interface OrderBookRepository {
    void add(LimitOrder order);

    List<LimitOrder> findExecutable(String symbol, BigDecimal marketPrice);

    boolean remove(String orderId);

    long countOpenOrders(String symbol);
}
