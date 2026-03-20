package com.example.mobile_obs_asm;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.core.content.IntentCompat;

import com.example.mobile_obs_asm.data.OrderRemoteRepository;
import com.example.mobile_obs_asm.data.PaymentRemoteRepository;
import com.example.mobile_obs_asm.data.RepositoryCallback;
import com.example.mobile_obs_asm.data.SessionManager;
import com.example.mobile_obs_asm.model.OrderPreview;
import com.example.mobile_obs_asm.model.PaymentHistoryItem;
import com.example.mobile_obs_asm.model.PaymentRequestInfo;
import com.example.mobile_obs_asm.ui.common.SectionStateController;
import com.example.mobile_obs_asm.util.PriceFormatter;
import com.example.mobile_obs_asm.util.SystemBarInsetsHelper;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

import java.util.List;

public class OrderDetailActivity extends SessionAwareActivity {

    private static final String EXTRA_ORDER = "extra_order";

    private OrderPreview currentOrder;
    private boolean sellerSession;
    private boolean buyerSession;

    private SectionStateController stateController;
    private PaymentRemoteRepository paymentRemoteRepository;
    private OrderRemoteRepository orderRemoteRepository;
    private String activePaymentLink = "";
    private boolean allowResumeRefresh;

    private View cardPaymentRequest;
    private TextView textPaymentRequestSummary;
    private TextView textPaymentRequestTransfer;
    private TextView textPaymentRequestInstructions;
    private TextView textPaymentRequestExpires;
    private MaterialButton buttonPaymentOpenLink;
    private View cardPaymentHistory;
    private TextView textPaymentHistoryEmpty;
    private LinearLayout layoutPaymentHistoryList;
    private View cardOrderActions;
    private TextView textOrderActionHelper;
    private MaterialButton buttonPrimaryAction;
    private MaterialButton buttonSecondaryAction;

    public static Intent createIntent(Context context, OrderPreview order) {
        Intent intent = new Intent(context, OrderDetailActivity.class);
        intent.putExtra(EXTRA_ORDER, order);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);
        SystemBarInsetsHelper.applyToRoot(findViewById(R.id.orderDetailRoot));

        MaterialToolbar toolbar = findViewById(R.id.toolbarOrderDetail);
        toolbar.setNavigationOnClickListener(view -> finish());
        toolbar.setTitle("");

