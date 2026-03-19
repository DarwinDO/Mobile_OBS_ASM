package com.example.mobile_obs_asm.network.payment;

import java.math.BigDecimal;

public class RemotePaymentRequestResponse {

    private String checkoutUrl;
    private String qrCodeUrl;
    private String transferContent;
    private String bankBin;
    private String bankAccountNumber;
    private String bankAccountName;
    private boolean mockMode;
    private String instructions;
    private String expiresAt;
    private BigDecimal amount;

    public String getCheckoutUrl() {
        return checkoutUrl;
    }

    public String getQrCodeUrl() {
        return qrCodeUrl;
    }

    public String getTransferContent() {
        return transferContent;
    }

    public String getBankBin() {
        return bankBin;
    }

    public String getBankAccountNumber() {
        return bankAccountNumber;
    }

    public String getBankAccountName() {
        return bankAccountName;
    }

    public boolean isMockMode() {
        return mockMode;
    }

    public String getInstructions() {
        return instructions;
    }

    public String getExpiresAt() {
        return expiresAt;
    }

    public BigDecimal getAmount() {
        return amount;
    }
}
