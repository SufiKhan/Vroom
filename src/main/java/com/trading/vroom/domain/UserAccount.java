package com.example.trading.vroom.domain;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class UserAccount {
    private final String userId;
    private BigDecimal cashBalance;
    private final Map<String, BigDecimal> positions;

    public UserAccount(String userId, BigDecimal cashBalance) {
        this.userId = userId;
        this.cashBalance = cashBalance.setScale(2, RoundingMode.HALF_UP);
        this.positions = new ConcurrentHashMap<>();
    }

    public String getUserId() {
        return userId;
    }

    public synchronized BigDecimal getCashBalance() {
        return cashBalance;
    }

    public synchronized Map<String, BigDecimal> getPositionsSnapshot() {
        return Map.copyOf(positions);
    }

    public synchronized boolean applyBuy(String symbol, BigDecimal quantity, BigDecimal executionPrice) {
        BigDecimal notional = quantity.multiply(executionPrice).setScale(2, RoundingMode.HALF_UP);
        if (cashBalance.compareTo(notional) < 0) {
            return false;
        }

        cashBalance = cashBalance.subtract(notional).setScale(2, RoundingMode.HALF_UP);
        positions.merge(symbol, quantity, BigDecimal::add);
        return true;
    }

    public synchronized boolean applySell(String symbol, BigDecimal quantity, BigDecimal executionPrice) {
        BigDecimal currentPosition = positions.getOrDefault(symbol, BigDecimal.ZERO);
        if (currentPosition.compareTo(quantity) < 0) {
            return false;
        }

        BigDecimal notional = quantity.multiply(executionPrice).setScale(2, RoundingMode.HALF_UP);
        positions.put(symbol, currentPosition.subtract(quantity));
        cashBalance = cashBalance.add(notional).setScale(2, RoundingMode.HALF_UP);
        return true;
    }

    public synchronized void addPosition(String symbol, BigDecimal quantity) {
        positions.merge(symbol, quantity, BigDecimal::add);
    }
}
