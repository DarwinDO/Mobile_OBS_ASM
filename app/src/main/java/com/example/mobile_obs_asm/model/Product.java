package com.example.mobile_obs_asm.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.ColorRes;

public class Product implements Parcelable {

    private final String id;
    private final String title;
    private final String tagline;
    private final String coverLabel;
    private final String location;
    private final String condition;
    private final String badge;
    private final String description;
    private final String frameSize;
    private final String wheelSize;
    private final String groupset;
    private final long price;
    private final int heroColorRes;
    private final int coverColorRes;
    private final boolean remoteSource;

    public Product(
            String id,
            String title,
            String tagline,
            String coverLabel,
            String location,
            String condition,
            String badge,
            String description,
            String frameSize,
            String wheelSize,
            String groupset,
            long price,
            @ColorRes int heroColorRes,
            @ColorRes int coverColorRes,
            boolean remoteSource
    ) {
        this.id = id;
        this.title = title;
        this.tagline = tagline;
        this.coverLabel = coverLabel;
        this.location = location;
        this.condition = condition;
        this.badge = badge;
        this.description = description;
        this.frameSize = frameSize;
        this.wheelSize = wheelSize;
        this.groupset = groupset;
        this.price = price;
        this.heroColorRes = heroColorRes;
        this.coverColorRes = coverColorRes;
        this.remoteSource = remoteSource;
    }

    protected Product(Parcel in) {
        id = in.readString();
        title = in.readString();
        tagline = in.readString();
        coverLabel = in.readString();
        location = in.readString();
        condition = in.readString();
        badge = in.readString();
        description = in.readString();
        frameSize = in.readString();
        wheelSize = in.readString();
        groupset = in.readString();
        price = in.readLong();
        heroColorRes = in.readInt();
        coverColorRes = in.readInt();
        remoteSource = in.readByte() != 0;
    }

    public static final Creator<Product> CREATOR = new Creator<Product>() {
        @Override
        public Product createFromParcel(Parcel in) {
            return new Product(in);
        }

        @Override
        public Product[] newArray(int size) {
            return new Product[size];
        }
    };

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getTagline() {
        return tagline;
    }

    public String getCoverLabel() {
        return coverLabel;
    }

    public String getLocation() {
        return location;
    }

    public String getCondition() {
        return condition;
    }

    public String getBadge() {
        return badge;
    }

    public String getDescription() {
        return description;
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

    public long getPrice() {
        return price;
    }

    public int getHeroColorRes() {
        return heroColorRes;
    }

    public int getCoverColorRes() {
        return coverColorRes;
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
        dest.writeString(tagline);
        dest.writeString(coverLabel);
        dest.writeString(location);
        dest.writeString(condition);
        dest.writeString(badge);
        dest.writeString(description);
        dest.writeString(frameSize);
        dest.writeString(wheelSize);
        dest.writeString(groupset);
        dest.writeLong(price);
        dest.writeInt(heroColorRes);
        dest.writeInt(coverColorRes);
        dest.writeByte((byte) (remoteSource ? 1 : 0));
    }
}
