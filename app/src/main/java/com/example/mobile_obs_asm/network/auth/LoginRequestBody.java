package com.example.mobile_obs_asm.network.auth;

public class LoginRequestBody {

    private final String email;
    private final String password;

    public LoginRequestBody(String email, String password) {
        this.email = email;
        this.password = password;
    }
}
