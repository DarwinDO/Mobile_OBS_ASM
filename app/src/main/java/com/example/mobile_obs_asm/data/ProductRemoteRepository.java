package com.example.mobile_obs_asm.data;

import android.content.Context;

import com.example.mobile_obs_asm.R;
import com.example.mobile_obs_asm.model.Product;
import com.example.mobile_obs_asm.network.ApiEnvelope;
import com.example.mobile_obs_asm.network.RetrofitClient;
import com.example.mobile_obs_asm.network.SpringPageResponse;
import com.example.mobile_obs_asm.network.product.ProductApiService;
import com.example.mobile_obs_asm.network.product.RemoteProductResponse;
import com.example.mobile_obs_asm.util.DisplayLabelFormatter;

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
                    callback.onError("Không thể đọc danh sách sản phẩm từ máy chủ.", null);
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
                callback.onError("Không thể kết nối để tải danh sách sản phẩm.", throwable);
            }
        });
    }

    public void fetchProductDetail(String productId, RepositoryCallback<Product> callback) {
        productApiService.getProductDetail(productId).enqueue(new Callback<ApiEnvelope<RemoteProductResponse>>() {
            @Override
            public void onResponse(Call<ApiEnvelope<RemoteProductResponse>> call, Response<ApiEnvelope<RemoteProductResponse>> response) {
                if (!response.isSuccessful() || response.body() == null || response.body().getResult() == null) {
                    callback.onError("Không thể đọc chi tiết sản phẩm từ máy chủ.", null);
                    return;
                }
                callback.onSuccess(mapProduct(response.body().getResult()));
            }

            @Override
            public void onFailure(Call<ApiEnvelope<RemoteProductResponse>> call, Throwable throwable) {
                callback.onError("Không thể kết nối để tải chi tiết sản phẩm.", throwable);
            }
        });
    }

    private Product mapProduct(RemoteProductResponse remoteProduct) {
        return new Product(
                remoteProduct.getId(),
                fallback(remoteProduct.getTitle(), "Xe đang cập nhật tên"),
                buildTagline(remoteProduct),
                buildCoverLabel(remoteProduct.getTitle()),
                fallback(remoteProduct.getProvince(), "Đang cập nhật khu vực"),
                formatValue(remoteProduct.getCondition()),
                buildBadge(remoteProduct),
                fallback(remoteProduct.getDescription(), "Người bán chưa bổ sung mô tả chi tiết cho sản phẩm này."),
                fallback(remoteProduct.getFrameSize(), "Đang cập nhật"),
                fallback(remoteProduct.getWheelSize(), "Đang cập nhật"),
                fallback(remoteProduct.getGroupset(), "Đang cập nhật"),
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
            return brandName + " với cấu hình phù hợp cho nhu cầu đi lại hằng ngày.";
        }
        if (description.length() > 80) {
            return description.substring(0, 80) + "...";
        }
        return description.isEmpty() ? "Thông tin sản phẩm đang được đồng bộ từ máy chủ." : description;
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
            return "Đã kiểm định";
        }
        return formatValue(remoteProduct.getCondition());
    }

    private String formatValue(String rawValue) {
        return DisplayLabelFormatter.formatValue(rawValue);
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
