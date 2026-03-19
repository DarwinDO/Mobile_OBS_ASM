package com.example.mobile_obs_asm.model;

public class PaymentRequestInfo {

    private final String checkoutUrl;
    private final String qrCodeUrl;
    private final String transferContent;
    private final String bankAccountNumber;
    private final String bankAccountName;
    private final String bankBin;
    private final String instructions;
    private final String expiresAtLabel;
    private final String amountLabel;
    private final boolean mockMode;

    public PaymentRequestInfo(
            String checkoutUrl,
            String qrCodeUrl,
            String transferContent,
            String bankAccountNumber,
            String bankAccountName,
            String bankBin,
            String instructions,
            String expiresAtLabel,
            String amountLabel,
            boolean mockMode
    ) {
        this.checkoutUrl = checkoutUrl;
        this.qrCodeUrl = qrCodeUrl;
        this.transferContent = transferContent;
        this.bankAccountNumber = bankAccountNumber;
        this.bankAccountName = bankAccountName;
        this.bankBin = bankBin;
        this.instructions = instructions;
        this.expiresAtLabel = expiresAtLabel;
        this.amountLabel = amountLabel;
        this.mockMode = mockMode;
    }

    public String getCheckoutUrl() {
        return checkoutUrl;
    }

    public String getQrCodeUrl() {
        return qrCodeUrl;
    }

    public String getTransferContent() {
        return transferContent;
    }

    public String getBankAccountNumber() {
        return bankAccountNumber;
    }

    public String getBankAccountName() {
        return bankAccountName;
    }

    public String getBankBin() {
        return bankBin;
    }

    public String getInstructions() {
        return instructions;
    }

    public String getExpiresAtLabel() {
        return expiresAtLabel;
    }

    public String getAmountLabel() {
        return amountLabel;
    }

    public boolean isMockMode() {
        return mockMode;
    }
}
