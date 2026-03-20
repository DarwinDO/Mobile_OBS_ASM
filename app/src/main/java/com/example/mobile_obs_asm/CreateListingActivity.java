package com.example.mobile_obs_asm;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

import com.example.mobile_obs_asm.data.ReferenceDataRemoteRepository;
import com.example.mobile_obs_asm.data.RepositoryCallback;
import com.example.mobile_obs_asm.data.SellerProductRemoteRepository;
import com.example.mobile_obs_asm.data.SessionManager;
import com.example.mobile_obs_asm.model.CreateListingDraft;
import com.example.mobile_obs_asm.model.CreateListingFormOptions;
import com.example.mobile_obs_asm.model.ReferenceOption;
import com.example.mobile_obs_asm.model.SellerListing;
import com.example.mobile_obs_asm.model.SellerListingEditorData;
import com.example.mobile_obs_asm.ui.common.SectionStateController;
import com.example.mobile_obs_asm.util.SystemBarInsetsHelper;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CreateListingActivity extends SessionAwareActivity {

    private static final String EXTRA_EDIT_LISTING_ID = "extra_edit_listing_id";

    private TextInputLayout layoutTitle;
    private TextInputLayout layoutPrice;
    private TextInputLayout layoutProvince;
    private TextInputLayout layoutFrameSize;
    private TextInputLayout layoutWheelSize;
    private TextInputLayout layoutBrakeType;
    private TextInputLayout layoutFrameMaterial;
    private TextInputLayout layoutImages;
    private TextInputEditText inputTitle;
    private TextInputEditText inputDescription;
    private TextInputEditText inputPrice;
    private TextInputEditText inputProvince;
    private TextInputEditText inputDistrict;
    private TextInputEditText inputFrameSize;
    private TextInputEditText inputWheelSize;
    private TextInputEditText inputGroupset;
    private MaterialAutoCompleteTextView inputCondition;
    private MaterialAutoCompleteTextView inputBrakeType;
    private MaterialAutoCompleteTextView inputFrameMaterial;
    private TextView textCreateListingBadge;
    private TextView textCreateListingTitle;
    private TextView textCreateListingSubtitle;
    private TextView textCreateListingFormTitle;
    private TextView textSelectedImages;
    private MaterialButton buttonPickImages;
    private MaterialButton buttonSubmit;
    private SectionStateController stateController;

    private final List<ReferenceOption> conditionOptions = Arrays.asList(
            new ReferenceOption("new_90", "Mới 90%"),
            new ReferenceOption("used", "Đã qua sử dụng"),
            new ReferenceOption("needs_repair", "Cần sửa chữa")
    );
    private List<ReferenceOption> brakeTypeOptions = new ArrayList<>();
    private List<ReferenceOption> frameMaterialOptions = new ArrayList<>();
    private final List<Uri> selectedImageUris = new ArrayList<>();

    private SellerProductRemoteRepository sellerProductRemoteRepository;
    private ReferenceDataRemoteRepository referenceDataRemoteRepository;
    private boolean editMode;
    private String editingListingId;
    private SellerListingEditorData existingListingData;

    private final ActivityResultLauncher<String> imagePickerLauncher =
            registerForActivityResult(new ActivityResultContracts.GetMultipleContents(), uris -> {
                selectedImageUris.clear();
                if (uris != null) {
                    selectedImageUris.addAll(uris);
                }
                layoutImages.setError(null);
                renderSelectedImages();
            });

    public static Intent createIntent(Context context) {
        return new Intent(context, CreateListingActivity.class);
    }

    public static Intent createEditIntent(Context context, String listingId) {
        Intent intent = new Intent(context, CreateListingActivity.class);
        intent.putExtra(EXTRA_EDIT_LISTING_ID, listingId);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!SessionManager.getInstance(this).isSellerSession()) {
            Toast.makeText(this, R.string.create_listing_requires_seller, Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        editMode = getIntent().hasExtra(EXTRA_EDIT_LISTING_ID);
        editingListingId = getIntent().getStringExtra(EXTRA_EDIT_LISTING_ID);
        sellerProductRemoteRepository = new SellerProductRemoteRepository(this);
        referenceDataRemoteRepository = new ReferenceDataRemoteRepository(this);

        setContentView(R.layout.activity_create_listing);
        SystemBarInsetsHelper.applyToRoot(findViewById(R.id.createListingRoot));

        MaterialToolbar toolbar = findViewById(R.id.toolbarCreateListing);
        toolbar.setNavigationOnClickListener(v -> finish());
        toolbar.setTitle("");

        bindViews();
        applyModeCopy();
        setupStaticConditionDropdown();
        renderSelectedImages();

        buttonPickImages.setOnClickListener(v -> imagePickerLauncher.launch("image/*"));
        buttonSubmit.setOnClickListener(v -> submitListing());

        loadReferenceData();
    }

    private void bindViews() {
        layoutTitle = findViewById(R.id.layoutListingTitle);
        layoutPrice = findViewById(R.id.layoutListingPrice);
        layoutProvince = findViewById(R.id.layoutListingProvince);
        layoutFrameSize = findViewById(R.id.layoutListingFrameSize);
        layoutWheelSize = findViewById(R.id.layoutListingWheelSize);
        layoutBrakeType = findViewById(R.id.layoutListingBrakeType);
        layoutFrameMaterial = findViewById(R.id.layoutListingFrameMaterial);
        layoutImages = findViewById(R.id.layoutListingImages);
        inputTitle = findViewById(R.id.inputListingTitle);
        inputDescription = findViewById(R.id.inputListingDescription);
        inputPrice = findViewById(R.id.inputListingPrice);
        inputProvince = findViewById(R.id.inputListingProvince);
        inputDistrict = findViewById(R.id.inputListingDistrict);
        inputFrameSize = findViewById(R.id.inputListingFrameSize);
        inputWheelSize = findViewById(R.id.inputListingWheelSize);
        inputGroupset = findViewById(R.id.inputListingGroupset);
        inputCondition = findViewById(R.id.inputListingCondition);
        inputBrakeType = findViewById(R.id.inputListingBrakeType);
        inputFrameMaterial = findViewById(R.id.inputListingFrameMaterial);
        textCreateListingBadge = findViewById(R.id.textCreateListingBadge);
        textCreateListingTitle = findViewById(R.id.textCreateListingTitle);
        textCreateListingSubtitle = findViewById(R.id.textCreateListingSubtitle);
        textCreateListingFormTitle = findViewById(R.id.textCreateListingFormTitle);
        textSelectedImages = findViewById(R.id.textSelectedImages);
        buttonPickImages = findViewById(R.id.buttonPickListingImages);
        buttonSubmit = findViewById(R.id.buttonSubmitListing);
        stateController = new SectionStateController(findViewById(android.R.id.content), R.id.layoutCreateListingState);
    }

    private void applyModeCopy() {
        textCreateListingBadge.setText(editMode ? R.string.edit_listing_badge : R.string.create_listing_badge);
        textCreateListingTitle.setText(editMode ? R.string.edit_listing_title : R.string.create_listing_title);
        textCreateListingSubtitle.setText(editMode ? R.string.edit_listing_subtitle : R.string.create_listing_subtitle);
        textCreateListingFormTitle.setText(editMode ? R.string.edit_listing_form_title : R.string.create_listing_form_title);
        buttonPickImages.setText(editMode ? R.string.edit_listing_pick_images : R.string.create_listing_pick_images);
        buttonSubmit.setText(editMode ? R.string.edit_listing_submit : R.string.create_listing_submit);
    }

    private void setupStaticConditionDropdown() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                buildLabels(conditionOptions)
        );
        inputCondition.setAdapter(adapter);
        inputCondition.setText(adapter.getItem(1), false);
    }

    private void loadReferenceData() {
        setLoadingState(true, getString(R.string.create_listing_loading_title), getString(R.string.create_listing_loading_message));
        referenceDataRemoteRepository.fetchCreateListingOptions(new RepositoryCallback<CreateListingFormOptions>() {
            @Override
            public void onSuccess(CreateListingFormOptions value) {
                if (isFinishing()) {
                    return;
                }
                brakeTypeOptions = value.getBrakeTypes();
                frameMaterialOptions = value.getFrameMaterials();
                bindReferenceDropdown(inputBrakeType, brakeTypeOptions);
                bindReferenceDropdown(inputFrameMaterial, frameMaterialOptions);
                if (editMode) {
                    loadExistingListing();
                    return;
                }
                setLoadingState(false, null, null);
            }

            @Override
            public void onError(String message, Throwable throwable) {
                if (isFinishing()) {
                    return;
                }
                stateController.showMessage(
                        getString(R.string.create_listing_lookup_error_title),
                        message,
                        getString(R.string.state_action_retry),
                        actionView -> loadReferenceData()
                );
                buttonSubmit.setEnabled(false);
            }
        });
    }

    private void loadExistingListing() {
        if (editingListingId == null || editingListingId.trim().isEmpty()) {
            Toast.makeText(this, R.string.edit_listing_missing_id, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        stateController.showLoading(
                getString(R.string.edit_listing_loading_title),
                getString(R.string.edit_listing_loading_message)
        );

        sellerProductRemoteRepository.fetchMyProductDetail(editingListingId, new RepositoryCallback<SellerListingEditorData>() {
            @Override
            public void onSuccess(SellerListingEditorData value) {
                if (isFinishing()) {
                    return;
                }
                existingListingData = value;
                populateForm(value);
                setLoadingState(false, null, null);
            }

            @Override
            public void onError(String message, Throwable throwable) {
                if (isFinishing()) {
                    return;
                }
                stateController.showMessage(
                        getString(R.string.edit_listing_error_title),
                        message,
                        getString(R.string.state_action_retry),
                        actionView -> loadExistingListing()
                );
            }
        });
    }

    private void populateForm(SellerListingEditorData data) {
        inputTitle.setText(data.getTitle());
        inputDescription.setText(data.getDescription());
        inputPrice.setText(String.valueOf(data.getPrice()));
        inputProvince.setText(data.getProvince());
        inputDistrict.setText(data.getDistrict());
        inputFrameSize.setText(data.getFrameSize());
        inputWheelSize.setText(data.getWheelSize());
        inputGroupset.setText(data.getGroupset());
        inputCondition.setText(resolveLabelById(conditionOptions, data.getConditionKey()), false);
        inputBrakeType.setText(resolveClosestLabel(brakeTypeOptions, data.getBrakeTypeLabel()), false);
        inputFrameMaterial.setText(resolveClosestLabel(frameMaterialOptions, data.getFrameMaterialLabel()), false);
        renderSelectedImages();
    }

    private void bindReferenceDropdown(MaterialAutoCompleteTextView inputView, List<ReferenceOption> options) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                buildLabels(options)
        );
        inputView.setAdapter(adapter);
        if (!options.isEmpty()) {
            inputView.setText(options.get(0).getLabel(), false);
        }
    }

    private String[] buildLabels(List<ReferenceOption> options) {
        String[] labels = new String[options.size()];
        for (int index = 0; index < options.size(); index++) {
            labels[index] = options.get(index).getLabel();
        }
        return labels;
    }

    private void renderSelectedImages() {
        if (selectedImageUris.isEmpty()) {
            if (editMode && existingListingData != null) {
                textSelectedImages.setText(getString(
                        R.string.edit_listing_images_keep_current,
                        existingListingData.getCurrentImageCount()
                ));
                return;
            }
            textSelectedImages.setText(R.string.create_listing_images_empty);
            return;
        }

        StringBuilder builder = new StringBuilder();
        builder.append(getString(R.string.create_listing_images_count, selectedImageUris.size()));
        int previewCount = Math.min(3, selectedImageUris.size());
        for (int index = 0; index < previewCount; index++) {
            builder.append('\n')
                    .append("• ")
                    .append(getString(R.string.create_listing_images_item, index + 1));
        }
        if (selectedImageUris.size() > previewCount) {
            builder.append('\n')
                    .append(getString(R.string.create_listing_images_more, selectedImageUris.size() - previewCount));
        }
        if (editMode) {
            builder.append('\n').append(getString(R.string.edit_listing_images_replace_note));
        }
        textSelectedImages.setText(builder.toString());
    }

    private void submitListing() {
        clearErrors();
        CreateListingDraft draft = buildValidatedDraft();
        if (draft == null) {
            return;
        }

        setSubmitting(true);
        RepositoryCallback<SellerListing> callback = new RepositoryCallback<SellerListing>() {
            @Override
            public void onSuccess(SellerListing value) {
                if (isFinishing()) {
                    return;
                }
                setSubmitting(false);
                Toast.makeText(
                        CreateListingActivity.this,
                        editMode ? R.string.edit_listing_success : R.string.create_listing_success,
                        Toast.LENGTH_SHORT
                ).show();
                startActivity(MainActivity.createIntent(
                        CreateListingActivity.this,
                        R.id.navigation_wishlist
                ));
                finish();
            }

            @Override
            public void onError(String message, Throwable throwable) {
                if (isFinishing()) {
                    return;
                }
                setSubmitting(false);
                stateController.showMessage(
                        getString(editMode ? R.string.edit_listing_error_title : R.string.create_listing_submit_error_title),
                        message,
                        getString(R.string.state_action_retry),
                        actionView -> submitListing()
                );
            }
        };

        if (editMode) {
            sellerProductRemoteRepository.updateProduct(editingListingId, draft, callback);
            return;
        }
        sellerProductRemoteRepository.createProduct(draft, callback);
    }

    private CreateListingDraft buildValidatedDraft() {
        String title = readText(inputTitle);
        String description = readText(inputDescription);
        String priceText = readText(inputPrice);
        String province = readText(inputProvince);
        String district = readText(inputDistrict);
        String frameSize = readText(inputFrameSize);
        String wheelSize = readText(inputWheelSize);
        String groupset = readText(inputGroupset);
        String conditionId = resolveSelectedId(conditionOptions, readText(inputCondition));
        String brakeTypeId = resolveSelectedId(brakeTypeOptions, readText(inputBrakeType));
        String frameMaterialId = resolveSelectedId(frameMaterialOptions, readText(inputFrameMaterial));

        if (title.isEmpty()) {
            layoutTitle.setError(getString(R.string.create_listing_error_title_required));
            return null;
        }
        if (priceText.isEmpty()) {
            layoutPrice.setError(getString(R.string.create_listing_error_price_required));
            return null;
        }

        long price;
        try {
            price = Long.parseLong(priceText);
        } catch (NumberFormatException exception) {
            layoutPrice.setError(getString(R.string.create_listing_error_price_invalid));
            return null;
        }
        if (price <= 0L) {
            layoutPrice.setError(getString(R.string.create_listing_error_price_invalid));
            return null;
        }

        if (province.isEmpty()) {
            layoutProvince.setError(getString(R.string.create_listing_error_province_required));
            return null;
        }
        if (frameSize.isEmpty()) {
            layoutFrameSize.setError(getString(R.string.create_listing_error_frame_required));
            return null;
        }
        if (wheelSize.isEmpty()) {
            layoutWheelSize.setError(getString(R.string.create_listing_error_wheel_required));
            return null;
        }
        if (conditionId == null) {
            Toast.makeText(this, R.string.create_listing_error_condition_required, Toast.LENGTH_SHORT).show();
            return null;
        }
        if (brakeTypeId == null) {
            layoutBrakeType.setError(getString(R.string.create_listing_error_brake_required));
            return null;
        }
        if (frameMaterialId == null) {
            layoutFrameMaterial.setError(getString(R.string.create_listing_error_frame_material_required));
            return null;
        }
        if (!editMode && selectedImageUris.size() < 3) {
            layoutImages.setError(getString(R.string.create_listing_error_images_required));
            return null;
        }
        if (editMode && !selectedImageUris.isEmpty() && selectedImageUris.size() < 3) {
            layoutImages.setError(getString(R.string.edit_listing_error_images_required));
            return null;
        }

        return new CreateListingDraft(
                title,
                description,
                price,
                brakeTypeId,
                frameMaterialId,
                frameSize,
                wheelSize,
                groupset,
                conditionId,
                province,
                district,
                selectedImageUris
        );
    }

    private void clearErrors() {
        layoutTitle.setError(null);
        layoutPrice.setError(null);
        layoutProvince.setError(null);
        layoutFrameSize.setError(null);
        layoutWheelSize.setError(null);
        layoutBrakeType.setError(null);
        layoutFrameMaterial.setError(null);
        layoutImages.setError(null);
        stateController.hide();
    }

    private String resolveSelectedId(List<ReferenceOption> options, String selectedLabel) {
        for (ReferenceOption option : options) {
            if (option.getLabel().equals(selectedLabel)) {
                return option.getId();
            }
        }
        return null;
    }

    private String resolveLabelById(List<ReferenceOption> options, String selectedId) {
        for (ReferenceOption option : options) {
            if (option.getId().equals(selectedId)) {
                return option.getLabel();
            }
        }
        return options.isEmpty() ? "" : options.get(0).getLabel();
    }

    private String resolveClosestLabel(List<ReferenceOption> options, String expectedLabel) {
        if (expectedLabel == null || expectedLabel.trim().isEmpty()) {
            return options.isEmpty() ? "" : options.get(0).getLabel();
        }
        for (ReferenceOption option : options) {
            if (option.getLabel().equalsIgnoreCase(expectedLabel.trim())) {
                return option.getLabel();
            }
        }
        return expectedLabel.trim();
    }

    private void setSubmitting(boolean isSubmitting) {
        buttonSubmit.setEnabled(!isSubmitting);
        buttonPickImages.setEnabled(!isSubmitting);
        if (isSubmitting) {
            stateController.showLoading(
                    getString(editMode ? R.string.edit_listing_submitting_title : R.string.create_listing_submitting_title),
                    getString(editMode ? R.string.edit_listing_submitting_message : R.string.create_listing_submitting_message)
            );
        } else {
            stateController.hide();
        }
        buttonSubmit.setText(isSubmitting
                ? (editMode ? R.string.edit_listing_submit_loading : R.string.create_listing_submit_loading)
                : (editMode ? R.string.edit_listing_submit : R.string.create_listing_submit));
    }

    private void setLoadingState(boolean isLoading, String title, String message) {
        if (isLoading) {
            stateController.showLoading(title, message);
            buttonSubmit.setEnabled(false);
            buttonPickImages.setEnabled(false);
            return;
        }

        stateController.hide();
        buttonSubmit.setEnabled(true);
        buttonPickImages.setEnabled(true);
    }

    private String readText(TextView view) {
        return view.getText() == null ? "" : view.getText().toString().trim();
    }
}
