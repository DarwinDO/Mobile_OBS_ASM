package com.example.mobile_obs_asm.data;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;

import com.example.mobile_obs_asm.R;
import com.example.mobile_obs_asm.model.UserProfile;
import com.example.mobile_obs_asm.network.auth.RemoteAuthResponse;

import java.io.IOException;
import java.security.GeneralSecurityException;

@SuppressWarnings("deprecation")
public class SessionManager {

    private static final String PREFS_NAME = "mobile_obs_secure_prefs";
    private static final String KEY_ACCESS_TOKEN = "access_token";
    private static final String KEY_REFRESH_TOKEN = "refresh_token";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_FIRST_NAME = "first_name";
    private static final String KEY_LAST_NAME = "last_name";
    private static final String KEY_ROLE = "role";
    private static final String KEY_ADDRESS = "address";

    private static SessionManager instance;

    private final Context appContext;
    private final SharedPreferences sharedPreferences;

    private SessionManager(Context context) {
        appContext = context.getApplicationContext();
        sharedPreferences = createSecurePreferences(appContext);
    }

    public static synchronized SessionManager getInstance(Context context) {
        if (instance == null) {
            instance = new SessionManager(context);
        }
        return instance;
    }

    private SharedPreferences createSecurePreferences(Context context) {
        try {
            MasterKey masterKey = new MasterKey.Builder(context)
                    .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                    .build();
            return EncryptedSharedPreferences.create(
                    context,
                    PREFS_NAME,
                    masterKey,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            );
        } catch (GeneralSecurityException | IOException exception) {
            return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        }
    }

    public void saveAuthSession(RemoteAuthResponse authResponse) {
        if (authResponse == null || authResponse.getUser() == null) {
            return;
        }

        sharedPreferences.edit()
                .putString(KEY_ACCESS_TOKEN, safe(authResponse.getAccessToken()))
                .putString(KEY_REFRESH_TOKEN, safe(authResponse.getRefreshToken()))
                .putString(KEY_USER_ID, safe(authResponse.getUser().getId()))
                .putString(KEY_EMAIL, safe(authResponse.getUser().getEmail()))
                .putString(KEY_FIRST_NAME, safe(authResponse.getUser().getFirstName()))
                .putString(KEY_LAST_NAME, safe(authResponse.getUser().getLastName()))
                .putString(KEY_ROLE, safe(authResponse.getUser().getRole()))
                .putString(KEY_ADDRESS, safe(authResponse.getUser().getDefaultAddress()))
                .commit();
    }

    public boolean hasActiveSession() {
        return !getAccessToken().isEmpty();
    }

    public String getAccessToken() {
        return sharedPreferences.getString(KEY_ACCESS_TOKEN, "");
    }

    public void clearSession() {
        WishlistStateStore.getInstance(appContext).clearActiveUserState();
        sharedPreferences.edit().clear().commit();
    }

    public String getCurrentUserId() {
        return sharedPreferences.getString(KEY_USER_ID, "");
    }

    public boolean isSellerSession() {
        return "seller".equals(getStoredRoleKey());
    }

    public boolean isBuyerSession() {
        return "buyer".equals(getStoredRoleKey());
    }

    public UserProfile getStoredUserProfile() {
        if (!hasActiveSession()) {
            return null;
        }

        String firstName = sharedPreferences.getString(KEY_FIRST_NAME, "");
        String lastName = sharedPreferences.getString(KEY_LAST_NAME, "");
        String name = (firstName + " " + lastName).trim();
        if (name.isEmpty()) {
            name = "Người dùng";
        }

        String role = getStoredRoleKey();
        String email = sharedPreferences.getString(KEY_EMAIL, "");
        String address = sharedPreferences.getString(KEY_ADDRESS, "");
        if (address.isEmpty()) {
            address = appContext.getString(R.string.profile_location_pending);
        }

        return new UserProfile(name, normalizeRole(role), email, address, 0, 0);
    }

    private String safe(String value) {
        return value == null ? "" : value;
    }

    private String getStoredRoleKey() {
        return safe(sharedPreferences.getString(KEY_ROLE, "buyer")).toLowerCase();
    }

    private String normalizeRole(String rawRole) {
        if (rawRole == null || rawRole.isEmpty()) {
            return "Người dùng";
        }

        String lower = rawRole.toLowerCase();
        if ("buyer".equals(lower)) {
            return "Người mua";
        }
        if ("seller".equals(lower)) {
            return "Người bán";
        }
        if ("admin".equals(lower)) {
            return "Quản trị viên";
        }
        if ("inspector".equals(lower)) {
            return "Kiểm định viên";
        }
        if ("guest".equals(lower)) {
            return "Khách";
        }
        return Character.toUpperCase(lower.charAt(0)) + lower.substring(1);
    }
}
