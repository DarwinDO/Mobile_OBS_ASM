package com.example.mobile_obs_asm.data;

import android.content.Context;

import com.example.mobile_obs_asm.network.ApiEnvelope;
import com.example.mobile_obs_asm.network.RetrofitClient;
import com.example.mobile_obs_asm.network.auth.AuthApiService;
import com.example.mobile_obs_asm.network.auth.LoginRequestBody;
import com.example.mobile_obs_asm.network.auth.RegisterRequestBody;
import com.example.mobile_obs_asm.network.auth.RemoteAuthResponse;
import com.example.mobile_obs_asm.util.ApiErrorMessageExtractor;

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
                    callback.onError(
                            ApiErrorMessageExtractor.extract(response, "Không thể đăng nhập lúc này."),
                            null
                    );
                    return;
                }

                RemoteAuthResponse authResponse = response.body().getResult();
                sessionManager.saveAuthSession(authResponse);
                callback.onSuccess(authResponse);
            }

            @Override
            public void onFailure(Call<ApiEnvelope<RemoteAuthResponse>> call, Throwable throwable) {
                callback.onError(
                        "Không thể kết nối tới máy chủ đăng nhập. Hãy kiểm tra xem backend đã bật và địa chỉ máy chủ đã được cấu hình đúng hay chưa.",
                        throwable
                );
            }
        });
    }

    public void register(
            String email,
            String password,
            String firstName,
            String lastName,
            String phone,
            String role,
            RepositoryCallback<String> callback
    ) {
        authApiService.register(new RegisterRequestBody(email, password, firstName, lastName, phone, role))
                .enqueue(new Callback<ApiEnvelope<String>>() {
                    @Override
                    public void onResponse(Call<ApiEnvelope<String>> call, Response<ApiEnvelope<String>> response) {
                        if (!response.isSuccessful() || response.body() == null || response.body().getResult() == null) {
                            callback.onError(
                                    ApiErrorMessageExtractor.extract(response, "Không thể tạo tài khoản lúc này."),
                                    null
                            );
                            return;
                        }
                        callback.onSuccess(response.body().getResult());
                    }

                    @Override
                    public void onFailure(Call<ApiEnvelope<String>> call, Throwable throwable) {
                        callback.onError(
                                "Không thể kết nối tới máy chủ đăng ký. Hãy kiểm tra lại kết nối và cấu hình địa chỉ máy chủ.",
                                throwable
                        );
                    }
                });
    }
}
