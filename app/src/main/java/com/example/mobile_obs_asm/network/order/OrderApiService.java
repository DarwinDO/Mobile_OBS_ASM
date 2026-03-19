package com.example.mobile_obs_asm.network.order;

import com.example.mobile_obs_asm.network.ApiEnvelope;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Body;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface OrderApiService {

    @POST("api/orders")
    Call<ApiEnvelope<RemoteOrderResponse>> createOrder(@Body OrderCreateRequestBody requestBody);

    @GET("api/orders/me")
    Call<ApiEnvelope<List<RemoteOrderResponse>>> getMyOrders();

    @PATCH("api/orders/{orderId}/accept")
    Call<ApiEnvelope<RemoteOrderResponse>> acceptOrder(@Path("orderId") String orderId);

    @PATCH("api/orders/{orderId}/confirm-deposit")
    Call<ApiEnvelope<RemoteOrderResponse>> confirmDeposit(@Path("orderId") String orderId);

    @PATCH("api/orders/{orderId}/complete")
    Call<ApiEnvelope<RemoteOrderResponse>> completeOrder(@Path("orderId") String orderId);

    @PATCH("api/orders/{orderId}/confirm-received")
    Call<ApiEnvelope<RemoteOrderResponse>> confirmReceived(@Path("orderId") String orderId);

    @PATCH("api/orders/{orderId}/cancel")
    Call<ApiEnvelope<RemoteOrderResponse>> cancelOrder(@Path("orderId") String orderId);
}
