package com.example.mobile_obs_asm.network.order;

import com.example.mobile_obs_asm.network.ApiEnvelope;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface OrderApiService {

    @POST("api/orders")
    Call<ApiEnvelope<RemoteOrderResponse>> createOrder(@Body OrderCreateRequestBody requestBody);

    @GET("api/orders/me")
    Call<ApiEnvelope<List<RemoteOrderResponse>>> getMyOrders();
}
