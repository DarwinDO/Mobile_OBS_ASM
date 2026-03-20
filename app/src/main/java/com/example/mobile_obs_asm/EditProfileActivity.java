package com.example.mobile_obs_asm;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mobile_obs_asm.data.AuthRepository;
import com.example.mobile_obs_asm.data.RepositoryCallback;
import com.example.mobile_obs_asm.data.SessionManager;
import com.example.mobile_obs_asm.network.auth.RemoteAuthResponse;
import com.example.mobile_obs_asm.ui.common.SectionStateController;
import com.example.mobile_obs_asm.util.SystemBarInsetsHelper;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class EditProfileActivity extends SessionAwareActivity {

    public static Intent createIntent(Context context) {
        return new Intent(context, EditProfileActivity.class);
    }

    private TextInputLayout layoutFirstName;
    private TextInputLayout layoutLastName;
    private TextInputLayout layoutAddress;
    private TextInputEditText inputFirstName;
    private TextInputEditText inputLastName;
    private TextInputEditText inputPhone;
    private TextInputEditText inputAddress;
    private MaterialButton buttonSave;
    private MaterialButton buttonCancel;
    private SectionStateController stateController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        SystemBarInsetsHelper.applyToRoot(findViewById(R.id.editProfileRoot));

        MaterialToolbar toolbar = findViewById(R.id.toolbarEditProfile);
        toolbar.setNavigationOnClickListener(view -> finish());
        toolbar.setTitle("");

        layoutFirstName = findViewById(R.id.layoutEditFirstName);
        layoutLastName = findViewById(R.id.layoutEditLastName);
        layoutAddress = findViewById(R.id.layoutEditAddress);
        inputFirstName = findViewById(R.id.inputEditFirstName);
        inputLastName = findViewById(R.id.inputEditLastName);
        inputPhone = findViewById(R.id.inputEditPhone);
        inputAddress = findViewById(R.id.inputEditAddress);
        buttonSave = findViewById(R.id.buttonSaveProfile);
        buttonCancel = findViewById(R.id.buttonCancelProfile);
        stateController = new SectionStateController(findViewById(android.R.id.content), R.id.layoutEditProfileState);

        bindCurrentProfile();

        buttonSave.setOnClickListener(view -> handleSave());
        buttonCancel.setOnClickListener(view -> finish());
    }

    private void bindCurrentProfile() {
        SessionManager sessionManager = SessionManager.getInstance(this);
        ((TextView) findViewById(R.id.textEditProfileEmail)).setText(sessionManager.getStoredEmail());
        inputFirstName.setText(sessionManager.getStoredFirstName());
        inputLastName.setText(sessionManager.getStoredLastName());
        inputPhone.setText(sessionManager.getStoredPhone());
        inputAddress.setText(sessionManager.getStoredAddress());
    }

    private void handleSave() {
        clearErrors();
        stateController.hide();

        String firstName = readText(inputFirstName);
        String lastName = readText(inputLastName);
        String phone = normalizeOptional(readText(inputPhone));
        String address = normalizeOptional(readText(inputAddress));

        if (firstName.isEmpty()) {
            layoutFirstName.setError(getString(R.string.edit_profile_error_first_name));
            return;
        }

        if (lastName.isEmpty()) {
            layoutLastName.setError(getString(R.string.edit_profile_error_last_name));
            return;
        }

        if (address == null || address.isEmpty()) {
            layoutAddress.setError(getString(R.string.edit_profile_error_address));
            return;
        }

        setSubmitting(true);
        new AuthRepository(this).updateProfile(firstName, lastName, phone, address, new RepositoryCallback<RemoteAuthResponse.RemoteUserInfo>() {
            @Override
            public void onSuccess(RemoteAuthResponse.RemoteUserInfo value) {
                if (isFinishing()) {
                    return;
                }
                setSubmitting(false);
                Toast.makeText(EditProfileActivity.this, R.string.edit_profile_success, Toast.LENGTH_SHORT).show();
                setResult(RESULT_OK);
                finish();
            }

            @Override
            public void onError(String message, Throwable throwable) {
                if (isFinishing()) {
                    return;
                }
                setSubmitting(false);
                stateController.showMessage(
                        getString(R.string.edit_profile_error_title),
                        message,
                        getString(R.string.state_action_retry),
                        retryView -> handleSave()
                );
            }
        });
    }

    private void setSubmitting(boolean isSubmitting) {
        buttonSave.setEnabled(!isSubmitting);
        buttonCancel.setEnabled(!isSubmitting);
        inputFirstName.setEnabled(!isSubmitting);
        inputLastName.setEnabled(!isSubmitting);
        inputPhone.setEnabled(!isSubmitting);
        inputAddress.setEnabled(!isSubmitting);
        buttonSave.setText(isSubmitting ? R.string.edit_profile_submit_loading : R.string.edit_profile_save_action);

        if (isSubmitting) {
            stateController.showLoading(
                    getString(R.string.edit_profile_loading_title),
                    getString(R.string.edit_profile_loading_message)
            );
        } else {
            stateController.hide();
        }
    }

    private void clearErrors() {
        layoutFirstName.setError(null);
        layoutLastName.setError(null);
        layoutAddress.setError(null);
    }

    private String normalizeOptional(String value) {
        return value == null || value.trim().isEmpty() ? null : value.trim();
    }

    private String readText(TextInputEditText editText) {
        return editText.getText() == null ? "" : editText.getText().toString().trim();
    }
}
