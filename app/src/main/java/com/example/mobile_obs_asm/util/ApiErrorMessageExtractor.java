package com.example.mobile_obs_asm.util;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Response;

public final class ApiErrorMessageExtractor {

    private ApiErrorMessageExtractor() {
    }

    public static String extract(Response<?> response, String fallbackMessage) {
        if (response == null) {
            return fallbackMessage;
        }

        ResponseBody errorBody = response.errorBody();
        if (errorBody == null) {
            return fallbackMessage;
        }

        try {
            String rawBody = errorBody.string();
            if (rawBody == null || rawBody.isEmpty()) {
                return fallbackMessage;
            }

            JSONObject jsonObject = new JSONObject(rawBody);
            String message = jsonObject.optString("message", "");
            return message.isEmpty() ? fallbackMessage : message;
        } catch (IOException | JSONException exception) {
            return fallbackMessage;
        }
    }
}
