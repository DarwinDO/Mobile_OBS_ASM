package com.example.mobile_obs_asm.model;

import androidx.annotation.ColorRes;

public class OrderPreview {

    private final String id;
    private final String title;
    private final String timeline;
    private final long amount;
    private final String status;
    private final int statusColorRes;

    public OrderPreview(String id, String title, String timeline, long amount, String status, @ColorRes int statusColorRes) {
        this.id = id;
        this.title = title;
        this.timeline = timeline;
        this.amount = amount;
        this.status = status;
        this.statusColorRes = statusColorRes;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getTimeline() {
        return timeline;
    }

    public long getAmount() {
        return amount;
    }

    public String getStatus() {
        return status;
    }

    public int getStatusColorRes() {
        return statusColorRes;
    }
}
