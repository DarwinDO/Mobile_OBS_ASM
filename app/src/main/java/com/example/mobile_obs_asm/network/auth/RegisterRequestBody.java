package com.example.mobile_obs_asm.network.auth;

public class RegisterRequestBody {

    private final String email;
    private final String password;
    private final String firstName;
    private final String lastName;
    private final String phone;
    private final String role;

    public RegisterRequestBody(String email, String password, String firstName, String lastName, String phone, String role) {
        this.email = email;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phone = phone;
        this.role = role;
    }
}
