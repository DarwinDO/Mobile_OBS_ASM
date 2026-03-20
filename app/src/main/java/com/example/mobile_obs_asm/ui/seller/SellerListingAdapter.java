package com.example.mobile_obs_asm.ui.seller;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobile_obs_asm.R;
import com.example.mobile_obs_asm.model.SellerListing;
import com.example.mobile_obs_asm.util.ProductImageUrlResolver;
import com.example.mobile_obs_asm.util.PriceFormatter;
import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.List;

public class SellerListingAdapter extends RecyclerView.Adapter<SellerListingAdapter.SellerListingViewHolder> {

    public interface OnSellerListingActionListener {
        void onPrimaryAction(SellerListing listing);

        void onEditAction(SellerListing listing);

        void onDeleteAction(SellerListing listing);
    }

    private final List<SellerListing> listings;
    private final OnSellerListingActionListener actionListener;

    public SellerListingAdapter(List<SellerListing> listings, OnSellerListingActionListener actionListener) {
        this.listings = new ArrayList<>(listings);
        this.actionListener = actionListener;
    }

    public void replaceListings(List<SellerListing> updatedListings) {
        listings.clear();
        listings.addAll(updatedListings);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public SellerListingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_seller_listing_card, parent, false);
        return new SellerListingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SellerListingViewHolder holder, int position) {
        SellerListing listing = listings.get(position);
        holder.textStatus.setText(listing.getStatusLabel());
        holder.textTitle.setText(listing.getTitle());
        holder.textSummary.setText(listing.getSummary());
        holder.textMeta.setText(listing.getMeta());
        holder.textPrice.setText(PriceFormatter.formatCurrency(listing.getPrice()));
        holder.textCoverLabel.setText(listing.getCoverLabel());
        holder.cardCover.setCardBackgroundColor(ContextCompat.getColor(holder.itemView.getContext(), listing.getCoverColorRes()));
        bindCoverImage(holder, listing);

        boolean managementLocked = listing.isSold() || listing.isLockedForTransaction();
        holder.buttonPrimary.setEnabled(!listing.isSold());
        holder.buttonPrimary.setText(listing.isSold()
                ? R.string.seller_listing_action_sold
                : (listing.isHidden() ? R.string.seller_listing_action_show : R.string.seller_listing_action_hide));
        holder.buttonPrimary.setOnClickListener(listing.isSold() ? null : v -> actionListener.onPrimaryAction(listing));

        holder.buttonEdit.setEnabled(!managementLocked);
        holder.buttonDelete.setEnabled(!managementLocked);
        holder.buttonEdit.setOnClickListener(managementLocked ? null : v -> actionListener.onEditAction(listing));
        holder.buttonDelete.setOnClickListener(managementLocked ? null : v -> actionListener.onDeleteAction(listing));
    }

    @Override
    public int getItemCount() {
        return listings.size();
    }

    private void bindCoverImage(@NonNull SellerListingViewHolder holder, SellerListing listing) {
        if (ProductImageUrlResolver.hasValue(listing.getImageUrl())) {
            holder.imageCover.setVisibility(View.VISIBLE);
            holder.textCoverLabel.setVisibility(View.GONE);
            Glide.with(holder.itemView)
                    .load(listing.getImageUrl())
                    .centerCrop()
                    .into(holder.imageCover);
            return;
        }

        Glide.with(holder.itemView).clear(holder.imageCover);
        holder.imageCover.setImageDrawable(null);
        holder.imageCover.setVisibility(View.GONE);
        holder.textCoverLabel.setVisibility(View.VISIBLE);
    }

    static class SellerListingViewHolder extends RecyclerView.ViewHolder {
        private final MaterialCardView cardCover;
        private final ImageView imageCover;
        private final TextView textStatus;
        private final TextView textTitle;
        private final TextView textSummary;
        private final TextView textMeta;
        private final TextView textPrice;
        private final TextView textCoverLabel;
        private final MaterialButton buttonPrimary;
        private final MaterialButton buttonEdit;
        private final MaterialButton buttonDelete;

        SellerListingViewHolder(@NonNull View itemView) {
            super(itemView);
            cardCover = itemView.findViewById(R.id.cardSellerListingCover);
            imageCover = itemView.findViewById(R.id.imageSellerListingCover);
            textStatus = itemView.findViewById(R.id.textSellerListingStatus);
            textTitle = itemView.findViewById(R.id.textSellerListingTitle);
            textSummary = itemView.findViewById(R.id.textSellerListingSummary);
            textMeta = itemView.findViewById(R.id.textSellerListingMeta);
            textPrice = itemView.findViewById(R.id.textSellerListingPrice);
            textCoverLabel = itemView.findViewById(R.id.textSellerListingCoverLabel);
            buttonPrimary = itemView.findViewById(R.id.buttonSellerListingPrimary);
            buttonEdit = itemView.findViewById(R.id.buttonSellerListingEdit);
            buttonDelete = itemView.findViewById(R.id.buttonSellerListingDelete);
        }
    }
}
