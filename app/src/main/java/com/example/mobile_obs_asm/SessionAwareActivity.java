package com.example.mobile_obs_asm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.mobile_obs_asm.util.SessionExpiryNotifier;

public abstract class SessionAwareActivity extends AppCompatActivity {

    private final BroadcastReceiver sessionExpiryReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (isFinishing()) {
                return;
            }

            String reason = intent.getStringExtra(SessionExpiryNotifier.EXTRA_REASON);
            Toast.makeText(
                    SessionAwareActivity.this,
                    reason == null || reason.trim().isEmpty()
                            ? getString(R.string.session_expired_message)
                            : reason,
                    Toast.LENGTH_LONG
            ).show();

            Intent loginIntent = LoginActivity.createIntent(SessionAwareActivity.this, null);
            loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(loginIntent);
            finish();
        }
    };

    @Override
    protected void onStart() {
        super.onStart();
        ContextCompat.registerReceiver(
                this,
                sessionExpiryReceiver,
                new IntentFilter(SessionExpiryNotifier.ACTION_SESSION_EXPIRED),
                ContextCompat.RECEIVER_NOT_EXPORTED
        );
    }

    @Override
    protected void onStop() {
        try {
            unregisterReceiver(sessionExpiryReceiver);
        } catch (IllegalArgumentException ignored) {
            // Receiver may already be unregistered during teardown.
        }
        super.onStop();
    }
}
