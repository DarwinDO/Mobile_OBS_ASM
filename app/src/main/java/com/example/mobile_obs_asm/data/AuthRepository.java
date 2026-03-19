package com.example.mobile_obs_asm.data;

import android.content.Context;

import com.example.mobile_obs_asm.network.ApiEnvelope;
import com.example.mobile_obs_asm.network.RetrofitClient;
import com.example.mobile_obs_asm.network.auth.AuthApiService;
import com.example.mobile_obs_asm.network.auth.LoginRequestBody;
import com.example.mobile_obs_asm.network.auth.RemoteAuthResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AuthRepository {

    private final AuthApiService authApiService;
    private final SessionManager sessionManager;

    public AuthRepository(Context context) {
        authApiService = RetrofitClient.createAuthApiService(context);
        sessionManager = SessionManager.getInstance(context);
    }

    public void login(String email, String password, RepositoryCallback<RemoteAuthResponse> callback) {
        authApiService.login(new LoginRequestBody(email, password)).enqueue(new Callback<ApiEnvelope<RemoteAuthResponse>>() {
            @Override
            public void onResponse(Call<ApiEnvelope<RemoteAuthResponse>> call, Response<ApiEnvelope<RemoteAuthResponse>> response) {
                if (!response.isSuccessful() || response.body() == null || response.body().getResult() == null) {
                    callback.onError("Login request was not accepted by the backend.", null);
                    return;
                }

                RemoteAuthResponse authResponse = response.body().getResult();
                sessionManager.saveAuthSession(authResponse);
                callback.onSuccess(authResponse);
            }

            @Override
            public void onFailure(Call<ApiEnvelope<RemoteAuthResponse>> call, Throwable throwable) {
                callback.onError("Could not reach backend login endpoint.", throwable);
            }
        });
    }
}