        currentOrder = IntentCompat.getParcelableExtra(getIntent(), EXTRA_ORDER, OrderPreview.class);
        if (currentOrder == null) {
            Toast.makeText(this, R.string.order_detail_missing_toast, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        SessionManager sessionManager = SessionManager.getInstance(this);
        sellerSession = sessionManager.isSellerSession();
        buyerSession = sessionManager.isBuyerSession();
        paymentRemoteRepository = new PaymentRemoteRepository(this);
        orderRemoteRepository = new OrderRemoteRepository(this);
        stateController = new SectionStateController(findViewById(android.R.id.content), R.id.layoutOrderDetailState);

        bindViews();
        bindOrder(currentOrder);
        bindAvailableActions();
        loadPaymentHistory();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (allowResumeRefresh
                && currentOrder != null
                && currentOrder.isRemoteSource()
                && SessionManager.getInstance(this).hasActiveSession()) {
            refreshOrderSnapshot(false);
        }
        allowResumeRefresh = true;
    }

    private void bindViews() {
        cardPaymentRequest = findViewById(R.id.cardOrderPaymentRequest);
        textPaymentRequestSummary = findViewById(R.id.textOrderPaymentRequestSummary);
        textPaymentRequestTransfer = findViewById(R.id.textOrderPaymentRequestTransfer);
        textPaymentRequestInstructions = findViewById(R.id.textOrderPaymentRequestInstructions);
        textPaymentRequestExpires = findViewById(R.id.textOrderPaymentRequestExpires);
        buttonPaymentOpenLink = findViewById(R.id.buttonOrderPaymentOpenLink);
        cardPaymentHistory = findViewById(R.id.cardOrderPaymentHistory);
        textPaymentHistoryEmpty = findViewById(R.id.textOrderPaymentHistoryEmpty);
        layoutPaymentHistoryList = findViewById(R.id.layoutOrderPaymentHistoryList);
        cardOrderActions = findViewById(R.id.cardOrderActions);
        textOrderActionHelper = findViewById(R.id.textOrderActionHelper);
        buttonPrimaryAction = findViewById(R.id.buttonOrderPrimaryAction);
        buttonSecondaryAction = findViewById(R.id.buttonOrderSecondaryAction);
        cardPaymentRequest.setVisibility(View.GONE);
        cardPaymentHistory.setVisibility(View.GONE);
        cardOrderActions.setVisibility(View.GONE);
    }

    private void bindOrder(OrderPreview order) {
        MaterialCardView heroCard = findViewById(R.id.cardOrderDetailHero);
        TextView textStatus = findViewById(R.id.textOrderDetailStatus);
        TextView textTitle = findViewById(R.id.textOrderDetailTitle);
        TextView textAmount = findViewById(R.id.textOrderDetailAmount);
        TextView textTimeline = findViewById(R.id.textOrderDetailTimeline);
        TextView textFunding = findViewById(R.id.textOrderDetailFunding);
        TextView textPaymentMethod = findViewById(R.id.textOrderDetailPaymentMethod);
        TextView textCreatedAt = findViewById(R.id.textOrderDetailCreatedAt);
        TextView textDeadline = findViewById(R.id.textOrderDetailDeadline);
        TextView textParties = findViewById(R.id.textOrderDetailParties);
        TextView textSummary = findViewById(R.id.textOrderDetailSummary);
        TextView textTotals = findViewById(R.id.textOrderDetailTotals);
        MaterialToolbar toolbar = findViewById(R.id.toolbarOrderDetail);

        heroCard.setCardBackgroundColor(ContextCompat.getColor(this, order.getStatusColorRes()));
        textStatus.setText(order.getStatus());
        textTitle.setText(order.getTitle());
        textAmount.setText(PriceFormatter.formatCurrency(order.getAmount()));
        textTimeline.setText(order.getTimeline());
        textFunding.setText(getString(R.string.order_detail_label_funding) + ": " + order.getFundingStatus());
        textPaymentMethod.setText(getString(R.string.order_detail_label_payment_method) + ": " + order.getPaymentMethod());
        textCreatedAt.setText(getString(R.string.order_detail_label_created_at) + ": " + order.getCreatedAtLabel());
        textDeadline.setText(getString(R.string.order_detail_label_deadline) + ": " + order.getDeadlineLabel());
        textParties.setText(order.getPartiesLabel());
        textSummary.setText(order.getSummaryNote());
        textTotals.setText(getString(
                R.string.order_detail_totals_value,
                PriceFormatter.formatCurrency(order.getTotalAmount()),
                PriceFormatter.formatCurrency(order.getRequiredUpfrontAmount()),
                PriceFormatter.formatCurrency(order.getRemainingAmount())
        ));
        toolbar.setTitle(sellerSession ? R.string.order_detail_title_seller : R.string.order_detail_title_buyer);
    }

    private void loadPaymentHistory() {
        loadPaymentHistory(true);
    }

    private void loadPaymentHistory(boolean showLoadingState) {
        if (!currentOrder.isRemoteSource()) {
            cardPaymentHistory.setVisibility(View.GONE);
            cardPaymentRequest.setVisibility(View.GONE);
            stateController.hide();
            return;
        }

        if (showLoadingState) {
            stateController.showLoading(
                    getString(R.string.order_detail_loading_title),
                    getString(R.string.order_detail_loading_message)
            );
        }

        paymentRemoteRepository.fetchOrderPayments(currentOrder.getId(), new RepositoryCallback<List<PaymentHistoryItem>>() {
            @Override
            public void onSuccess(List<PaymentHistoryItem> value) {
                if (isFinishing()) {
                    return;
                }
                renderPaymentHistory(value);
                if (showLoadingState) {
                    stateController.hide();
                }
            }

            @Override
            public void onError(String message, Throwable throwable) {
                if (isFinishing()) {
                    return;
                }
                if (showLoadingState) {
                    stateController.showMessage(
                            getString(R.string.order_detail_payment_history_error_title),
                            message,
                            getString(R.string.state_action_retry),
                            actionView -> loadPaymentHistory()
                    );
                }
            }
        });
    }

    private void refreshOrderSnapshot(boolean showLoadingState) {
        if (!currentOrder.isRemoteSource()) {
            return;
        }

        if (showLoadingState) {
            stateController.showLoading(
                    getString(R.string.order_detail_loading_title),
                    getString(R.string.order_detail_loading_message)
            );
        }

        orderRemoteRepository.fetchMyOrderById(currentOrder.getId(), new RepositoryCallback<OrderPreview>() {
            @Override
            public void onSuccess(OrderPreview value) {
                if (isFinishing()) {
                    return;
                }
                currentOrder = value;
                activePaymentLink = "";
                cardPaymentRequest.setVisibility(View.GONE);
                bindOrder(currentOrder);
                bindAvailableActions();
                loadPaymentHistory(showLoadingState);
            }

            @Override
            public void onError(String message, Throwable throwable) {
                if (isFinishing()) {
                    return;
                }
                if (showLoadingState) {
                    stateController.showMessage(
                            getString(R.string.order_detail_payment_history_error_title),
                            message,
                            getString(R.string.state_action_retry),
                            actionView -> refreshOrderSnapshot(true)
                    );
                }
            }
        });
    }

    private void renderPaymentHistory(List<PaymentHistoryItem> items) {
        cardPaymentHistory.setVisibility(View.VISIBLE);
        layoutPaymentHistoryList.removeAllViews();
        if (items == null || items.isEmpty()) {
            textPaymentHistoryEmpty.setVisibility(View.VISIBLE);
            return;
        }

        textPaymentHistoryEmpty.setVisibility(View.GONE);
        for (PaymentHistoryItem item : items) {
            layoutPaymentHistoryList.addView(createPaymentHistoryView(item));
        }
    }

    private View createPaymentHistoryView(PaymentHistoryItem item) {
        LinearLayout container = new LinearLayout(this);
        container.setOrientation(LinearLayout.VERTICAL);
        container.setPadding(0, 0, 0, 24);

        TextView headline = new TextView(this);
        headline.setText(item.getHeadline());
        headline.setTextColor(ContextCompat.getColor(this, R.color.text_primary));
        headline.setTextSize(15f);
        headline.setTypeface(headline.getTypeface(), android.graphics.Typeface.BOLD);

        TextView supporting = new TextView(this);
        supporting.setText(item.getSupporting());
        supporting.setTextColor(ContextCompat.getColor(this, R.color.text_secondary));
        supporting.setTextSize(14f);
        supporting.setPadding(0, 8, 0, 0);

        TextView timestamp = new TextView(this);
        timestamp.setText(item.getTimestamp());
        timestamp.setTextColor(ContextCompat.getColor(this, R.color.text_secondary));
        timestamp.setTextSize(13f);
        timestamp.setPadding(0, 6, 0, 0);

        container.addView(headline);
        container.addView(supporting);
        container.addView(timestamp);
        return container;
    }

    private void bindAvailableActions() {
        buttonPrimaryAction.setVisibility(View.GONE);
        buttonSecondaryAction.setVisibility(View.GONE);
        textOrderActionHelper.setText(R.string.order_action_helper_idle);

        if (!currentOrder.isRemoteSource()) {
            cardOrderActions.setVisibility(View.GONE);
            return;
        }

        ActionSpec primaryAction = null;
        ActionSpec secondaryAction = null;

        String rawStatus = currentOrder.getRawStatus();
        String rawFundingStatus = currentOrder.getRawFundingStatus();
        String rawPaymentMethod = currentOrder.getRawPaymentMethod();
        boolean hasActivePaymentLink = !activePaymentLink.isEmpty();

        if (sellerSession) {
            if ("pending".equals(rawStatus) && "unpaid".equals(rawFundingStatus)) {
                primaryAction = new ActionSpec(R.string.order_action_accept, this::acceptOrder);
                if ("cash".equals(rawPaymentMethod)) {
                    secondaryAction = new ActionSpec(R.string.order_action_confirm_cash_deposit, this::confirmCashDeposit);
                } else if (canCancelOrder()) {
                    secondaryAction = new ActionSpec(R.string.order_action_cancel, this::cancelOrder);
                }
            } else if ("pending".equals(rawStatus) && "awaiting_payment".equals(rawFundingStatus) && "cash".equals(rawPaymentMethod)) {
                primaryAction = new ActionSpec(R.string.order_action_confirm_cash_deposit, this::confirmCashDeposit);
            } else if ("deposited".equals(rawStatus)) {
                primaryAction = new ActionSpec(R.string.order_action_complete_delivery, this::completeOrder);
            } else if (canCancelOrder()) {
                primaryAction = new ActionSpec(R.string.order_action_cancel, this::cancelOrder);
            }
        } else if (buyerSession) {
            if ("pending".equals(rawStatus) && "awaiting_payment".equals(rawFundingStatus) && !"cash".equals(rawPaymentMethod)) {
                primaryAction = hasActivePaymentLink
                        ? new ActionSpec(R.string.order_action_open_payment_link, this::openActivePaymentLink)
                        : new ActionSpec(R.string.order_action_request_payment, this::requestPayment);
            } else if ("awaiting_buyer_confirmation".equals(rawStatus) && "held".equals(rawFundingStatus)) {
                primaryAction = new ActionSpec(R.string.order_action_confirm_received, this::confirmReceived);
            }

            if (canCancelOrder()) {
                if (primaryAction == null) {
                    primaryAction = new ActionSpec(R.string.order_action_cancel, this::cancelOrder);
                } else {
                    secondaryAction = new ActionSpec(R.string.order_action_cancel, this::cancelOrder);
                }
            }
        }

        if (primaryAction == null && secondaryAction == null) {
            cardOrderActions.setVisibility(View.GONE);
            return;
        }

        cardOrderActions.setVisibility(View.VISIBLE);
        textOrderActionHelper.setText(resolveActionHelper(primaryAction, secondaryAction));
        applyAction(buttonPrimaryAction, primaryAction);
        applyAction(buttonSecondaryAction, secondaryAction);
    }

    private int resolveActionHelper(ActionSpec primaryAction, ActionSpec secondaryAction) {
        if (primaryAction == null && secondaryAction == null) {
            return R.string.order_action_helper_idle;
        }

        if (primaryAction != null) {
            if (primaryAction.labelRes == R.string.order_action_accept) {
                return R.string.order_action_helper_accept;
            }
            if (primaryAction.labelRes == R.string.order_action_confirm_cash_deposit) {
                return R.string.order_action_helper_confirm_cash;
            }
            if (primaryAction.labelRes == R.string.order_action_request_payment
                    || primaryAction.labelRes == R.string.order_action_open_payment_link) {
                return R.string.order_action_helper_request_payment;
            }
            if (primaryAction.labelRes == R.string.order_action_complete_delivery) {
                return R.string.order_action_helper_complete;
            }
            if (primaryAction.labelRes == R.string.order_action_confirm_received) {
                return R.string.order_action_helper_received;
            }
            if (primaryAction.labelRes == R.string.order_action_cancel
                    || (secondaryAction != null && secondaryAction.labelRes == R.string.order_action_cancel)) {
                return R.string.order_action_helper_cancel;
            }
        }

        return R.string.order_action_helper_idle;
    }

    private void applyAction(MaterialButton button, ActionSpec actionSpec) {
        if (actionSpec == null) {
            button.setVisibility(View.GONE);
            button.setOnClickListener(null);
            return;
        }

        button.setVisibility(View.VISIBLE);
        button.setText(actionSpec.labelRes);
        button.setOnClickListener(v -> actionSpec.runnable.run());
    }

    private boolean canCancelOrder() {
        String rawStatus = currentOrder.getRawStatus();
        String rawFundingStatus = currentOrder.getRawFundingStatus();

        return !"completed".equals(rawStatus)
                && !"cancelled".equals(rawStatus)
                && !"deposited".equals(rawStatus)
                && !"awaiting_buyer_confirmation".equals(rawStatus)
                && !"held".equals(rawFundingStatus)
                && !"refund_pending".equals(rawFundingStatus)
                && !"refund_pending_transfer".equals(rawFundingStatus)
                && !"seller_payout_pending".equals(rawFundingStatus)
                && !"released".equals(rawFundingStatus)
                && !"refunded".equals(rawFundingStatus);
    }

    private void requestPayment() {
        stateController.showLoading(
                getString(R.string.order_action_loading_title),
                getString(R.string.order_action_request_payment_loading)
        );

        paymentRemoteRepository.requestUpfrontPayment(currentOrder.getId(), new RepositoryCallback<PaymentRequestInfo>() {
            @Override
            public void onSuccess(PaymentRequestInfo value) {
                if (isFinishing()) {
                    return;
                }
                showPaymentRequest(value);
                bindAvailableActions();
                loadPaymentHistory(true);
                Toast.makeText(OrderDetailActivity.this, R.string.order_action_request_payment_success, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(String message, Throwable throwable) {
                if (isFinishing()) {
                    return;
                }
                showActionError(message, OrderDetailActivity.this::requestPayment);
            }
        });
    }

    private void showPaymentRequest(PaymentRequestInfo info) {
        cardPaymentRequest.setVisibility(View.VISIBLE);
        activePaymentLink = !info.getCheckoutUrl().isEmpty() ? info.getCheckoutUrl() : info.getQrCodeUrl();
        textPaymentRequestSummary.setText(getString(
                R.string.order_payment_request_summary_value,
                info.getAmountLabel(),
                info.isMockMode() ? getString(R.string.order_payment_request_mode_mock) : getString(R.string.order_payment_request_mode_live)
        ));

        StringBuilder transferBuilder = new StringBuilder();
        if (!info.getBankBin().isEmpty()) {
            transferBuilder.append(getString(R.string.order_payment_request_bank_label)).append(": ").append(info.getBankBin());
        }
        if (!info.getBankAccountNumber().isEmpty()) {
            if (transferBuilder.length() > 0) {
                transferBuilder.append('\n');
            }
            transferBuilder.append(getString(R.string.order_payment_request_account_number_label)).append(": ")
                    .append(info.getBankAccountNumber());
        }
        if (!info.getBankAccountName().isEmpty()) {
            if (transferBuilder.length() > 0) {
                transferBuilder.append('\n');
            }
            transferBuilder.append(getString(R.string.order_payment_request_account_name_label)).append(": ")
                    .append(info.getBankAccountName());
        }
        if (!info.getTransferContent().isEmpty()) {
            if (transferBuilder.length() > 0) {
                transferBuilder.append('\n');
            }
            transferBuilder.append(getString(R.string.order_payment_request_transfer_content_label)).append(": ")
                    .append(info.getTransferContent());
        }
        textPaymentRequestTransfer.setText(transferBuilder.toString());
        textPaymentRequestTransfer.setVisibility(transferBuilder.length() == 0 ? View.GONE : View.VISIBLE);

        textPaymentRequestInstructions.setText(info.getInstructions().isEmpty()
                ? getString(R.string.order_payment_request_instruction_empty)
                : info.getInstructions());
        textPaymentRequestExpires.setText(info.getExpiresAtLabel().isEmpty()
                ? getString(R.string.order_payment_request_expire_empty)
                : getString(R.string.order_payment_request_expire_value, info.getExpiresAtLabel()));

        if (activePaymentLink.isEmpty()) {
            buttonPaymentOpenLink.setVisibility(View.GONE);
            buttonPaymentOpenLink.setOnClickListener(null);
        } else {
            buttonPaymentOpenLink.setVisibility(View.VISIBLE);
            buttonPaymentOpenLink.setOnClickListener(v -> openExternalUrl(activePaymentLink));
        }
    }

    private void acceptOrder() {
        executeOrderAction(R.string.order_action_loading_title, R.string.order_action_accept_loading,
                callback -> orderRemoteRepository.acceptOrder(currentOrder.getId(), callback),
                R.string.order_action_accept_success);
    }

    private void confirmCashDeposit() {
        executeOrderAction(R.string.order_action_loading_title, R.string.order_action_confirm_cash_loading,
                callback -> orderRemoteRepository.confirmCashDeposit(currentOrder.getId(), callback),
                R.string.order_action_confirm_cash_success);
    }

    private void completeOrder() {
        executeOrderAction(R.string.order_action_loading_title, R.string.order_action_complete_loading,
                callback -> orderRemoteRepository.completeOrder(currentOrder.getId(), callback),
                R.string.order_action_complete_success);
    }

    private void confirmReceived() {
        executeOrderAction(R.string.order_action_loading_title, R.string.order_action_confirm_received_loading,
                callback -> orderRemoteRepository.confirmReceived(currentOrder.getId(), callback),
                R.string.order_action_confirm_received_success);
    }

    private void cancelOrder() {
        executeOrderAction(R.string.order_action_loading_title, R.string.order_action_cancel_loading,
                callback -> orderRemoteRepository.cancelOrder(currentOrder.getId(), callback),
                R.string.order_action_cancel_success);
    }

    private void executeOrderAction(
            int loadingTitleRes,
            int loadingMessageRes,
            OrderActionExecutor executor,
            int successMessageRes
    ) {
        stateController.showLoading(getString(loadingTitleRes), getString(loadingMessageRes));
        executor.execute(new RepositoryCallback<OrderPreview>() {
            @Override
            public void onSuccess(OrderPreview value) {
                if (isFinishing()) {
                    return;
                }
                currentOrder = value;
                activePaymentLink = "";
                cardPaymentRequest.setVisibility(View.GONE);
                bindOrder(currentOrder);
                bindAvailableActions();
                loadPaymentHistory(true);
                Toast.makeText(OrderDetailActivity.this, successMessageRes, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(String message, Throwable throwable) {
                if (isFinishing()) {
                    return;
                }
                showActionError(message, () -> executeOrderAction(loadingTitleRes, loadingMessageRes, executor, successMessageRes));
            }
        });
    }

    private void showActionError(String message, Runnable retryRunnable) {
        stateController.showMessage(
                getString(R.string.order_action_error_title),
                message,
                getString(R.string.state_action_retry),
                actionView -> retryRunnable.run()
        );
    }

    private void openExternalUrl(String url) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(intent);
        } catch (Exception exception) {
            Toast.makeText(this, R.string.order_payment_request_open_link_error, Toast.LENGTH_SHORT).show();
        }
    }

    private void openActivePaymentLink() {
        if (activePaymentLink.isEmpty()) {
            Toast.makeText(this, R.string.order_payment_request_open_link_error, Toast.LENGTH_SHORT).show();
            return;
        }
        openExternalUrl(activePaymentLink);
    }

    private interface OrderActionExecutor {
        void execute(RepositoryCallback<OrderPreview> callback);
    }

    private static class ActionSpec {
        private final int labelRes;
        private final Runnable runnable;

        private ActionSpec(int labelRes, Runnable runnable) {
            this.labelRes = labelRes;
            this.runnable = runnable;
        }
    }
}
