package com.example.mobile_obs_asm;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.IntentCompat;
import androidx.core.content.ContextCompat;

import com.example.mobile_obs_asm.data.ProductRemoteRepository;
import com.example.mobile_obs_asm.data.RepositoryCallback;
import com.example.mobile_obs_asm.data.SessionManager;
import com.example.mobile_obs_asm.data.WishlistRemoteRepository;
import com.example.mobile_obs_asm.data.FakeMarketplaceRepository;
import com.example.mobile_obs_asm.data.WishlistStateStore;
import com.example.mobile_obs_asm.model.Product;
import com.example.mobile_obs_asm.util.ProductImageUrlResolver;
import com.example.mobile_obs_asm.util.PriceFormatter;
import com.example.mobile_obs_asm.util.SystemBarInsetsHelper;
import com.bumptech.glide.Glide;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

import java.util.List;

public class ProductDetailActivity extends SessionAwareActivity {

    private static final String EXTRA_PRODUCT = "extra_product";

    private Product currentProduct;
    private boolean wishlistSaved;
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
        SystemBarInsetsHelper.applyToRoot(findViewById(R.id.productDetailRoot));

        MaterialToolbar toolbar = findViewById(R.id.toolbarDetail);
        toolbar.setNavigationOnClickListener(view -> finish());
        toolbar.setTitle("");

        currentProduct = readProductExtra();
        if (currentProduct == null) {
            Toast.makeText(this, R.string.detail_missing_toast, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        setupActions();
        wishlistSaved = resolveWishlistSaved(currentProduct);
        bindProduct(currentProduct);
        refreshRemoteDetailIfNeeded(currentProduct);
        syncRemoteWishlistStateIfNeeded(currentProduct);
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
        ImageView imageCover = findViewById(R.id.imageDetailCover);
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
        bindCoverImage(imageCover, textCoverLabel, product);
        textCondition.setText(getString(R.string.label_condition) + ": " + product.getCondition());
        textFrameSize.setText(getString(R.string.label_frame_size) + ": " + product.getFrameSize());
        textWheelSize.setText(getString(R.string.label_wheel_size) + ": " + product.getWheelSize());
        textGroupset.setText(getString(R.string.label_groupset) + ": " + product.getGroupset());
        textDescription.setText(product.getDescription());
        textTrust.setText(product.isRemoteSource()
                ? getString(R.string.detail_trust_live)
                : getString(R.string.detail_trust_preview));

        if (buttonSavePreview != null && buttonOrderPreview != null) {
            buttonSavePreview.setText(resolveWishlistButtonLabel());
            buttonOrderPreview.setText(product.isRemoteSource() ? R.string.detail_order_action : R.string.detail_order_preview_action);
        }
    }

    private void bindCoverImage(ImageView imageCover, TextView textCoverLabel, Product product) {
        if (ProductImageUrlResolver.hasValue(product.getImageUrl())) {
            imageCover.setVisibility(View.VISIBLE);
            textCoverLabel.setVisibility(View.GONE);
            Glide.with(this)
                    .load(product.getImageUrl())
                    .centerCrop()
                    .into(imageCover);
            return;
        }

        Glide.with(this).clear(imageCover);
        imageCover.setImageDrawable(null);
        imageCover.setVisibility(View.GONE);
        textCoverLabel.setVisibility(View.VISIBLE);
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
            handlePreviewWishlistAction();
            return;
        }

        if (!SessionManager.getInstance(this).hasActiveSession()) {
            Toast.makeText(this, R.string.detail_wishlist_requires_sign_in, Toast.LENGTH_LONG).show();
            return;
        }

        if (wishlistSaved) {
            removeRemoteWishlistProduct();
            return;
        }

        addRemoteWishlistProduct();
    }

