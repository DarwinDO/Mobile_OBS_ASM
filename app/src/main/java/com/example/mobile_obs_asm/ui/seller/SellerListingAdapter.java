package com.example.mobile_obs_asm.ui.seller;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobile_obs_asm.R;
import com.example.mobile_obs_asm.model.SellerListing;
import com.example.mobile_obs_asm.util.PriceFormatter;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.List;

public class SellerListingAdapter extends RecyclerView.Adapter<SellerListingAdapter.SellerListingViewHolder> {

    public interface OnSellerListingActionListener {
        void onPrimaryAction(SellerListing listing);
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

        if (listing.isSold()) {
            holder.buttonPrimary.setEnabled(false);
            holder.buttonPrimary.setText(R.string.seller_listing_action_sold);
            holder.buttonPrimary.setOnClickListener(null);
        } else {
            holder.buttonPrimary.setEnabled(true);
            holder.buttonPrimary.setText(listing.isHidden()
                    ? R.string.seller_listing_action_show
                    : R.string.seller_listing_action_hide);
            holder.buttonPrimary.setOnClickListener(v -> actionListener.onPrimaryAction(listing));
        }
    }

    @Override
    public int getItemCount() {
        return listings.size();
    }

    static class SellerListingViewHolder extends RecyclerView.ViewHolder {
        private final MaterialCardView cardCover;
        private final TextView textStatus;
        private final TextView textTitle;
        private final TextView textSummary;
        private final TextView textMeta;
        private final TextView textPrice;
        private final TextView textCoverLabel;
        private final MaterialButton buttonPrimary;

        SellerListingViewHolder(@NonNull View itemView) {
            super(itemView);
            cardCover = itemView.findViewById(R.id.cardSellerListingCover);
            textStatus = itemView.findViewById(R.id.textSellerListingStatus);
            textTitle = itemView.findViewById(R.id.textSellerListingTitle);
            textSummary = itemView.findViewById(R.id.textSellerListingSummary);
            textMeta = itemView.findViewById(R.id.textSellerListingMeta);
            textPrice = itemView.findViewById(R.id.textSellerListingPrice);
            textCoverLabel = itemView.findViewById(R.id.textSellerListingCoverLabel);
            buttonPrimary = itemView.findViewById(R.id.buttonSellerListingPrimary);
        }
    }
}
