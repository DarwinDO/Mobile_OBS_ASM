package com.example.mobile_obs_asm.network.auth;

import com.google.gson.annotations.SerializedName;

public class RemoteAuthResponse {

    private String accessToken;
    private String refreshToken;
    private String tokenType;
    private Long expiresIn;
    private RemoteUserInfo user;

    public String getAccessToken() {
        return accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public String getTokenType() {
        return tokenType;
    }

    public Long getExpiresIn() {
        return expiresIn;
    }

    public RemoteUserInfo getUser() {
        return user;
    }

    public static class RemoteUserInfo {
        private String id;
        private String email;
        private String firstName;
        private String lastName;
        private String phone;
        private String avatarUrl;
        private String defaultAddress;
        private String role;
        private String status;
        @SerializedName(value = "verified", alternate = {"isVerified"})
        private boolean verified;

        public String getId() {
            return id;
        }

        public String getEmail() {
            return email;
        }

        public String getFirstName() {
            return firstName;
        }

        public String getLastName() {
            return lastName;
        }

        public String getPhone() {
            return phone;
        }

        public String getAvatarUrl() {
            return avatarUrl;
        }

        public String getDefaultAddress() {
            return defaultAddress;
        }

        public String getRole() {
            return role;
        }

        public String getStatus() {
            return status;
        }

        public boolean isVerified() {
            return verified;
        }
    }
}
