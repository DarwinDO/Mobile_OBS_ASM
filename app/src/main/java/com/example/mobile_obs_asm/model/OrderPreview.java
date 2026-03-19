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
                false
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
