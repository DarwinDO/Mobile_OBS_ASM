package com.example.mobile_obs_asm.util;

import com.example.mobile_obs_asm.network.product.RemoteProductResponse;

import java.util.List;

public final class ProductImageUrlResolver {

    private ProductImageUrlResolver() {
    }

    public static String resolvePrimaryImageUrl(RemoteProductResponse remoteProduct) {
        if (remoteProduct == null) {
            return null;
        }
        return resolvePrimaryImageUrl(remoteProduct.getImages());
    }

    public static String resolvePrimaryImageUrl(List<RemoteProductResponse.ImageInfo> images) {
        if (images == null || images.isEmpty()) {
            return null;
        }

        for (RemoteProductResponse.ImageInfo image : images) {
            if (image != null && image.isPrimary() && hasValue(image.getUrl())) {
                return image.getUrl().trim();
            }
        }

        for (RemoteProductResponse.ImageInfo image : images) {
            if (image != null && hasValue(image.getUrl())) {
                return image.getUrl().trim();
            }
        }
        return null;
    }

    public static boolean hasValue(String value) {
        return value != null && !value.trim().isEmpty();
    }
}
