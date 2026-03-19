package com.example.mobile_obs_asm.model;

import android.net.Uri;

import java.util.ArrayList;
import java.util.List;

public class CreateListingDraft {

    private final String title;
    private final String description;
    private final long price;
    private final String brakeTypeId;
    private final String frameMaterialId;
    private final String frameSize;
    private final String wheelSize;
    private final String groupset;
    private final String condition;
    private final String province;
    private final String district;
    private final List<Uri> imageUris;

    public CreateListingDraft(
            String title,
            String description,
            long price,
            String brakeTypeId,
            String frameMaterialId,
            String frameSize,
            String wheelSize,
            String groupset,
            String condition,
            String province,
            String district,
            List<Uri> imageUris
    ) {
        this.title = title;
        this.description = description;
        this.price = price;
        this.brakeTypeId = brakeTypeId;
        this.frameMaterialId = frameMaterialId;
        this.frameSize = frameSize;
        this.wheelSize = wheelSize;
        this.groupset = groupset;
        this.condition = condition;
        this.province = province;
        this.district = district;
        this.imageUris = new ArrayList<>(imageUris);
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public long getPrice() {
        return price;
    }

    public String getBrakeTypeId() {
        return brakeTypeId;
    }

    public String getFrameMaterialId() {
        return frameMaterialId;
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

    public String getCondition() {
        return condition;
    }

    public String getProvince() {
        return province;
    }

    public String getDistrict() {
        return district;
    }

    public List<Uri> getImageUris() {
        return new ArrayList<>(imageUris);
    }
}
