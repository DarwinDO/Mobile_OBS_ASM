package com.example.mobile_obs_asm.network.product;

import com.google.gson.annotations.SerializedName;

import java.math.BigDecimal;
import java.util.List;

public class RemoteProductResponse {

    private String id;
    private String title;
    private String description;
    private BigDecimal price;
    private String condition;
    private String status;
    private String province;
    private String district;
    private String frameSize;
    private String wheelSize;
    private String groupset;
    private String brandName;
    @SerializedName(value = "verified", alternate = {"isVerified"})
    private boolean verified;
    private List<ImageInfo> images;
    private SellerInfo seller;

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public String getCondition() {
        return condition;
    }

    public String getStatus() {
        return status;
    }

    public String getProvince() {
        return province;
    }

    public String getDistrict() {
        return district;
    }

    public String getFrameSize() {
        return frameSize;
    }

    public String getWheelSize() {
        return wheelSize;
    }

    public String getGroupset() {
        return groupset;
    }

    public String getBrandName() {
        return brandName;
    }

    public boolean isVerified() {
        return verified;
    }

    public List<ImageInfo> getImages() {
        return images;
    }

    public String getSellerId() {
        return seller == null ? null : seller.id;
    }

    public static class SellerInfo {
        private String id;
    }

    public static class ImageInfo {
        private String id;
        private String url;
        @SerializedName(value = "primary", alternate = {"isPrimary"})
        private boolean primary;

        public String getId() {
            return id;
        }

        public String getUrl() {
            return url;
        }

        public boolean isPrimary() {
            return primary;
        }
    }
}
