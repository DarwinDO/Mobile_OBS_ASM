package com.example.mobile_obs_asm;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.IntentCompat;

import com.example.mobile_obs_asm.model.OrderPreview;
import com.example.mobile_obs_asm.util.PriceFormatter;
import com.example.mobile_obs_asm.util.SystemBarInsetsHelper;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.card.MaterialCardView;

public class OrderDetailActivity extends AppCompatActivity {

    private static final String EXTRA_ORDER = "extra_order";

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

        OrderPreview order = IntentCompat.getParcelableExtra(getIntent(), EXTRA_ORDER, OrderPreview.class);
        if (order == null) {
            Toast.makeText(this, R.string.order_detail_missing_toast, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        bindOrder(order);
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
    }
}
