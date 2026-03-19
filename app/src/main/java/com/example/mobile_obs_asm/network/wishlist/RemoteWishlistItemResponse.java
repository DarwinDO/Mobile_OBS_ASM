package com.example.mobile_obs_asm.network.wishlist;

import java.math.BigDecimal;

public class RemoteWishlistItemResponse {

    private String productId;
    private String title;
    private BigDecimal price;
    private String status;
    private String sellerId;
    private String sellerName;
    private String primaryImageUrl;
    private String addedAt;

    public String getProductId() {
        return productId;
    }

    public String getTitle() {
        return title;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public String getStatus() {
        return status;
    }

    public String getSellerId() {
        return sellerId;
    }

    public String getSellerName() {
        return sellerName;
    }

    public String getPrimaryImageUrl() {
        return primaryImageUrl;
    }

    public String getAddedAt() {
        return addedAt;
    }
}
