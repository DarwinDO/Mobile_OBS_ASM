package com.example.mobile_obs_asm.ui.orders;

import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobile_obs_asm.R;
import com.example.mobile_obs_asm.model.OrderPreview;
import com.example.mobile_obs_asm.util.PriceFormatter;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {

    public interface OnOrderClickListener {
        void onOrderClick(OrderPreview order);
    }

    private final List<OrderPreview> orders;
    private final OnOrderClickListener listener;

    public OrderAdapter(List<OrderPreview> orders, OnOrderClickListener listener) {
        this.orders = new ArrayList<>(orders);
        this.listener = listener;
    }

    public void replaceOrders(List<OrderPreview> updatedOrders) {
        orders.clear();
        orders.addAll(updatedOrders);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order_card, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        OrderPreview order = orders.get(position);
        holder.textStatus.setText(order.getStatus());
        holder.textTitle.setText(order.getTitle());
        holder.textTimeline.setText(order.getTimeline());
        holder.textAmount.setText(PriceFormatter.formatCurrency(order.getAmount()));
        holder.textStatus.setBackgroundTintList(ColorStateList.valueOf(
                ContextCompat.getColor(holder.itemView.getContext(), order.getStatusColorRes())
        ));
        holder.itemView.setOnClickListener(view -> listener.onOrderClick(order));
        holder.buttonAction.setOnClickListener(view -> listener.onOrderClick(order));
        holder.itemView.setContentDescription(
                holder.itemView.getContext().getString(R.string.accessibility_open_order) + ": " + order.getTitle()
        );
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    static class OrderViewHolder extends RecyclerView.ViewHolder {
        private final TextView textStatus;
        private final TextView textTitle;
        private final TextView textTimeline;
        private final TextView textAmount;
        private final MaterialButton buttonAction;

        OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            textStatus = itemView.findViewById(R.id.textOrderStatus);
            textTitle = itemView.findViewById(R.id.textOrderTitle);
            textTimeline = itemView.findViewById(R.id.textOrderTimeline);
            textAmount = itemView.findViewById(R.id.textOrderAmount);
            buttonAction = itemView.findViewById(R.id.buttonOrderAction);
        }
    }
}
