package com.example.mobile_obs_asm.network;

public class ApiEnvelope<T> {

    private int code;
    private String message;
    private T result;

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public T getResult() {
        return result;
    }
}
