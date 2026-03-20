package com.example.mobile_obs_asm.network.auth;

import com.example.mobile_obs_asm.network.ApiEnvelope;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.PATCH;
import retrofit2.http.POST;

public interface AuthApiService {

    @POST("api/auth/register")
    Call<ApiEnvelope<String>> register(@Body RegisterRequestBody requestBody);

    @POST("api/auth/login")
    Call<ApiEnvelope<RemoteAuthResponse>> login(@Body LoginRequestBody requestBody);

    @PATCH("api/auth/profile")
    Call<ApiEnvelope<RemoteAuthResponse.RemoteUserInfo>> updateProfile(@Body ProfileUpdateRequestBody requestBody);
}
