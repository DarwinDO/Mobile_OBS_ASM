package com.example.mobile_obs_asm;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.IntentCompat;

import com.example.mobile_obs_asm.data.OrderRemoteRepository;
import com.example.mobile_obs_asm.data.RepositoryCallback;
import com.example.mobile_obs_asm.data.SessionManager;
import com.example.mobile_obs_asm.model.Product;
import com.example.mobile_obs_asm.network.order.RemoteOrderResponse;
import com.example.mobile_obs_asm.ui.common.SectionStateController;
import com.example.mobile_obs_asm.util.PriceFormatter;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.math.BigDecimal;

public class CreateOrderActivity extends AppCompatActivity {

    private static final String EXTRA_PRODUCT = "extra_product";

    private static final String PAYMENT_OPTION_PARTIAL = "partial";
    private static final String PAYMENT_OPTION_FULL = "full";
    private static final String PAYMENT_METHOD_TRANSFER = "transfer";
    private static final String PAYMENT_METHOD_CASH = "cash";
    private static final String PAYMENT_METHOD_ONLINE = "online";

    private final String[] paymentOptionValues = {PAYMENT_OPTION_PARTIAL, PAYMENT_OPTION_FULL};
    private final String[] paymentMethodValues = {PAYMENT_METHOD_TRANSFER, PAYMENT_METHOD_CASH, PAYMENT_METHOD_ONLINE};

    private Product product;
    private TextInputLayout layoutUpfrontAmount;
    private MaterialAutoCompleteTextView inputPaymentOption;
    private MaterialAutoCompleteTextView inputPaymentMethod;
    private TextInputEditText inputUpfrontAmount;
    private TextView textOrderSummary;
    private TextView textOrderHelper;
    private MaterialButton buttonSubmitOrder;
    private SectionStateController stateController;

