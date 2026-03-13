package com.example.a4diapersa.domain;

import java.time.Month;

public class MonthlyProfit {
    private final Month month;
    private final int ordersCount;
    private final double totalPrice;

    public MonthlyProfit(Month month, int ordersCount, double totalPrice) {
        this.month = month;
        this.ordersCount = ordersCount;
        this.totalPrice = totalPrice;
    }

    public Month getMonth() {
        return month;
    }

    public int getOrdersCount() {
        return ordersCount;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    @Override
    public String toString() {
        return month + " | Orders: " + ordersCount + " | Total Price: " + totalPrice;
    }
}
