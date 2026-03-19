package com.example.mobile_obs_asm.network.product;

import com.example.mobile_obs_asm.network.ApiEnvelope;
import com.example.mobile_obs_asm.network.SpringPageResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ProductApiService {

    @GET("api/products")
    Call<ApiEnvelope<SpringPageResponse<RemoteProductResponse>>> getPublicProducts(
            @Query("page") int page,
            @Query("size") int size
    );

    @GET("api/products/{id}")
    Call<ApiEnvelope<RemoteProductResponse>> getProductDetail(@Path("id") String productId);
}
