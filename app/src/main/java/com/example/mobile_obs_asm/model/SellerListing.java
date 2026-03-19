package com.example.mobile_obs_asm.model;

import androidx.annotation.ColorRes;

public class SellerListing {

    private final String id;
    private final String title;
    private final String summary;
    private final String meta;
    private final long price;
    private final String status;
    private final String statusLabel;
    private final String coverLabel;
    private final @ColorRes int coverColorRes;

    public SellerListing(
            String id,
            String title,
            String summary,
            String meta,
            long price,
            String status,
            String statusLabel,
            String coverLabel,
            @ColorRes int coverColorRes
    ) {
        this.id = id;
        this.title = title;
        this.summary = summary;
        this.meta = meta;
        this.price = price;
        this.status = status;
        this.statusLabel = statusLabel;
        this.coverLabel = coverLabel;
        this.coverColorRes = coverColorRes;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getSummary() {
        return summary;
    }

    public String getMeta() {
        return meta;
    }

    public long getPrice() {
        return price;
    }

    public String getStatus() {
        return status;
    }

    public String getStatusLabel() {
        return statusLabel;
    }

    public String getCoverLabel() {
        return coverLabel;
    }

    public int getCoverColorRes() {
        return coverColorRes;
    }

    public boolean isHidden() {
        return "hidden".equalsIgnoreCase(status);
    }

    public boolean isSold() {
        return "sold".equalsIgnoreCase(status);
    }
}
