package com.example.mobile_obs_asm.data;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.OpenableColumns;

import com.example.mobile_obs_asm.R;
import com.example.mobile_obs_asm.model.CreateListingDraft;
import com.example.mobile_obs_asm.model.SellerListing;
import com.example.mobile_obs_asm.model.SellerListingEditorData;
import com.example.mobile_obs_asm.network.ApiEnvelope;
import com.example.mobile_obs_asm.network.RetrofitClient;
import com.example.mobile_obs_asm.network.SpringPageResponse;
import com.example.mobile_obs_asm.network.product.ProductApiService;
import com.example.mobile_obs_asm.network.product.RemoteProductResponse;
import com.example.mobile_obs_asm.util.ApiErrorMessageExtractor;
import com.example.mobile_obs_asm.util.DisplayLabelFormatter;
import com.example.mobile_obs_asm.util.ProductImageUrlResolver;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SellerProductRemoteRepository {

    private final Context appContext;
    private final ProductApiService productApiService;

    public SellerProductRemoteRepository(Context context) {
        appContext = context.getApplicationContext();
        productApiService = RetrofitClient.createProductApiService(appContext);
    }

    public void fetchMyProducts(RepositoryCallback<List<SellerListing>> callback) {
        productApiService.getMyProducts(0, 20).enqueue(new Callback<ApiEnvelope<SpringPageResponse<RemoteProductResponse>>>() {
            @Override
            public void onResponse(
                    Call<ApiEnvelope<SpringPageResponse<RemoteProductResponse>>> call,
                    Response<ApiEnvelope<SpringPageResponse<RemoteProductResponse>>> response
            ) {
                if (!response.isSuccessful() || response.body() == null || response.body().getResult() == null) {
                    callback.onError(
                            ApiErrorMessageExtractor.extract(response, "Không thể tải tin của bạn lúc này."),
                            null
                    );
                    return;
                }

                List<RemoteProductResponse> remoteProducts = response.body().getResult().getContent();
                List<SellerListing> listings = new ArrayList<>();
                if (remoteProducts != null) {
                    for (RemoteProductResponse remoteProduct : remoteProducts) {
                        listings.add(mapListing(remoteProduct));
                    }
                }
                callback.onSuccess(listings);
            }

            @Override
            public void onFailure(Call<ApiEnvelope<SpringPageResponse<RemoteProductResponse>>> call, Throwable throwable) {
                callback.onError("Không thể kết nối để tải tin của bạn.", throwable);
            }
        });
    }

    public void fetchMyProductDetail(String productId, RepositoryCallback<SellerListingEditorData> callback) {
        productApiService.getMyProductDetail(productId).enqueue(new Callback<ApiEnvelope<RemoteProductResponse>>() {
            @Override
            public void onResponse(Call<ApiEnvelope<RemoteProductResponse>> call, Response<ApiEnvelope<RemoteProductResponse>> response) {
                if (!response.isSuccessful() || response.body() == null || response.body().getResult() == null) {
                    callback.onError(
                            ApiErrorMessageExtractor.extract(response, "Không thể tải thông tin chi tiết của tin này."),
                            null
                    );
                    return;
                }
                callback.onSuccess(mapEditorData(response.body().getResult()));
            }

            @Override
            public void onFailure(Call<ApiEnvelope<RemoteProductResponse>> call, Throwable throwable) {
                callback.onError("Không thể kết nối để tải dữ liệu chỉnh sửa tin.", throwable);
            }
        });
    }

    public void hideProduct(String productId, RepositoryCallback<SellerListing> callback) {
        productApiService.hideProduct(productId).enqueue(mapSingleProductCallback(
                callback,
                "Không thể ẩn tin này lúc này.",
                "Không thể kết nối để ẩn tin."
        ));
    }

    public void showProduct(String productId, RepositoryCallback<SellerListing> callback) {
        productApiService.showProduct(productId).enqueue(mapSingleProductCallback(
                callback,
                "Không thể gửi lại tin này lúc này.",
                "Không thể kết nối để cập nhật trạng thái tin."
        ));
    }

    public void createProduct(CreateListingDraft draft, RepositoryCallback<SellerListing> callback) {
        submitProduct(
                null,
                draft,
                callback,
                "Không thể đăng tin lúc này.",
                "Không thể kết nối để đăng tin."
        );
    }

    public void updateProduct(String productId, CreateListingDraft draft, RepositoryCallback<SellerListing> callback) {
        submitProduct(
                productId,
                draft,
                callback,
                "Không thể cập nhật tin lúc này.",
                "Không thể kết nối để cập nhật tin."
        );
    }

    public void deleteProduct(String productId, RepositoryCallback<Void> callback) {
        productApiService.deleteProduct(productId).enqueue(new Callback<ApiEnvelope<String>>() {
            @Override
            public void onResponse(Call<ApiEnvelope<String>> call, Response<ApiEnvelope<String>> response) {
                if (!response.isSuccessful()) {
                    callback.onError(
                            ApiErrorMessageExtractor.extract(response, "Không thể xoá tin này lúc này."),
                            null
                    );
                    return;
                }
                callback.onSuccess(null);
            }

            @Override
            public void onFailure(Call<ApiEnvelope<String>> call, Throwable throwable) {
                callback.onError("Không thể kết nối để xoá tin.", throwable);
            }
        });
    }

    private void submitProduct(
            String productId,
            CreateListingDraft draft,
            RepositoryCallback<SellerListing> callback,
            String fallbackError,
            String networkError
    ) {
        List<MultipartBody.Part> imageParts;
        try {
            imageParts = buildImageParts(draft.getImageUris());
        } catch (IOException exception) {
            callback.onError("Không thể đọc ảnh bạn đã chọn để gửi lên backend.", exception);
            return;
        }

        Callback<ApiEnvelope<RemoteProductResponse>> responseCallback = mapSingleProductCallback(callback, fallbackError, networkError);
        if (productId == null) {
            productApiService.createProduct(
                    toRequestBody(draft.getTitle()),
                    toNullableRequestBody(draft.getDescription()),
                    toRequestBody(String.valueOf(draft.getPrice())),
                    toRequestBody(draft.getBrakeTypeId()),
                    toRequestBody(draft.getFrameMaterialId()),
                    toRequestBody(draft.getFrameSize()),
                    toRequestBody(draft.getWheelSize()),
                    toNullableRequestBody(draft.getGroupset()),
                    toRequestBody(draft.getCondition()),
                    toRequestBody(draft.getProvince()),
                    toNullableRequestBody(draft.getDistrict()),
                    imageParts
            ).enqueue(responseCallback);
            return;
        }

        productApiService.updateProduct(
                productId,
                toRequestBody(draft.getTitle()),
                toNullableRequestBody(draft.getDescription()),
                toRequestBody(String.valueOf(draft.getPrice())),
                toRequestBody(draft.getBrakeTypeId()),
                toRequestBody(draft.getFrameMaterialId()),
                toRequestBody(draft.getFrameSize()),
                toRequestBody(draft.getWheelSize()),
                toNullableRequestBody(draft.getGroupset()),
                toRequestBody(draft.getCondition()),
                toRequestBody(draft.getProvince()),
                toNullableRequestBody(draft.getDistrict()),
                imageParts
        ).enqueue(responseCallback);
    }

    private Callback<ApiEnvelope<RemoteProductResponse>> mapSingleProductCallback(
            RepositoryCallback<SellerListing> callback,
            String fallbackError,
            String networkError
    ) {
        return new Callback<ApiEnvelope<RemoteProductResponse>>() {
            @Override
            public void onResponse(
                    Call<ApiEnvelope<RemoteProductResponse>> call,
                    Response<ApiEnvelope<RemoteProductResponse>> response
            ) {
                if (!response.isSuccessful() || response.body() == null || response.body().getResult() == null) {
                    callback.onError(ApiErrorMessageExtractor.extract(response, fallbackError), null);
                    return;
                }
                callback.onSuccess(mapListing(response.body().getResult()));
            }

            @Override
            public void onFailure(Call<ApiEnvelope<RemoteProductResponse>> call, Throwable throwable) {
                callback.onError(networkError, throwable);
            }
        };
    }

    private List<MultipartBody.Part> buildImageParts(List<Uri> imageUris) throws IOException {
        List<MultipartBody.Part> imageParts = new ArrayList<>();
        for (int index = 0; index < imageUris.size(); index++) {
            Uri imageUri = imageUris.get(index);
            String mimeType = appContext.getContentResolver().getType(imageUri);
            if (mimeType == null || mimeType.isEmpty()) {
                mimeType = "image/*";
            }

            RequestBody imageBody = RequestBody.create(readBytes(imageUri), MediaType.parse(mimeType));
            imageParts.add(MultipartBody.Part.createFormData(
                    "images",
                    resolveFileName(imageUri, index),
                    imageBody
            ));
        }
        return imageParts;
    }

    private byte[] readBytes(Uri uri) throws IOException {
        try (InputStream inputStream = appContext.getContentResolver().openInputStream(uri);
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            if (inputStream == null) {
                throw new IOException("Cannot open image stream");
            }

            byte[] buffer = new byte[8 * 1024];
            int read;
            while ((read = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, read);
            }
            return outputStream.toByteArray();
        }
    }

    private String resolveFileName(Uri uri, int index) {
        Cursor cursor = appContext.getContentResolver().query(uri, null, null, null, null);
        if (cursor != null) {
            try {
                int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                if (cursor.moveToFirst() && nameIndex >= 0) {
                    String displayName = cursor.getString(nameIndex);
                    if (displayName != null && !displayName.trim().isEmpty()) {
                        return displayName.trim();
                    }
                }
            } finally {
                cursor.close();
            }
        }
        return "listing_image_" + (index + 1) + ".jpg";
    }

    private RequestBody toRequestBody(String value) {
        return RequestBody.create(value, MultipartBody.FORM);
    }

    private RequestBody toNullableRequestBody(String value) {
        return value == null || value.trim().isEmpty() ? null : toRequestBody(value.trim());
    }

    private SellerListing mapListing(RemoteProductResponse remoteProduct) {
        return new SellerListing(
                remoteProduct.getId(),
                fallback(remoteProduct.getTitle(), "Tin đang cập nhật"),
                buildSummary(remoteProduct),
                buildMeta(remoteProduct),
                remoteProduct.getPrice() == null ? 0L : remoteProduct.getPrice().longValue(),
                fallback(remoteProduct.getStatus(), "pending"),
                DisplayLabelFormatter.formatValue(remoteProduct.getStatus()),
                buildCoverLabel(remoteProduct.getTitle()),
                ProductImageUrlResolver.resolvePrimaryImageUrl(remoteProduct),
                pickCoverColor(remoteProduct.getId()),
                remoteProduct.isLockedForTransaction()
        );
    }

    private SellerListingEditorData mapEditorData(RemoteProductResponse remoteProduct) {
        int imageCount = remoteProduct.getImages() == null ? 0 : remoteProduct.getImages().size();
        return new SellerListingEditorData(
                fallback(remoteProduct.getId(), ""),
                fallback(remoteProduct.getTitle(), ""),
                fallback(remoteProduct.getDescription(), ""),
                remoteProduct.getPrice() == null ? 0L : remoteProduct.getPrice().longValue(),
                fallback(remoteProduct.getProvince(), ""),
                fallback(remoteProduct.getDistrict(), ""),
                fallback(remoteProduct.getFrameSize(), ""),
                fallback(remoteProduct.getWheelSize(), ""),
                fallback(remoteProduct.getGroupset(), ""),
                fallback(remoteProduct.getCondition(), ""),
                fallback(remoteProduct.getBrakeTypeName(), ""),
                fallback(remoteProduct.getFrameMaterialName(), ""),
                imageCount
        );
    }

    private String buildSummary(RemoteProductResponse remoteProduct) {
        String description = fallback(remoteProduct.getDescription(), "");
        if (description.length() > 88) {
            return description.substring(0, 88) + "...";
        }
        if (!description.isEmpty()) {
            return description;
        }

        return "Khung " + fallback(remoteProduct.getFrameSize(), "chưa cập nhật")
                + " • Bánh " + fallback(remoteProduct.getWheelSize(), "chưa cập nhật");
    }

    private String buildMeta(RemoteProductResponse remoteProduct) {
        StringBuilder builder = new StringBuilder();
        builder.append(fallback(remoteProduct.getProvince(), "Đang cập nhật khu vực"));

        if (remoteProduct.getDistrict() != null && !remoteProduct.getDistrict().trim().isEmpty()) {
            builder.append(" • ").append(remoteProduct.getDistrict().trim());
        }

        builder.append(" • ").append(DisplayLabelFormatter.formatValue(remoteProduct.getCondition()));
        if (remoteProduct.isLockedForTransaction()) {
            builder.append(" • ").append(appContext.getString(R.string.seller_listing_locked_label));
        }
        return builder.toString();
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

    private int pickCoverColor(String seed) {
        String safeSeed = seed == null ? "seller" : seed;
        int[] colors = {
                R.color.banner_warm,
                R.color.card_blue,
                R.color.card_sand,
                R.color.card_mint
        };
        return colors[Math.abs(safeSeed.hashCode()) % colors.length];
    }

    private String fallback(String value, String fallback) {
        return value == null || value.trim().isEmpty() ? fallback : value.trim();
    }
}
