package com.example.mobile_obs_asm.model;

public class ReferenceOption {

    private final String id;
    private final String label;

    public ReferenceOption(String id, String label) {
        this.id = id;
        this.label = label;
    }

    public String getId() {
        return id;
    }

    public String getLabel() {
        return label;
    }

    @Override
    public String toString() {
        return label;
    }
}
