package com.example.mobile_obs_asm.network.order;

import java.math.BigDecimal;

public class OrderCreateRequestBody {

    private final String productId;
    private final BigDecimal upfrontAmount;
    private final BigDecimal depositAmount;
    private final BigDecimal serviceFee;
    private final String paymentOption;
    private final String paymentMethod;

    public OrderCreateRequestBody(
            String productId,
            BigDecimal upfrontAmount,
            BigDecimal depositAmount,
            BigDecimal serviceFee,
            String paymentOption,
            String paymentMethod
    ) {
        this.productId = productId;
        this.upfrontAmount = upfrontAmount;
        this.depositAmount = depositAmount;
        this.serviceFee = serviceFee;
        this.paymentOption = paymentOption;
        this.paymentMethod = paymentMethod;
    }

    public String getProductId() {
        return productId;
    }

    public BigDecimal getUpfrontAmount() {
        return upfrontAmount;
    }

    public BigDecimal getDepositAmount() {
        return depositAmount;
    }

    public BigDecimal getServiceFee() {
        return serviceFee;
    }

    public String getPaymentOption() {
        return paymentOption;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }
}