    private void handlePreviewWishlistAction() {
        FakeMarketplaceRepository fakeMarketplaceRepository = FakeMarketplaceRepository.getInstance();
        if (wishlistSaved) {
            fakeMarketplaceRepository.removeWishlistProduct(currentProduct.getId());
            wishlistSaved = false;
            bindProduct(currentProduct);
            Toast.makeText(this, R.string.detail_wishlist_removed_toast, Toast.LENGTH_SHORT).show();
            return;
        }

        fakeMarketplaceRepository.saveWishlistProduct(currentProduct);
        wishlistSaved = true;
        bindProduct(currentProduct);
        Toast.makeText(this, R.string.detail_saved_toast, Toast.LENGTH_SHORT).show();
    }

    private void addRemoteWishlistProduct() {
        setWishlistSaveLoading(true);
        new WishlistRemoteRepository(this).addProduct(currentProduct.getId(), new RepositoryCallback<Product>() {
            @Override
            public void onSuccess(Product value) {
                if (isFinishing()) {
                    return;
                }
                setWishlistSaveLoading(false);
                wishlistSaved = true;
                bindProduct(currentProduct);
                Toast.makeText(ProductDetailActivity.this, R.string.detail_wishlist_synced_toast, Toast.LENGTH_SHORT).show();
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

    private void removeRemoteWishlistProduct() {
        setWishlistSaveLoading(true);
        new WishlistRemoteRepository(this).removeProduct(currentProduct.getId(), new RepositoryCallback<Void>() {
            @Override
            public void onSuccess(Void value) {
                if (isFinishing()) {
                    return;
                }
                setWishlistSaveLoading(false);
                wishlistSaved = false;
                bindProduct(currentProduct);
                Toast.makeText(ProductDetailActivity.this, R.string.detail_wishlist_removed_toast, Toast.LENGTH_SHORT).show();
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
        buttonSavePreview.setText(isLoading ? R.string.detail_save_loading : resolveWishlistButtonLabel());
    }

    private void refreshRemoteDetailIfNeeded(Product initialProduct) {
        if (!initialProduct.isRemoteSource()) {
            return;
        }

        ProductRemoteRepository productRemoteRepository = new ProductRemoteRepository(this);
        productRemoteRepository.fetchProductDetail(initialProduct.getId(), new RepositoryCallback<Product>() {
            @Override
            public void onSuccess(Product value) {
                wishlistSaved = resolveWishlistSaved(value);
                bindProduct(value);
            }

            @Override
            public void onError(String message, Throwable throwable) {
                // Keep the passed preview data if detail sync fails.
            }
        });
    }

    private void syncRemoteWishlistStateIfNeeded(Product product) {
        if (product == null || !product.isRemoteSource()) {
            return;
        }
        if (!SessionManager.getInstance(this).hasActiveSession() || wishlistSaved) {
            return;
        }

        new WishlistRemoteRepository(this).fetchWishlist(new RepositoryCallback<List<Product>>() {
            @Override
            public void onSuccess(List<Product> value) {
                if (isFinishing() || currentProduct == null) {
                    return;
                }
                wishlistSaved = WishlistStateStore.getInstance(ProductDetailActivity.this).isSaved(currentProduct.getId());
                bindProduct(currentProduct);
            }

            @Override
            public void onError(String message, Throwable throwable) {
                // Keep the local state if wishlist sync fails.
            }
        });
    }

    private Product readProductExtra() {
        return IntentCompat.getParcelableExtra(getIntent(), EXTRA_PRODUCT, Product.class);
    }

    private boolean resolveWishlistSaved(Product product) {
        if (product == null) {
            return false;
        }
        if (!product.isRemoteSource()) {
            return FakeMarketplaceRepository.getInstance().isWishlistProduct(product.getId());
        }
        if (!SessionManager.getInstance(this).hasActiveSession()) {
            return false;
        }
        return WishlistStateStore.getInstance(this).isSaved(product.getId());
    }

    private int resolveWishlistButtonLabel() {
        return wishlistSaved ? R.string.detail_remove_action : R.string.detail_save_action;
    }
}
