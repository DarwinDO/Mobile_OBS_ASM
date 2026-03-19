package com.example.mobile_obs_asm;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mobile_obs_asm.data.AuthRepository;
import com.example.mobile_obs_asm.data.RepositoryCallback;
import com.example.mobile_obs_asm.ui.common.SectionStateController;
import com.example.mobile_obs_asm.util.SystemBarInsetsHelper;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class RegisterActivity extends AppCompatActivity {

    private static final String ROLE_BUYER = "buyer";
    private static final String ROLE_SELLER = "seller";

    private TextInputLayout layoutFullName;
    private TextInputLayout layoutEmail;
    private TextInputLayout layoutPassword;
    private TextInputLayout layoutConfirmPassword;
    private TextInputEditText inputFullName;
    private TextInputEditText inputEmail;
    private TextInputEditText inputPhone;
    private TextInputEditText inputPassword;
    private TextInputEditText inputConfirmPassword;
    private MaterialAutoCompleteTextView inputRole;
    private MaterialButton buttonRegister;
    private MaterialButton buttonBackToLogin;
    private SectionStateController stateController;

    public static Intent createIntent(Context context) {
        return new Intent(context, RegisterActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        SystemBarInsetsHelper.applyToRoot(findViewById(R.id.registerRoot));

        MaterialToolbar toolbar = findViewById(R.id.toolbarRegister);
        toolbar.setNavigationOnClickListener(view -> finish());
        toolbar.setTitle("");

        layoutFullName = findViewById(R.id.layoutRegisterName);
        layoutEmail = findViewById(R.id.layoutRegisterEmail);
        layoutPassword = findViewById(R.id.layoutRegisterPassword);
        layoutConfirmPassword = findViewById(R.id.layoutRegisterConfirmPassword);
        inputFullName = findViewById(R.id.inputRegisterName);
        inputEmail = findViewById(R.id.inputRegisterEmail);
        inputPhone = findViewById(R.id.inputRegisterPhone);
        inputPassword = findViewById(R.id.inputRegisterPassword);
        inputConfirmPassword = findViewById(R.id.inputRegisterConfirmPassword);
        inputRole = findViewById(R.id.inputRegisterRole);
        buttonRegister = findViewById(R.id.buttonSubmitRegister);
        buttonBackToLogin = findViewById(R.id.buttonBackToLogin);
        stateController = new SectionStateController(findViewById(android.R.id.content), R.id.layoutRegisterState);

        setupRoleDropdown();
        buttonRegister.setOnClickListener(view -> handleRegister());
        buttonBackToLogin.setOnClickListener(view -> {
            openLogin(readText(inputEmail));
            finish();
        });
    }

    private void setupRoleDropdown() {
        ArrayAdapter<String> roleAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                new String[]{
                        getString(R.string.register_role_buyer),
                        getString(R.string.register_role_seller)
                }
        );
        inputRole.setAdapter(roleAdapter);
        inputRole.setText(roleAdapter.getItem(0), false);
    }

    private void handleRegister() {
        clearErrors();
        stateController.hide();

        String fullName = readText(inputFullName);
        String email = readText(inputEmail);
        String phone = normalizeOptional(readText(inputPhone));
        String password = readText(inputPassword);
        String confirmPassword = readText(inputConfirmPassword);

        if (fullName.isEmpty()) {
            layoutFullName.setError(getString(R.string.register_invalid_name));
            return;
        }

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            layoutEmail.setError(getString(R.string.register_invalid_email));
            return;
        }

        if (password.length() < 8) {
            layoutPassword.setError(getString(R.string.register_invalid_password));
            return;
        }

        if (confirmPassword.isEmpty()) {
            layoutConfirmPassword.setError(getString(R.string.register_invalid_confirm_password));
            return;
        }

        if (!password.equals(confirmPassword)) {
            layoutConfirmPassword.setError(getString(R.string.register_password_not_match));
            return;
        }

        String[] nameParts = splitName(fullName);
        String role = getSelectedRole();

        setSubmitting(true);
        new AuthRepository(this).register(
                email,
                password,
                nameParts[0],
                nameParts[1],
                phone,
                role,
                new RepositoryCallback<String>() {
                    @Override
                    public void onSuccess(String value) {
                        if (isFinishing()) {
                            return;
                        }
                        setSubmitting(false);
                        Toast.makeText(
                                RegisterActivity.this,
                                value == null || value.isEmpty() ? getString(R.string.register_success) : value,
                                Toast.LENGTH_LONG
                        ).show();
                        openLogin(email);
                        finish();
                    }

                    @Override
                    public void onError(String message, Throwable throwable) {
                        if (isFinishing()) {
                            return;
                        }
                        setSubmitting(false);
                        stateController.showMessage(
                                getString(R.string.register_error_title),
                                message,
                                getString(R.string.state_action_retry),
                                retryView -> handleRegister()
                        );
                    }
                }
        );
    }

    private void openLogin(String email) {
        startActivity(LoginActivity.createIntent(this, email));
    }

    private void clearErrors() {
        layoutFullName.setError(null);
        layoutEmail.setError(null);
        layoutPassword.setError(null);
        layoutConfirmPassword.setError(null);
    }

    private void setSubmitting(boolean isSubmitting) {
        buttonRegister.setEnabled(!isSubmitting);
        buttonBackToLogin.setEnabled(!isSubmitting);
        inputFullName.setEnabled(!isSubmitting);
        inputEmail.setEnabled(!isSubmitting);
        inputPhone.setEnabled(!isSubmitting);
        inputPassword.setEnabled(!isSubmitting);
        inputConfirmPassword.setEnabled(!isSubmitting);
        inputRole.setEnabled(!isSubmitting);
        buttonRegister.setText(isSubmitting ? R.string.register_submit_loading : R.string.register_primary_action);

        if (isSubmitting) {
            stateController.showLoading(
                    getString(R.string.register_loading_title),
                    getString(R.string.register_loading_message)
            );
        } else {
            stateController.hide();
        }
    }

    private String getSelectedRole() {
        String roleLabel = readText(inputRole);
        return roleLabel.equals(getString(R.string.register_role_seller)) ? ROLE_SELLER : ROLE_BUYER;
    }

    private String[] splitName(String fullName) {
        String normalized = fullName.trim().replaceAll("\\s+", " ");
        int lastSpace = normalized.lastIndexOf(' ');
        if (lastSpace < 0) {
            return new String[]{normalized, ""};
        }
        return new String[]{
                normalized.substring(0, lastSpace).trim(),
                normalized.substring(lastSpace + 1).trim()
        };
    }

    private String normalizeOptional(String value) {
        return value == null || value.isEmpty() ? null : value;
    }

    private String readText(TextView textView) {
        return textView.getText() == null ? "" : textView.getText().toString().trim();
    }
}
