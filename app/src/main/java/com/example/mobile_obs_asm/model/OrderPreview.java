package com.example.mobile_obs_asm.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.ColorRes;

public class OrderPreview implements Parcelable {

    private final String id;
    private final String title;
    private final String timeline;
    private final long amount;
    private final String status;
    private final int statusColorRes;
    private final String fundingStatus;
    private final String paymentMethod;
    private final String createdAtLabel;
    private final String deadlineLabel;
    private final String partiesLabel;
    private final String summaryNote;
    private final boolean remoteSource;
    private final String rawStatus;
    private final String rawFundingStatus;
    private final String rawPaymentMethod;
    private final String rawPaymentOption;
    private final long totalAmount;
    private final long requiredUpfrontAmount;
    private final long remainingAmount;

    public OrderPreview(String id, String title, String timeline, long amount, String status, @ColorRes int statusColorRes) {
        this(
                id,
                title,
                timeline,
                amount,
                status,
                statusColorRes,
                "Đang cập nhật",
                "Chưa cập nhật",
                "Đang cập nhật",
                "Đang cập nhật",
                "Thông tin người mua và người bán",
                "Đây là dữ liệu tham khảo để mô phỏng quy trình đơn mua trên ứng dụng di động.",
                false,
                "",
                "",
                "",
                "",
                amount,
                amount,
                amount
        );
    }

    public OrderPreview(
            String id,
            String title,
            String timeline,
            long amount,
            String status,
            @ColorRes int statusColorRes,
            String fundingStatus,
            String paymentMethod,
            String createdAtLabel,
            String deadlineLabel,
            String partiesLabel,
            String summaryNote,
            boolean remoteSource
    ) {
        this(
                id,
                title,
                timeline,
                amount,
                status,
                statusColorRes,
                fundingStatus,
                paymentMethod,
                createdAtLabel,
                deadlineLabel,
                partiesLabel,
                summaryNote,
                remoteSource,
                "",
                "",
                "",
                "",
                amount,
                amount,
                amount
        );
    }

    public OrderPreview(
            String id,
            String title,
            String timeline,
            long amount,
            String status,
            @ColorRes int statusColorRes,
            String fundingStatus,
            String paymentMethod,
            String createdAtLabel,
            String deadlineLabel,
            String partiesLabel,
            String summaryNote,
            boolean remoteSource,
            String rawStatus,
            String rawFundingStatus,
            String rawPaymentMethod,
            String rawPaymentOption,
            long totalAmount,
            long requiredUpfrontAmount,
            long remainingAmount
    ) {
        this.id = id;
        this.title = title;
        this.timeline = timeline;
        this.amount = amount;
        this.status = status;
        this.statusColorRes = statusColorRes;
        this.fundingStatus = fundingStatus;
        this.paymentMethod = paymentMethod;
        this.createdAtLabel = createdAtLabel;
        this.deadlineLabel = deadlineLabel;
        this.partiesLabel = partiesLabel;
        this.summaryNote = summaryNote;
        this.remoteSource = remoteSource;
        this.rawStatus = rawStatus;
        this.rawFundingStatus = rawFundingStatus;
        this.rawPaymentMethod = rawPaymentMethod;
        this.rawPaymentOption = rawPaymentOption;
        this.totalAmount = totalAmount;
        this.requiredUpfrontAmount = requiredUpfrontAmount;
        this.remainingAmount = remainingAmount;
    }

    protected OrderPreview(Parcel in) {
        id = in.readString();
        title = in.readString();
        timeline = in.readString();
        amount = in.readLong();
        status = in.readString();
        statusColorRes = in.readInt();
        fundingStatus = in.readString();
        paymentMethod = in.readString();
        createdAtLabel = in.readString();
        deadlineLabel = in.readString();
        partiesLabel = in.readString();
        summaryNote = in.readString();
        remoteSource = in.readByte() != 0;
        rawStatus = in.readString();
        rawFundingStatus = in.readString();
        rawPaymentMethod = in.readString();
        rawPaymentOption = in.readString();
        totalAmount = in.readLong();
        requiredUpfrontAmount = in.readLong();
        remainingAmount = in.readLong();
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

    public String getFundingStatus() {
        return fundingStatus;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public String getCreatedAtLabel() {
        return createdAtLabel;
    }

    public String getDeadlineLabel() {
        return deadlineLabel;
    }

    public String getPartiesLabel() {
        return partiesLabel;
    }

    public String getSummaryNote() {
        return summaryNote;
    }

    public boolean isRemoteSource() {
        return remoteSource;
    }

    public String getRawStatus() {
        return rawStatus;
    }

    public String getRawFundingStatus() {
        return rawFundingStatus;
    }

    public String getRawPaymentMethod() {
        return rawPaymentMethod;
    }

    public String getRawPaymentOption() {
        return rawPaymentOption;
    }

    public long getTotalAmount() {
        return totalAmount;
    }

    public long getRequiredUpfrontAmount() {
        return requiredUpfrontAmount;
    }

    public long getRemainingAmount() {
        return remainingAmount;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(title);
        dest.writeString(timeline);
        dest.writeLong(amount);
        dest.writeString(status);
        dest.writeInt(statusColorRes);
        dest.writeString(fundingStatus);
        dest.writeString(paymentMethod);
        dest.writeString(createdAtLabel);
        dest.writeString(deadlineLabel);
        dest.writeString(partiesLabel);
        dest.writeString(summaryNote);
        dest.writeByte((byte) (remoteSource ? 1 : 0));
        dest.writeString(rawStatus);
        dest.writeString(rawFundingStatus);
        dest.writeString(rawPaymentMethod);
        dest.writeString(rawPaymentOption);
        dest.writeLong(totalAmount);
        dest.writeLong(requiredUpfrontAmount);
        dest.writeLong(remainingAmount);
    }

    public static final Creator<OrderPreview> CREATOR = new Creator<OrderPreview>() {
        @Override
        public OrderPreview createFromParcel(Parcel in) {
            return new OrderPreview(in);
        }

        @Override
        public OrderPreview[] newArray(int size) {
            return new OrderPreview[size];
        }
    };
}
