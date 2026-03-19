package com.example.mobile_obs_asm;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.IntentCompat;
import androidx.core.content.ContextCompat;

import com.example.mobile_obs_asm.data.ProductRemoteRepository;
import com.example.mobile_obs_asm.data.RepositoryCallback;
import com.example.mobile_obs_asm.data.SessionManager;
import com.example.mobile_obs_asm.data.WishlistRemoteRepository;
import com.example.mobile_obs_asm.model.Product;
import com.example.mobile_obs_asm.util.PriceFormatter;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

public class ProductDetailActivity extends AppCompatActivity {

    private static final String EXTRA_PRODUCT = "extra_product";

    private Product currentProduct;
    private MaterialButton buttonSavePreview;
    private MaterialButton buttonOrderPreview;

    public static Intent createIntent(Context context, Product product) {
        Intent intent = new Intent(context, ProductDetailActivity.class);
        intent.putExtra(EXTRA_PRODUCT, product);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        MaterialToolbar toolbar = findViewById(R.id.toolbarDetail);
        toolbar.setNavigationOnClickListener(view -> finish());
        toolbar.setTitle("");

        currentProduct = readProductExtra();
        if (currentProduct == null) {
            Toast.makeText(this, R.string.detail_missing_toast, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        bindProduct(currentProduct);
        setupActions();
        refreshRemoteDetailIfNeeded(currentProduct);
    }

    private void bindProduct(Product product) {
        currentProduct = product;

        MaterialCardView heroCard = findViewById(R.id.cardDetailHero);
        MaterialCardView coverCard = findViewById(R.id.cardDetailCover);
        TextView textBadge = findViewById(R.id.textDetailBadge);
        TextView textTitle = findViewById(R.id.textDetailTitle);
        TextView textTagline = findViewById(R.id.textDetailTagline);
        TextView textPrice = findViewById(R.id.textDetailPrice);
        TextView textLocation = findViewById(R.id.textDetailLocation);
        TextView textCoverLabel = findViewById(R.id.textDetailCoverLabel);
        TextView textCondition = findViewById(R.id.textDetailCondition);
        TextView textFrameSize = findViewById(R.id.textDetailFrameSize);
        TextView textWheelSize = findViewById(R.id.textDetailWheelSize);
        TextView textGroupset = findViewById(R.id.textDetailGroupset);
        TextView textDescription = findViewById(R.id.textDetailDescription);
        TextView textTrust = findViewById(R.id.textDetailTrust);

        heroCard.setCardBackgroundColor(ContextCompat.getColor(this, product.getHeroColorRes()));
        coverCard.setCardBackgroundColor(ContextCompat.getColor(this, product.getCoverColorRes()));
        textBadge.setText(product.getBadge());
        textTitle.setText(product.getTitle());
        textTagline.setText(product.getTagline());
        textPrice.setText(PriceFormatter.formatCurrency(product.getPrice()));
        textLocation.setText(product.getLocation() + " | " + product.getCondition());
        textCoverLabel.setText(product.getCoverLabel());
        textCondition.setText(getString(R.string.label_condition) + ": " + product.getCondition());
        textFrameSize.setText(getString(R.string.label_frame_size) + ": " + product.getFrameSize());
        textWheelSize.setText(getString(R.string.label_wheel_size) + ": " + product.getWheelSize());
        textGroupset.setText(getString(R.string.label_groupset) + ": " + product.getGroupset());
        textDescription.setText(product.getDescription());
        textTrust.setText(product.isRemoteSource()
                ? getString(R.string.detail_trust_live)
                : getString(R.string.detail_trust_preview));
    }

    private void setupActions() {
        buttonSavePreview = findViewById(R.id.buttonSavePreview);
        buttonOrderPreview = findViewById(R.id.buttonOrderPreview);

        buttonSavePreview.setOnClickListener(view -> handleSaveAction());
        buttonOrderPreview.setOnClickListener(view -> handleOrderAction());
    }

    private void handleSaveAction() {
        if (currentProduct == null) {
            return;
        }

        if (!currentProduct.isRemoteSource()) {
            openMainSection(R.id.navigation_wishlist, R.string.detail_saved_toast);
            return;
        }

        if (!SessionManager.getInstance(this).hasActiveSession()) {
            Toast.makeText(this, R.string.detail_wishlist_requires_sign_in, Toast.LENGTH_LONG).show();
            return;
        }

        setWishlistSaveLoading(true);
        new WishlistRemoteRepository(this).addProduct(currentProduct.getId(), new RepositoryCallback<Product>() {
            @Override
            public void onSuccess(Product value) {
                if (isFinishing()) {
                    return;
                }
                setWishlistSaveLoading(false);
                Toast.makeText(ProductDetailActivity.this, R.string.detail_wishlist_synced_toast, Toast.LENGTH_SHORT).show();
                startActivity(MainActivity.createIntent(ProductDetailActivity.this, R.id.navigation_wishlist));
                finish();
            }

            @Override
            public void onError(String message, Throwable throwable) {
                if (isFinishing()) {
                    return;
                }
                setWishlistSaveLoading(false);
                Toast.makeText(ProductDetailActivity.this, message, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void openMainSection(int destination, int toastMessage) {
        Toast.makeText(this, toastMessage, Toast.LENGTH_SHORT).show();
        startActivity(MainActivity.createIntent(this, destination));
        finish();
    }

    private void handleOrderAction() {
        if (currentProduct == null) {
            return;
        }

        if (!currentProduct.isRemoteSource()) {
            openMainSection(R.id.navigation_orders, R.string.detail_order_toast);
            return;
        }

        if (!SessionManager.getInstance(this).hasActiveSession()) {
            Toast.makeText(this, R.string.detail_order_requires_sign_in, Toast.LENGTH_LONG).show();
            return;
        }

        startActivity(CreateOrderActivity.createIntent(this, currentProduct));
    }

    private void setWishlistSaveLoading(boolean isLoading) {
        buttonSavePreview.setEnabled(!isLoading);
        buttonOrderPreview.setEnabled(!isLoading);
        buttonSavePreview.setText(isLoading ? R.string.detail_save_loading : R.string.detail_save_action);
    }

    private void refreshRemoteDetailIfNeeded(Product initialProduct) {
        if (!initialProduct.isRemoteSource()) {
            return;
        }

        ProductRemoteRepository productRemoteRepository = new ProductRemoteRepository(this);
        productRemoteRepository.fetchProductDetail(initialProduct.getId(), new RepositoryCallback<Product>() {
            @Override
            public void onSuccess(Product value) {
                bindProduct(value);
            }

            @Override
            public void onError(String message, Throwable throwable) {
                // Keep the passed preview data if detail sync fails.
            }
        });
    }

    private Product readProductExtra() {
        return IntentCompat.getParcelableExtra(getIntent(), EXTRA_PRODUCT, Product.class);
    }
}
