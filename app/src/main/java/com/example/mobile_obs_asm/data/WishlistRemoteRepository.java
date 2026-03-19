package com.example.mobile_obs_asm.data;

import android.content.Context;

import com.example.mobile_obs_asm.R;
import com.example.mobile_obs_asm.model.Product;
import com.example.mobile_obs_asm.network.ApiEnvelope;
import com.example.mobile_obs_asm.network.RetrofitClient;
import com.example.mobile_obs_asm.network.wishlist.RemoteWishlistItemResponse;
import com.example.mobile_obs_asm.network.wishlist.WishlistApiService;
import com.example.mobile_obs_asm.util.DateLabelFormatter;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WishlistRemoteRepository {

    private final WishlistApiService wishlistApiService;

    public WishlistRemoteRepository(Context context) {
        wishlistApiService = RetrofitClient.createWishlistApiService(context);
    }

    public void fetchWishlist(RepositoryCallback<List<Product>> callback) {
        wishlistApiService.getWishlist().enqueue(new Callback<ApiEnvelope<List<RemoteWishlistItemResponse>>>() {
            @Override
            public void onResponse(
                    Call<ApiEnvelope<List<RemoteWishlistItemResponse>>> call,
                    Response<ApiEnvelope<List<RemoteWishlistItemResponse>>> response
            ) {
                if (!response.isSuccessful() || response.body() == null || response.body().getResult() == null) {
                    callback.onError(resolveErrorMessage(response.code(), "Wishlist response was not accepted by the backend."), null);
                    return;
                }

                List<Product> mappedProducts = new ArrayList<>();
                for (RemoteWishlistItemResponse remoteItem : response.body().getResult()) {
                    mappedProducts.add(mapProduct(remoteItem));
                }
                callback.onSuccess(mappedProducts);
            }

            @Override
            public void onFailure(Call<ApiEnvelope<List<RemoteWishlistItemResponse>>> call, Throwable throwable) {
                callback.onError("Could not reach wishlist endpoint.", throwable);
            }
        });
    }

    public void addProduct(String productId, RepositoryCallback<Product> callback) {
        wishlistApiService.addProduct(productId).enqueue(new Callback<ApiEnvelope<RemoteWishlistItemResponse>>() {
            @Override
            public void onResponse(
                    Call<ApiEnvelope<RemoteWishlistItemResponse>> call,
                    Response<ApiEnvelope<RemoteWishlistItemResponse>> response
            ) {
                if (!response.isSuccessful() || response.body() == null || response.body().getResult() == null) {
                    callback.onError(resolveErrorMessage(response.code(), "Wishlist update was rejected by the backend."), null);
                    return;
                }
                callback.onSuccess(mapProduct(response.body().getResult()));
            }

            @Override
            public void onFailure(Call<ApiEnvelope<RemoteWishlistItemResponse>> call, Throwable throwable) {
                callback.onError("Could not reach wishlist update endpoint.", throwable);
            }
        });
    }

    private Product mapProduct(RemoteWishlistItemResponse remoteItem) {
        String productId = fallback(remoteItem.getProductId(), "wishlist-item");
        String title = fallback(remoteItem.getTitle(), "Saved listing");
        String sellerName = fallback(remoteItem.getSellerName(), "Unknown seller");
        String status = formatEnum(remoteItem.getStatus());
        return new Product(
                productId,
                title,
                "Saved seller: " + sellerName,
                buildCoverLabel(title),
                "Seller: " + sellerName,
                status,
                "Wishlist sync",
                buildDescription(remoteItem),
                "Pending from detail sync",
                "Pending from detail sync",
                "Pending from detail sync",
                remoteItem.getPrice() == null ? 0L : remoteItem.getPrice().longValue(),
                pickHeroColor(productId),
                pickCoverColor(productId),
                true
        );
    }

    private String buildDescription(RemoteWishlistItemResponse remoteItem) {
        String savedAt = DateLabelFormatter.formatIsoDateTime(remoteItem.getAddedAt());
        if (savedAt.isEmpty()) {
            return "This item was saved in your backend wishlist. Open detail to fetch the full product profile.";
        }
        return "Saved on " + savedAt + ". Open detail to fetch the full product profile.";
    }

    private String resolveErrorMessage(int statusCode, String fallbackMessage) {
        if (statusCode == 401 || statusCode == 403) {
            return "Please sign in with a backend account to sync wishlist.";
        }
        return fallbackMessage;
    }

    private String buildCoverLabel(String title) {
        if (title == null || title.trim().isEmpty()) {
            return "WL";
        }
        String[] parts = title.trim().split("\\s+");
        if (parts.length == 1) {
            return parts[0].substring(0, Math.min(2, parts[0].length())).toUpperCase();
        }
        return (parts[0].substring(0, 1) + parts[1].substring(0, 1)).toUpperCase();
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
        String safeSeed = seed == null ? "wishlist" : seed;
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
        String safeSeed = seed == null ? "wishlist" : seed;
        int[] colors = {
                R.color.card_ink,
                R.color.banner_olive,
                R.color.banner_warm,
                R.color.primary_soft
        };
        return colors[Math.abs(safeSeed.hashCode()) % colors.length];
    }
}
