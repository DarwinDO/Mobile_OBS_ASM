package com.example.mobile_obs_asm.network.order;

import java.math.BigDecimal;

public class RemoteOrderResponse {

    private String id;
    private String productId;
    private String productTitle;
    private String buyerId;
    private String buyerName;
    private String sellerId;
    private String sellerName;
    private BigDecimal totalAmount;
    private BigDecimal depositAmount;
    private BigDecimal requiredUpfrontAmount;
    private BigDecimal paidAmount;
    private BigDecimal remainingAmount;
    private BigDecimal serviceFee;
    private String paymentOption;
    private String status;
    private String fundingStatus;
    private String paymentMethod;
    private String acceptedAt;
    private String paymentDeadline;
    private String createdAt;
    private String updatedAt;

    public String getId() {
        return id;
    }

    public String getProductId() {
        return productId;
    }

    public String getProductTitle() {
        return productTitle;
    }

    public String getBuyerId() {
        return buyerId;
    }

    public String getBuyerName() {
        return buyerName;
    }

    public String getSellerId() {
        return sellerId;
    }

    public String getSellerName() {
        return sellerName;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public BigDecimal getDepositAmount() {
        return depositAmount;
    }

    public BigDecimal getRequiredUpfrontAmount() {
        return requiredUpfrontAmount;
    }

    public BigDecimal getPaidAmount() {
        return paidAmount;
    }

    public BigDecimal getRemainingAmount() {
        return remainingAmount;
    }

    public BigDecimal getServiceFee() {
        return serviceFee;
    }

    public String getPaymentOption() {
        return paymentOption;
    }

    public String getStatus() {
        return status;
    }

    public String getFundingStatus() {
        return fundingStatus;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public String getAcceptedAt() {
        return acceptedAt;
    }

    public String getPaymentDeadline() {
        return paymentDeadline;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }
}
