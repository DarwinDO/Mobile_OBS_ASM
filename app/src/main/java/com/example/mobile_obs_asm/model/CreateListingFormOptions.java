package com.example.mobile_obs_asm.model;

import java.util.List;

public class CreateListingFormOptions {

    private final List<ReferenceOption> brakeTypes;
    private final List<ReferenceOption> frameMaterials;

    public CreateListingFormOptions(List<ReferenceOption> brakeTypes, List<ReferenceOption> frameMaterials) {
        this.brakeTypes = brakeTypes;
        this.frameMaterials = frameMaterials;
    }

    public List<ReferenceOption> getBrakeTypes() {
        return brakeTypes;
    }

    public List<ReferenceOption> getFrameMaterials() {
        return frameMaterials;
    }
}
