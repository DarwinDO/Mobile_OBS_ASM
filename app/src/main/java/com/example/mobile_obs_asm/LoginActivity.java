package com.example.mobile_obs_asm;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import androidx.annotation.Nullable;

import com.example.mobile_obs_asm.data.AuthRepository;
import com.example.mobile_obs_asm.data.RepositoryCallback;
import com.example.mobile_obs_asm.data.SessionManager;
import com.example.mobile_obs_asm.network.auth.RemoteAuthResponse;
import com.example.mobile_obs_asm.util.SystemBarInsetsHelper;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class LoginActivity extends AppCompatActivity {

    private static final String EXTRA_PREFILL_EMAIL = "extra_prefill_email";

    private TextInputLayout layoutEmail;
    private TextInputLayout layoutPassword;
    private TextInputEditText inputEmail;
    private TextInputEditText inputPassword;
    private MaterialButton buttonLogin;
    private MaterialButton buttonRegister;
    private MaterialButton buttonOpenDemo;

    public static android.content.Intent createIntent(android.content.Context context, @Nullable String prefillEmail) {
        android.content.Intent intent = new android.content.Intent(context, LoginActivity.class);
        if (prefillEmail != null && !prefillEmail.trim().isEmpty()) {
            intent.putExtra(EXTRA_PREFILL_EMAIL, prefillEmail.trim());
        }
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (SessionManager.getInstance(this).hasActiveSession()) {
            startActivity(MainActivity.createIntent(this, MainActivity.defaultStartDestination(this)));
            finish();
            return;
        }

        setContentView(R.layout.activity_login);
        SystemBarInsetsHelper.applyToRoot(findViewById(R.id.loginRoot));

        layoutEmail = findViewById(R.id.layoutEmail);
        layoutPassword = findViewById(R.id.layoutPassword);
        inputEmail = findViewById(R.id.inputEmail);
        inputPassword = findViewById(R.id.inputPassword);
        buttonLogin = findViewById(R.id.buttonLogin);
        buttonRegister = findViewById(R.id.buttonRegister);
        buttonOpenDemo = findViewById(R.id.buttonOpenDemo);

        prefillEmailIfNeeded();
        buttonLogin.setOnClickListener(view -> handleLogin());
        buttonRegister.setOnClickListener(view -> openRegister());
        buttonOpenDemo.setOnClickListener(view -> openDemo());
    }

    private void prefillEmailIfNeeded() {
        String prefilledEmail = getIntent().getStringExtra(EXTRA_PREFILL_EMAIL);
        if (prefilledEmail != null && !prefilledEmail.trim().isEmpty()) {
            inputEmail.setText(prefilledEmail.trim());
            inputPassword.requestFocus();
        }
    }

    private void handleLogin() {
        String email = readText(inputEmail);
        String password = readText(inputPassword);

        layoutEmail.setError(null);
        layoutPassword.setError(null);

        if (email.isEmpty()) {
            layoutEmail.setError(getString(R.string.login_invalid_email));
            return;
        }

        if (password.isEmpty()) {
            layoutPassword.setError(getString(R.string.login_invalid_password));
            return;
        }

        setAuthActionLoading(true);
        new AuthRepository(this).login(email, password, new RepositoryCallback<RemoteAuthResponse>() {
            @Override
            public void onSuccess(RemoteAuthResponse value) {
                if (isFinishing()) {
                    return;
                }
                setAuthActionLoading(false);
                Toast.makeText(LoginActivity.this, R.string.login_backend_success, Toast.LENGTH_SHORT).show();
                startActivity(MainActivity.createIntent(
                        LoginActivity.this,
                        MainActivity.defaultStartDestination(LoginActivity.this)
                ));
                finish();
            }

            @Override
            public void onError(String message, Throwable throwable) {
                if (isFinishing()) {
                    return;
                }
                setAuthActionLoading(false);
                String fallbackMessage = getString(
                        throwable == null ? R.string.login_backend_error : R.string.login_backend_network_error
                );
                Toast.makeText(LoginActivity.this, message == null || message.isEmpty() ? fallbackMessage : message, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void setAuthActionLoading(boolean isLoading) {
        buttonLogin.setEnabled(!isLoading);
        buttonRegister.setEnabled(!isLoading);
        buttonOpenDemo.setEnabled(!isLoading);
    }

    private void openRegister() {
        startActivity(RegisterActivity.createIntent(this));
    }

    private void openDemo() {
        Toast.makeText(this, R.string.login_success, Toast.LENGTH_SHORT).show();
        startActivity(MainActivity.createIntent(this, R.id.navigation_home));
        finish();
    }

    private String readText(TextInputEditText editText) {
        if (editText.getText() == null) {
            return "";
        }
        return editText.getText().toString().trim();
    }
}
