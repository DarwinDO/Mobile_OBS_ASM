package com.example.mobile_obs_asm.network;

import android.content.Context;

import com.example.mobile_obs_asm.data.SessionManager;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class AuthHeaderInterceptor implements Interceptor {

    private final SessionManager sessionManager;

    public AuthHeaderInterceptor(Context context) {
        sessionManager = SessionManager.getInstance(context);
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request original = chain.request();
        String accessToken = sessionManager.getAccessToken();

        if (accessToken == null || accessToken.isEmpty() || original.header("Authorization") != null) {
            return chain.proceed(original);
        }

        Request authenticatedRequest = original.newBuilder()
                .header("Authorization", "Bearer " + accessToken)
                .build();
        return chain.proceed(authenticatedRequest);
    }
}
