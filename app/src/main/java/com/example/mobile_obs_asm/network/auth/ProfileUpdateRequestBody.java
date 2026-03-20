package com.example.mobile_obs_asm.network.auth;

public class ProfileUpdateRequestBody {

    private final String firstName;
    private final String lastName;
    private final String phone;
    private final String avatarUrl;
    private final String defaultAddress;

    public ProfileUpdateRequestBody(
            String firstName,
            String lastName,
            String phone,
            String avatarUrl,
            String defaultAddress
    ) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.phone = phone;
        this.avatarUrl = avatarUrl;
        this.defaultAddress = defaultAddress;
    }
}
