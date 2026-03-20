package com.example.mobile_obs_asm.model;

public class SellerListingEditorData {

    private final String id;
    private final String title;
    private final String description;
    private final long price;
    private final String province;
    private final String district;
    private final String frameSize;
    private final String wheelSize;
    private final String groupset;
    private final String conditionKey;
    private final String brakeTypeLabel;
    private final String frameMaterialLabel;
    private final int currentImageCount;

    public SellerListingEditorData(
            String id,
            String title,
            String description,
            long price,
            String province,
            String district,
            String frameSize,
            String wheelSize,
            String groupset,
            String conditionKey,
            String brakeTypeLabel,
            String frameMaterialLabel,
            int currentImageCount
    ) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.price = price;
        this.province = province;
        this.district = district;
        this.frameSize = frameSize;
        this.wheelSize = wheelSize;
        this.groupset = groupset;
        this.conditionKey = conditionKey;
        this.brakeTypeLabel = brakeTypeLabel;
        this.frameMaterialLabel = frameMaterialLabel;
        this.currentImageCount = currentImageCount;
    }

    public String getId() {
        return id;
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

    public String getConditionKey() {
        return conditionKey;
    }

    public String getBrakeTypeLabel() {
        return brakeTypeLabel;
    }

    public String getFrameMaterialLabel() {
        return frameMaterialLabel;
    }

    public int getCurrentImageCount() {
        return currentImageCount;
    }
}
