package com.example.mobile_obs_asm.network;

import android.content.Context;

import com.example.mobile_obs_asm.R;
import com.example.mobile_obs_asm.data.SessionManager;
import com.example.mobile_obs_asm.util.SessionExpiryNotifier;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class AuthHeaderInterceptor implements Interceptor {

    private final Context appContext;
    private final SessionManager sessionManager;

    public AuthHeaderInterceptor(Context context) {
        appContext = context.getApplicationContext();
        sessionManager = SessionManager.getInstance(appContext);
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
        Response response = chain.proceed(authenticatedRequest);

        if ((response.code() == 401 || response.code() == 403) && sessionManager.hasActiveSession()) {
            sessionManager.clearSession();
            SessionExpiryNotifier.notifyExpired(
                    appContext,
                    appContext.getString(R.string.session_expired_message)
            );
        }

        return response;
    }
}