    public static Intent createIntent(Context context, Product product) {
        Intent intent = new Intent(context, CreateOrderActivity.class);
        intent.putExtra(EXTRA_PRODUCT, product);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        product = IntentCompat.getParcelableExtra(getIntent(), EXTRA_PRODUCT, Product.class);
        if (product == null || !product.isRemoteSource()) {
            Toast.makeText(this, R.string.order_create_missing_product, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        if (!SessionManager.getInstance(this).hasActiveSession()) {
            Toast.makeText(this, R.string.order_create_requires_sign_in, Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        setContentView(R.layout.activity_create_order);

        MaterialToolbar toolbar = findViewById(R.id.toolbarCreateOrder);
        toolbar.setNavigationOnClickListener(view -> finish());
        toolbar.setTitle("");

        TextView textTitle = findViewById(R.id.textCreateOrderTitle);
        TextView textPrice = findViewById(R.id.textCreateOrderPrice);
        layoutUpfrontAmount = findViewById(R.id.layoutUpfrontAmount);
        inputPaymentOption = findViewById(R.id.inputPaymentOption);
        inputPaymentMethod = findViewById(R.id.inputPaymentMethod);
        inputUpfrontAmount = findViewById(R.id.inputUpfrontAmount);
        textOrderSummary = findViewById(R.id.textCreateOrderSummary);
        textOrderHelper = findViewById(R.id.textCreateOrderHelper);
        buttonSubmitOrder = findViewById(R.id.buttonSubmitOrder);
        stateController = new SectionStateController(findViewById(android.R.id.content), R.id.layoutCreateOrderState);

        textTitle.setText(product.getTitle());
        textPrice.setText(PriceFormatter.formatCurrency(product.getPrice()));

        setupDropdowns();
        updateAmountPresentation();

        buttonSubmitOrder.setOnClickListener(view -> submitOrder());
    }

    private void setupDropdowns() {
        ArrayAdapter<String> paymentOptionAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                new String[]{
                        getString(R.string.order_create_option_partial),
                        getString(R.string.order_create_option_full)
                }
        );
        inputPaymentOption.setAdapter(paymentOptionAdapter);
        inputPaymentOption.setText(paymentOptionAdapter.getItem(0), false);
        inputPaymentOption.setOnItemClickListener((parent, view, position, id) -> updateAmountPresentation());

        ArrayAdapter<String> paymentMethodAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                new String[]{
                        getString(R.string.order_create_method_transfer),
                        getString(R.string.order_create_method_cash),
                        getString(R.string.order_create_method_online)
                }
        );
        inputPaymentMethod.setAdapter(paymentMethodAdapter);
        inputPaymentMethod.setText(paymentMethodAdapter.getItem(0), false);

        inputUpfrontAmount.setText(String.valueOf(suggestedPartialAmount()));
    }

    private void updateAmountPresentation() {
        boolean isFullPayment = PAYMENT_OPTION_FULL.equals(getSelectedPaymentOption());
        layoutUpfrontAmount.setVisibility(isFullPayment ? View.GONE : View.VISIBLE);
        layoutUpfrontAmount.setError(null);

        if (isFullPayment) {
            textOrderSummary.setText(getString(
                    R.string.order_create_summary_full,
                    PriceFormatter.formatCurrency(product.getPrice())
            ));
            textOrderHelper.setText(getString(
                    R.string.order_create_helper_full,
                    PriceFormatter.formatCurrency(product.getPrice())
            ));
            return;
        }

        long suggestedAmount = suggestedPartialAmount();
        if (readText(inputUpfrontAmount).isEmpty()) {
            inputUpfrontAmount.setText(String.valueOf(suggestedAmount));
        }
        textOrderSummary.setText(getString(
                R.string.order_create_summary_partial,
                PriceFormatter.formatCurrency(suggestedAmount),
                PriceFormatter.formatCurrency(product.getPrice())
        ));
        textOrderHelper.setText(getString(
                R.string.order_create_helper_partial,
                PriceFormatter.formatCurrency(suggestedAmount)
        ));
    }

    private void submitOrder() {
        stateController.hide();
        layoutUpfrontAmount.setError(null);

        String paymentOption = getSelectedPaymentOption();
        String paymentMethod = getSelectedPaymentMethod();
        BigDecimal upfrontAmount = null;

        if (PAYMENT_OPTION_PARTIAL.equals(paymentOption)) {
            String rawAmount = readText(inputUpfrontAmount);
            if (rawAmount.isEmpty()) {
                layoutUpfrontAmount.setError(getString(R.string.order_create_invalid_amount_empty));
                return;
            }

            try {
                long parsedAmount = Long.parseLong(rawAmount);
                if (parsedAmount <= 0) {
                    layoutUpfrontAmount.setError(getString(R.string.order_create_invalid_amount_positive));
                    return;
                }
                if (parsedAmount > product.getPrice()) {
                    layoutUpfrontAmount.setError(getString(R.string.order_create_invalid_amount_too_large));
                    return;
                }
                upfrontAmount = BigDecimal.valueOf(parsedAmount);
            } catch (NumberFormatException exception) {
                layoutUpfrontAmount.setError(getString(R.string.order_create_invalid_amount_format));
                return;
            }
        }

        setSubmitting(true);
        new OrderRemoteRepository(this).createOrder(
                product.getId(),
                paymentOption,
                paymentMethod,
                upfrontAmount,
                new RepositoryCallback<RemoteOrderResponse>() {
                    @Override
                    public void onSuccess(RemoteOrderResponse value) {
                        if (isFinishing()) {
                            return;
                        }
                        setSubmitting(false);
                        Toast.makeText(CreateOrderActivity.this, R.string.order_create_success, Toast.LENGTH_SHORT).show();
                        startActivity(MainActivity.createIntent(CreateOrderActivity.this, R.id.navigation_orders));
                        finish();
                    }

                    @Override
                    public void onError(String message, Throwable throwable) {
                        if (isFinishing()) {
                            return;
                        }
                        setSubmitting(false);
                        stateController.showMessage(
                                getString(R.string.order_create_error_title),
                                message,
                                getString(R.string.state_action_retry),
                                retryView -> submitOrder()
                        );
                    }
                }
        );
    }

    private void setSubmitting(boolean isSubmitting) {
        buttonSubmitOrder.setEnabled(!isSubmitting);
        inputPaymentOption.setEnabled(!isSubmitting);
        inputPaymentMethod.setEnabled(!isSubmitting);
        inputUpfrontAmount.setEnabled(!isSubmitting);
        buttonSubmitOrder.setText(isSubmitting ? R.string.order_create_submit_loading : R.string.order_create_submit);

        if (isSubmitting) {
            stateController.showLoading(
                    getString(R.string.order_create_loading_title),
                    getString(R.string.order_create_loading_message)
            );
        } else {
            stateController.hide();
        }
    }

    private String getSelectedPaymentOption() {
        int selectedIndex = inputPaymentOption.getListSelection();
        if (selectedIndex >= 0 && selectedIndex < paymentOptionValues.length) {
            return paymentOptionValues[selectedIndex];
        }

        String selectedLabel = readText(inputPaymentOption);
        if (selectedLabel.equals(getString(R.string.order_create_option_full))) {
            return PAYMENT_OPTION_FULL;
        }
        return PAYMENT_OPTION_PARTIAL;
    }

    private String getSelectedPaymentMethod() {
        String selectedLabel = readText(inputPaymentMethod);
        if (selectedLabel.equals(getString(R.string.order_create_method_cash))) {
            return PAYMENT_METHOD_CASH;
        }
        if (selectedLabel.equals(getString(R.string.order_create_method_online))) {
            return PAYMENT_METHOD_ONLINE;
        }
        return PAYMENT_METHOD_TRANSFER;
    }

    private long suggestedPartialAmount() {
        long computed = product.getPrice() / 5L;
        if (computed <= 0L) {
            return product.getPrice();
        }
        return Math.min(product.getPrice(), computed);
    }

    private String readText(TextView textView) {
        return textView.getText() == null ? "" : textView.getText().toString().trim();
    }
}
