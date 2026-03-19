package com.example.mobile_obs_asm.ui.home;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobile_obs_asm.R;
import com.example.mobile_obs_asm.model.Product;
import com.example.mobile_obs_asm.util.PriceFormatter;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    public interface OnProductClickListener {
        void onProductClick(Product product);
    }

    public interface OnProductActionClickListener {
        void onProductActionClick(Product product);
    }

    private final List<Product> products;
    private final OnProductClickListener listener;
    private final int actionLabelRes;
    private final OnProductActionClickListener actionListener;

    public ProductAdapter(List<Product> products, OnProductClickListener listener) {
        this(products, listener, 0, null);
    }

    public ProductAdapter(
            List<Product> products,
            OnProductClickListener listener,
            @StringRes int actionLabelRes,
            @Nullable OnProductActionClickListener actionListener
    ) {
        this.products = new ArrayList<>(products);
        this.listener = listener;
        this.actionLabelRes = actionLabelRes;
        this.actionListener = actionListener;
    }

    public void replaceProducts(List<Product> updatedProducts) {
        products.clear();
        products.addAll(updatedProducts);
        notifyDataSetChanged();
    }

    public Product getFirstProduct() {
        return products.isEmpty() ? null : products.get(0);
    }

    public void removeProduct(Product product) {
        for (int index = 0; index < products.size(); index++) {
            if (products.get(index).getId().equals(product.getId())) {
                products.remove(index);
                notifyItemRemoved(index);
                return;
            }
        }
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product_card, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = products.get(position);
        holder.textBadge.setText(product.getBadge());
        holder.textTitle.setText(product.getTitle());
        holder.textTagline.setText(product.getTagline());
        holder.textMeta.setText(product.getLocation() + " | " + product.getCondition());
        holder.textPrice.setText(PriceFormatter.formatCurrency(product.getPrice()));
        holder.textCoverLabel.setText(product.getCoverLabel());
        holder.cardCover.setCardBackgroundColor(ContextCompat.getColor(holder.itemView.getContext(), product.getCoverColorRes()));
        holder.cardView.setOnClickListener(v -> listener.onProductClick(product));
        if (actionLabelRes != 0 && actionListener != null) {
            holder.buttonAction.setVisibility(View.VISIBLE);
            holder.buttonAction.setText(actionLabelRes);
            holder.buttonAction.setOnClickListener(v -> actionListener.onProductActionClick(product));
        } else {
            holder.buttonAction.setVisibility(View.VISIBLE);
            holder.buttonAction.setText(R.string.product_action_open_detail);
            holder.buttonAction.setOnClickListener(v -> listener.onProductClick(product));
        }
        holder.cardView.setContentDescription(
                holder.itemView.getContext().getString(R.string.accessibility_open_product) + ": " + product.getTitle()
        );
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    static class ProductViewHolder extends RecyclerView.ViewHolder {
        private final MaterialCardView cardView;
        private final MaterialCardView cardCover;
        private final TextView textBadge;
        private final TextView textTitle;
        private final TextView textTagline;
        private final TextView textMeta;
        private final TextView textPrice;
        private final TextView textCoverLabel;
        private final MaterialButton buttonAction;

        ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.cardProduct);
            cardCover = itemView.findViewById(R.id.cardProductCover);
            textBadge = itemView.findViewById(R.id.textProductBadge);
            textTitle = itemView.findViewById(R.id.textProductTitle);
            textTagline = itemView.findViewById(R.id.textProductTagline);
            textMeta = itemView.findViewById(R.id.textProductMeta);
            textPrice = itemView.findViewById(R.id.textProductPrice);
            textCoverLabel = itemView.findViewById(R.id.textProductCoverLabel);
            buttonAction = itemView.findViewById(R.id.buttonProductAction);
        }
    }
}
