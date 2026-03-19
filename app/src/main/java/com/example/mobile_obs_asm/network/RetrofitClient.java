package com.example.mobile_obs_asm.network;

import android.content.Context;

import com.example.mobile_obs_asm.BuildConfig;
import com.example.mobile_obs_asm.network.auth.AuthApiService;
import com.example.mobile_obs_asm.network.order.OrderApiService;
import com.example.mobile_obs_asm.network.product.ProductApiService;
import com.example.mobile_obs_asm.network.wishlist.WishlistApiService;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public final class RetrofitClient {

    private RetrofitClient() {
    }

    private static Retrofit createRetrofit(Context context) {
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BASIC);

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(new AuthHeaderInterceptor(context.getApplicationContext()))
                .addInterceptor(loggingInterceptor)
                .build();

        return new Retrofit.Builder()
                .baseUrl(BuildConfig.API_BASE_URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    public static AuthApiService createAuthApiService(Context context) {
        return createRetrofit(context).create(AuthApiService.class);
    }

    public static ProductApiService createProductApiService(Context context) {
        return createRetrofit(context).create(ProductApiService.class);
    }

    public static WishlistApiService createWishlistApiService(Context context) {
        return createRetrofit(context).create(WishlistApiService.class);
    }

    public static OrderApiService createOrderApiService(Context context) {
        return createRetrofit(context).create(OrderApiService.class);
    }
}
