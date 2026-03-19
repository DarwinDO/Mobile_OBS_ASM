package com.example.mobile_obs_asm.network.payment;

import java.math.BigDecimal;

public class RemotePaymentResponse {

    private String id;
    private BigDecimal amount;
    private String gateway;
    private String method;
    private String phase;
    private String status;
    private String transactionReference;
    private String paymentDate;
    private String createdAt;

    public String getId() {
        return id;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getGateway() {
        return gateway;
    }

    public String getMethod() {
        return method;
    }

    public String getPhase() {
        return phase;
    }

    public String getStatus() {
        return status;
    }

    public String getTransactionReference() {
        return transactionReference;
    }

    public String getPaymentDate() {
        return paymentDate;
    }

    public String getCreatedAt() {
        return createdAt;
    }
}
