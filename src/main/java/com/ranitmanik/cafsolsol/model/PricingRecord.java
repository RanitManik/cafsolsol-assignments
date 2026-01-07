package com.ranitmanik.cafsolsol.model;

import java.time.LocalTime;

public class PricingRecord {
    private String skuId;
    private LocalTime startTime;
    private LocalTime endTime;
    private double price;

    public PricingRecord(String skuId, LocalTime startTime, LocalTime endTime, double price) {
        this.skuId = skuId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.price = price;
    }

    public String getSkuId() {
        return skuId;
    }

    public void setSkuId(String skuId) {
        this.skuId = skuId;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public boolean isTimeInRange(LocalTime time) {
        return !time.isBefore(startTime) && time.isBefore(endTime);
    }
}
