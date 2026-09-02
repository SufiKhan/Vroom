package com.example.trading.vroom.config;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "market.simulator")
public class MarketSimulationProperties {

    @NotBlank
    private String symbol = "VROOM";

    @DecimalMin("0.01")
    private double initialPrice = 152.25;

    private double minPercentChange = -1.5;

    private double maxPercentChange = 1.5;

    @Positive
    private int minQuantity = 1;

    @Positive
    private int maxQuantity = 200;

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public double getInitialPrice() {
        return initialPrice;
    }

    public void setInitialPrice(double initialPrice) {
        this.initialPrice = initialPrice;
    }

    public double getMinPercentChange() {
        return minPercentChange;
    }

    public void setMinPercentChange(double minPercentChange) {
        this.minPercentChange = minPercentChange;
    }

    public double getMaxPercentChange() {
        return maxPercentChange;
    }

    public void setMaxPercentChange(double maxPercentChange) {
        this.maxPercentChange = maxPercentChange;
    }

    public int getMinQuantity() {
        return minQuantity;
    }

    public void setMinQuantity(int minQuantity) {
        this.minQuantity = minQuantity;
    }

    public int getMaxQuantity() {
        return maxQuantity;
    }

    public void setMaxQuantity(int maxQuantity) {
        this.maxQuantity = maxQuantity;
    }
}
