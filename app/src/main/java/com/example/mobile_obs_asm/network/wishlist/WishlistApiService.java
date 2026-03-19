package com.example.mobile_obs_asm.network.wishlist;

import com.example.mobile_obs_asm.network.ApiEnvelope;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface WishlistApiService {

    @GET("api/wishlist")
    Call<ApiEnvelope<List<RemoteWishlistItemResponse>>> getWishlist();

    @POST("api/wishlist/{productId}")
    Call<ApiEnvelope<RemoteWishlistItemResponse>> addProduct(@Path("productId") String productId);

    @DELETE("api/wishlist/{productId}")
    Call<ApiEnvelope<Void>> removeProduct(@Path("productId") String productId);
}
