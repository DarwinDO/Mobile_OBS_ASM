package com.example.mobile_obs_asm.data;

import android.content.Context;

import com.example.mobile_obs_asm.R;
import com.example.mobile_obs_asm.model.Product;
import com.example.mobile_obs_asm.network.ApiEnvelope;
import com.example.mobile_obs_asm.network.RetrofitClient;
import com.example.mobile_obs_asm.network.wishlist.RemoteWishlistItemResponse;
import com.example.mobile_obs_asm.network.wishlist.WishlistApiService;
import com.example.mobile_obs_asm.util.ApiErrorMessageExtractor;
import com.example.mobile_obs_asm.util.DateLabelFormatter;
import com.example.mobile_obs_asm.util.DisplayLabelFormatter;
import com.example.mobile_obs_asm.util.ProductImageUrlResolver;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WishlistRemoteRepository {

    private final Context appContext;
    private final WishlistApiService wishlistApiService;

    public WishlistRemoteRepository(Context context) {
        appContext = context.getApplicationContext();
        wishlistApiService = RetrofitClient.createWishlistApiService(appContext);
    }

    public void fetchWishlist(RepositoryCallback<List<Product>> callback) {
        wishlistApiService.getWishlist().enqueue(new Callback<ApiEnvelope<List<RemoteWishlistItemResponse>>>() {
            @Override
            public void onResponse(
                    Call<ApiEnvelope<List<RemoteWishlistItemResponse>>> call,
                    Response<ApiEnvelope<List<RemoteWishlistItemResponse>>> response
            ) {
                if (!response.isSuccessful() || response.body() == null || response.body().getResult() == null) {
                    callback.onError(
                            ApiErrorMessageExtractor.extract(
                                    response,
                                    resolveErrorMessage(response.code(), "Không thể đọc dữ liệu mục yêu thích từ máy chủ.")
                            ),
                            null
                    );
                    return;
                }

                List<Product> mappedProducts = new ArrayList<>();
                List<String> savedIds = new ArrayList<>();
                for (RemoteWishlistItemResponse remoteItem : response.body().getResult()) {
                    mappedProducts.add(mapProduct(remoteItem));
                    String productId = remoteItem.getProductId();
                    if (productId != null && !productId.trim().isEmpty()) {
                        savedIds.add(productId.trim());
                    }
                }
                WishlistStateStore.getInstance(appContext).replaceSavedIds(savedIds);
                callback.onSuccess(mappedProducts);
            }

            @Override
            public void onFailure(Call<ApiEnvelope<List<RemoteWishlistItemResponse>>> call, Throwable throwable) {
                callback.onError("Không thể kết nối để tải mục yêu thích.", throwable);
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
                    callback.onError(
                            ApiErrorMessageExtractor.extract(
                                    response,
                                    resolveErrorMessage(response.code(), "Không thể cập nhật mục yêu thích lúc này.")
                            ),
                            null
                    );
                    return;
                }
                Product mappedProduct = mapProduct(response.body().getResult());
                WishlistStateStore.getInstance(appContext).markSaved(mappedProduct.getId());
                callback.onSuccess(mappedProduct);
            }

            @Override
            public void onFailure(Call<ApiEnvelope<RemoteWishlistItemResponse>> call, Throwable throwable) {
                callback.onError("Không thể kết nối để lưu sản phẩm vào mục yêu thích.", throwable);
            }
        });
    }

    public void removeProduct(String productId, RepositoryCallback<Void> callback) {
        wishlistApiService.removeProduct(productId).enqueue(new Callback<ApiEnvelope<Void>>() {
            @Override
            public void onResponse(Call<ApiEnvelope<Void>> call, Response<ApiEnvelope<Void>> response) {
                if (!response.isSuccessful()) {
                    callback.onError(
                            ApiErrorMessageExtractor.extract(
                                    response,
                                    resolveErrorMessage(response.code(), "Không thể xoá sản phẩm khỏi mục yêu thích lúc này.")
                            ),
                            null
                    );
                    return;
                }
                WishlistStateStore.getInstance(appContext).markRemoved(productId);
                callback.onSuccess(null);
            }

            @Override
            public void onFailure(Call<ApiEnvelope<Void>> call, Throwable throwable) {
                callback.onError("Không thể kết nối để xoá sản phẩm khỏi mục yêu thích.", throwable);
            }
        });
    }

    private Product mapProduct(RemoteWishlistItemResponse remoteItem) {
        String productId = fallback(remoteItem.getProductId(), "wishlist-item");
        String title = fallback(remoteItem.getTitle(), "Xe đã lưu");
        String sellerName = fallback(remoteItem.getSellerName(), "Người bán đang cập nhật");
        String status = formatValue(remoteItem.getStatus());
        return new Product(
                productId,
                title,
                "Người bán: " + sellerName,
                buildCoverLabel(title),
                ProductImageUrlResolver.hasValue(remoteItem.getPrimaryImageUrl()) ? remoteItem.getPrimaryImageUrl().trim() : null,
                "Người bán: " + sellerName,
                status,
                "Đã lưu vào yêu thích",
                buildDescription(remoteItem),
                "Đang cập nhật",
                "Đang cập nhật",
                "Đang cập nhật",
                remoteItem.getPrice() == null ? 0L : remoteItem.getPrice().longValue(),
                pickHeroColor(productId),
                pickCoverColor(productId),
                true
        );
    }

    private String buildDescription(RemoteWishlistItemResponse remoteItem) {
        String savedAt = DateLabelFormatter.formatIsoDateTime(remoteItem.getAddedAt());
        if (savedAt.isEmpty()) {
            return "Sản phẩm này đã được lưu vào mục yêu thích của bạn. Hãy mở chi tiết để xem đầy đủ thông tin.";
        }
        return "Đã lưu lúc " + savedAt + ". Hãy mở chi tiết để xem đầy đủ thông tin.";
    }

    private String resolveErrorMessage(int statusCode, String fallbackMessage) {
        if (statusCode == 401 || statusCode == 403) {
            return "Hãy đăng nhập để đồng bộ mục yêu thích của bạn.";
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

    private String formatValue(String rawValue) {
        return DisplayLabelFormatter.formatValue(rawValue);
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
