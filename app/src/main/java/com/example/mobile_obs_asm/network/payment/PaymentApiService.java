package com.example.mobile_obs_asm.network.payment;

import com.example.mobile_obs_asm.network.ApiEnvelope;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface PaymentApiService {

    @POST("api/payments/orders/{orderId}/request")
    Call<ApiEnvelope<RemotePaymentRequestResponse>> requestUpfrontPayment(@Path("orderId") String orderId);

    @GET("api/payments/orders/{orderId}")
    Call<ApiEnvelope<List<RemotePaymentResponse>>> getOrderPayments(@Path("orderId") String orderId);
}
