package com.example.mobile_obs_asm.model;

public class PaymentHistoryItem {

    private final String headline;
    private final String supporting;
    private final String timestamp;

    public PaymentHistoryItem(String headline, String supporting, String timestamp) {
        this.headline = headline;
        this.supporting = supporting;
        this.timestamp = timestamp;
    }

    public String getHeadline() {
        return headline;
    }

    public String getSupporting() {
        return supporting;
    }

    public String getTimestamp() {
        return timestamp;
    }
}
