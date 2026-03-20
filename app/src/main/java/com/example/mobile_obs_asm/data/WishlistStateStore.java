package com.example.mobile_obs_asm.data;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class WishlistStateStore {

    private static final String PREFS_NAME = "mobile_obs_wishlist_state";
    private static final String KEY_PREFIX = "saved_product_ids_";

    private static WishlistStateStore instance;

    private final Context appContext;
    private final SharedPreferences sharedPreferences;

    private WishlistStateStore(Context context) {
        appContext = context.getApplicationContext();
        sharedPreferences = appContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public static synchronized WishlistStateStore getInstance(Context context) {
        if (instance == null) {
            instance = new WishlistStateStore(context);
        }
        return instance;
    }

    public boolean isSaved(String productId) {
        if (productId == null || productId.trim().isEmpty()) {
            return false;
        }
        return getSavedIds().contains(productId.trim());
    }

    public void markSaved(String productId) {
        if (productId == null || productId.trim().isEmpty()) {
            return;
        }
        Set<String> savedIds = getSavedIds();
        savedIds.add(productId.trim());
        persist(savedIds);
    }

    public void markRemoved(String productId) {
        if (productId == null || productId.trim().isEmpty()) {
            return;
        }
        Set<String> savedIds = getSavedIds();
        savedIds.remove(productId.trim());
        persist(savedIds);
    }

    public void replaceSavedIds(Collection<String> productIds) {
        Set<String> normalizedIds = new HashSet<>();
        if (productIds != null) {
            for (String productId : productIds) {
                if (productId != null && !productId.trim().isEmpty()) {
                    normalizedIds.add(productId.trim());
                }
            }
        }
        persist(normalizedIds);
    }

    public void clearActiveUserState() {
        sharedPreferences.edit().remove(resolveStorageKey()).apply();
    }

    private Set<String> getSavedIds() {
        return new HashSet<>(sharedPreferences.getStringSet(resolveStorageKey(), Collections.emptySet()));
    }

    private void persist(Set<String> savedIds) {
        sharedPreferences.edit()
                .putStringSet(resolveStorageKey(), new HashSet<>(savedIds))
                .apply();
    }

    private String resolveStorageKey() {
        String currentUserId = SessionManager.getInstance(appContext).getCurrentUserId();
        if (currentUserId == null || currentUserId.trim().isEmpty()) {
            currentUserId = "guest";
        }
        return KEY_PREFIX + currentUserId.trim();
    }
}
