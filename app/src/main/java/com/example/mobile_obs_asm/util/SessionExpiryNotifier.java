package com.example.mobile_obs_asm.util;

import android.content.Context;
import android.content.Intent;

public final class SessionExpiryNotifier {

    public static final String ACTION_SESSION_EXPIRED = "com.example.mobile_obs_asm.ACTION_SESSION_EXPIRED";
    public static final String EXTRA_REASON = "extra_reason";

    private SessionExpiryNotifier() {
    }

    public static void notifyExpired(Context context, String reason) {
        Intent intent = new Intent(ACTION_SESSION_EXPIRED);
        intent.setPackage(context.getPackageName());
        intent.putExtra(EXTRA_REASON, reason);
        context.sendBroadcast(intent);
    }
}
