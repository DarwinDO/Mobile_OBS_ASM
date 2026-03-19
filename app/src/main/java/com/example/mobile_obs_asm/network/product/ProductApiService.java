package com.example.mobile_obs_asm.network.product;

import com.example.mobile_obs_asm.network.ApiEnvelope;
import com.example.mobile_obs_asm.network.SpringPageResponse;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.PATCH;
import retrofit2.http.Path;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface ProductApiService {

    @GET("api/products")
    Call<ApiEnvelope<SpringPageResponse<RemoteProductResponse>>> getPublicProducts(
            @Query("page") int page,
            @Query("size") int size
    );

    @GET("api/products/{id}")
    Call<ApiEnvelope<RemoteProductResponse>> getProductDetail(@Path("id") String productId);

    @GET("api/products/my")
    Call<ApiEnvelope<SpringPageResponse<RemoteProductResponse>>> getMyProducts(
            @Query("page") int page,
            @Query("size") int size
    );

    @PATCH("api/products/{id}/hide")
    Call<ApiEnvelope<RemoteProductResponse>> hideProduct(@Path("id") String productId);

    @PATCH("api/products/{id}/show")
    Call<ApiEnvelope<RemoteProductResponse>> showProduct(@Path("id") String productId);

    @Multipart
    @POST("api/products")
    Call<ApiEnvelope<RemoteProductResponse>> createProduct(
            @Part("title") RequestBody title,
            @Part("description") RequestBody description,
            @Part("price") RequestBody price,
            @Part("brakeTypeId") RequestBody brakeTypeId,
            @Part("frameMaterialId") RequestBody frameMaterialId,
            @Part("frameSize") RequestBody frameSize,
            @Part("wheelSize") RequestBody wheelSize,
            @Part("groupset") RequestBody groupset,
            @Part("condition") RequestBody condition,
            @Part("province") RequestBody province,
            @Part("district") RequestBody district,
            @Part List<MultipartBody.Part> images
    );
}
