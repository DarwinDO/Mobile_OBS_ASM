package com.example.mobile_obs_asm.network.auth;

import com.example.mobile_obs_asm.network.ApiEnvelope;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface AuthApiService {

    @POST("api/auth/login")
    Call<ApiEnvelope<RemoteAuthResponse>> login(@Body LoginRequestBody requestBody);
}
