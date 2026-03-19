package com.example.mobile_obs_asm.data;

public interface RepositoryCallback<T> {
    void onSuccess(T value);

    void onError(String message, Throwable throwable);
}
