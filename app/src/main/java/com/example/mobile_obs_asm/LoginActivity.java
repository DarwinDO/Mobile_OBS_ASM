package com.example.mobile_obs_asm;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mobile_obs_asm.data.AuthRepository;
import com.example.mobile_obs_asm.data.RepositoryCallback;
import com.example.mobile_obs_asm.data.SessionManager;
import com.example.mobile_obs_asm.network.auth.RemoteAuthResponse;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class LoginActivity extends AppCompatActivity {

    private TextInputLayout layoutEmail;
    private TextInputLayout layoutPassword;
    private TextInputEditText inputEmail;
    private TextInputEditText inputPassword;
    private MaterialButton buttonLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (SessionManager.getInstance(this).hasActiveSession()) {
            startActivity(MainActivity.createIntent(this, R.id.navigation_home));
            finish();
            return;
        }

        setContentView(R.layout.activity_login);

        layoutEmail = findViewById(R.id.layoutEmail);
        layoutPassword = findViewById(R.id.layoutPassword);
        inputEmail = findViewById(R.id.inputEmail);
        inputPassword = findViewById(R.id.inputPassword);
        buttonLogin = findViewById(R.id.buttonLogin);
        MaterialButton buttonOpenDemo = findViewById(R.id.buttonOpenDemo);

        inputEmail.setText("buyer@oldbicycles.vn");
        inputPassword.setText("password123");

        buttonLogin.setOnClickListener(view -> handleLogin());
        buttonOpenDemo.setOnClickListener(view -> openDemo());
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

        buttonLogin.setEnabled(false);
        new AuthRepository(this).login(email, password, new RepositoryCallback<RemoteAuthResponse>() {
            @Override
            public void onSuccess(RemoteAuthResponse value) {
                if (isFinishing()) {
                    return;
                }
                buttonLogin.setEnabled(true);
                Toast.makeText(LoginActivity.this, R.string.login_backend_success, Toast.LENGTH_SHORT).show();
                startActivity(MainActivity.createIntent(LoginActivity.this, R.id.navigation_home));
                finish();
            }

            @Override
            public void onError(String message, Throwable throwable) {
                if (isFinishing()) {
                    return;
                }
                buttonLogin.setEnabled(true);
                int toastMessage = throwable == null ? R.string.login_backend_error : R.string.login_backend_network_error;
                Toast.makeText(LoginActivity.this, toastMessage, Toast.LENGTH_LONG).show();
            }
        });
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
