package com.example.mobile_obs_asm.data;

import android.content.Context;

import com.example.mobile_obs_asm.R;
import com.example.mobile_obs_asm.model.Product;
import com.example.mobile_obs_asm.network.ApiEnvelope;
import com.example.mobile_obs_asm.network.RetrofitClient;
import com.example.mobile_obs_asm.network.SpringPageResponse;
import com.example.mobile_obs_asm.network.product.ProductApiService;
import com.example.mobile_obs_asm.network.product.RemoteProductResponse;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductRemoteRepository {

    private final ProductApiService productApiService;

    public ProductRemoteRepository(Context context) {
        productApiService = RetrofitClient.createProductApiService(context);
    }

    public void fetchProducts(RepositoryCallback<List<Product>> callback) {
        productApiService.getPublicProducts(0, 12).enqueue(new Callback<ApiEnvelope<SpringPageResponse<RemoteProductResponse>>>() {
            @Override
            public void onResponse(
                    Call<ApiEnvelope<SpringPageResponse<RemoteProductResponse>>> call,
                    Response<ApiEnvelope<SpringPageResponse<RemoteProductResponse>>> response
            ) {
                if (!response.isSuccessful() || response.body() == null || response.body().getResult() == null) {
                    callback.onError("Product list response was empty.", null);
                    return;
                }

                List<RemoteProductResponse> remoteProducts = response.body().getResult().getContent();
                if (remoteProducts == null) {
                    callback.onSuccess(new ArrayList<>());
                    return;
                }

                List<Product> mappedProducts = new ArrayList<>();
                for (RemoteProductResponse remoteProduct : remoteProducts) {
                    mappedProducts.add(mapProduct(remoteProduct));
                }
                callback.onSuccess(mappedProducts);
            }

            @Override
            public void onFailure(Call<ApiEnvelope<SpringPageResponse<RemoteProductResponse>>> call, Throwable throwable) {
                callback.onError("Could not reach product list endpoint.", throwable);
            }
        });
    }

    public void fetchProductDetail(String productId, RepositoryCallback<Product> callback) {
        productApiService.getProductDetail(productId).enqueue(new Callback<ApiEnvelope<RemoteProductResponse>>() {
            @Override
            public void onResponse(Call<ApiEnvelope<RemoteProductResponse>> call, Response<ApiEnvelope<RemoteProductResponse>> response) {
                if (!response.isSuccessful() || response.body() == null || response.body().getResult() == null) {
                    callback.onError("Product detail response was empty.", null);
                    return;
                }
                callback.onSuccess(mapProduct(response.body().getResult()));
            }

            @Override
            public void onFailure(Call<ApiEnvelope<RemoteProductResponse>> call, Throwable throwable) {
                callback.onError("Could not reach product detail endpoint.", throwable);
            }
        });
    }

    private Product mapProduct(RemoteProductResponse remoteProduct) {
        return new Product(
                remoteProduct.getId(),
                fallback(remoteProduct.getTitle(), "Unnamed product"),
                buildTagline(remoteProduct),
                buildCoverLabel(remoteProduct.getTitle()),
                fallback(remoteProduct.getProvince(), "Location pending"),
                formatEnum(remoteProduct.getCondition()),
                buildBadge(remoteProduct),
                fallback(remoteProduct.getDescription(), "Description will appear here after backend wiring."),
                fallback(remoteProduct.getFrameSize(), "Pending"),
                fallback(remoteProduct.getWheelSize(), "Pending"),
                fallback(remoteProduct.getGroupset(), "Pending"),
                remoteProduct.getPrice() == null ? 0L : remoteProduct.getPrice().longValue(),
                pickHeroColor(remoteProduct.getId()),
                pickCoverColor(remoteProduct.getId()),
                true
        );
    }

    private String buildTagline(RemoteProductResponse remoteProduct) {
        String brandName = fallback(remoteProduct.getBrandName(), "");
        String description = fallback(remoteProduct.getDescription(), "");
        if (!brandName.isEmpty()) {
            return brandName + " build with mobile-ready listing preview.";
        }
        if (description.length() > 80) {
            return description.substring(0, 80) + "...";
        }
        return description.isEmpty() ? "Product detail from backend." : description;
    }

    private String buildCoverLabel(String title) {
        if (title == null || title.trim().isEmpty()) {
            return "OB";
        }
        String[] parts = title.trim().split("\\s+");
        if (parts.length == 1) {
            return parts[0].substring(0, Math.min(2, parts[0].length())).toUpperCase();
        }
        return (parts[0].substring(0, 1) + parts[1].substring(0, 1)).toUpperCase();
    }

    private String buildBadge(RemoteProductResponse remoteProduct) {
        if (remoteProduct.isVerified()) {
            return "Verified inspection";
        }
        return formatEnum(remoteProduct.getCondition());
    }

    private String formatEnum(String rawValue) {
        if (rawValue == null || rawValue.isEmpty()) {
            return "Unknown";
        }
        String normalized = rawValue.toLowerCase().replace('_', ' ');
        String[] words = normalized.split(" ");
        StringBuilder builder = new StringBuilder();
        for (String word : words) {
            if (word.isEmpty()) {
                continue;
            }
            if (builder.length() > 0) {
                builder.append(' ');
            }
            builder.append(Character.toUpperCase(word.charAt(0))).append(word.substring(1));
        }
        return builder.toString();
    }

    private String fallback(String value, String fallback) {
        return value == null || value.isEmpty() ? fallback : value;
    }

    private int pickHeroColor(String seed) {
        String safeSeed = seed == null ? "product" : seed;
        int[] colors = {
                R.color.card_blue,
                R.color.card_mint,
                R.color.card_sand,
                R.color.card_peach,
                R.color.card_berry
        };
        return colors[Math.abs(safeSeed.hashCode()) % colors.length];
    }

    private int pickCoverColor(String seed) {
        String safeSeed = seed == null ? "product" : seed;
        int[] colors = {
                R.color.card_ink,
                R.color.banner_olive,
                R.color.banner_warm,
                R.color.primary_soft
        };
        return colors[Math.abs(safeSeed.hashCode()) % colors.length];
    }
}
