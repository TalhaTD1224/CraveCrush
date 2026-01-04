package com.talha.cravecrush.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.talha.cravecrush.R;
import com.talha.cravecrush.admin.AdminOrderDetailActivity;
import com.talha.cravecrush.models.Order;

import java.util.List;

public class AdminOrderAdapter extends RecyclerView.Adapter<AdminOrderAdapter.AdminOrderViewHolder> {

    private Context context;
    private List<Order> orders;

    public AdminOrderAdapter(Context context, List<Order> orders) {
        this.context = context;
        this.orders = orders;
    }

    @NonNull
    @Override
    public AdminOrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_admin_order, parent, false);
        return new AdminOrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdminOrderViewHolder holder, int position) {
        Order order = orders.get(position);

        holder.tvOrderId.setText("Order #" + order.getId().substring(0, 6)); // Shortened ID
        holder.tvUser.setText("By: " + order.getGuestName());
        holder.tvPayment.setText("PKR " + String.format("%.2f", order.getTotalPrice()));
        holder.tvStatus.setText(order.getStatus());

        // Set status background color
        int statusBgRes;
        switch (order.getStatus().toLowerCase()) {
            case "approved":
                statusBgRes = R.drawable.status_approved_bg;
                break;
            case "rejected":
                statusBgRes = R.drawable.status_rejected_bg;
                break;
            default: // pending
                statusBgRes = R.drawable.status_pending_bg;
                break;
        }
        holder.tvStatus.setBackground(ContextCompat.getDrawable(context, statusBgRes));

        // Set click listener to open detail view
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, AdminOrderDetailActivity.class);
            intent.putExtra("orderId", order.getId());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    static class AdminOrderViewHolder extends RecyclerView.ViewHolder {
        TextView tvOrderId, tvUser, tvPayment, tvStatus;

        public AdminOrderViewHolder(@NonNull View itemView) {
            super(itemView);
            tvOrderId = itemView.findViewById(R.id.tvOrderId);
            tvUser = itemView.findViewById(R.id.tvUser);
            tvPayment = itemView.findViewById(R.id.tvPayment);
            tvStatus = itemView.findViewById(R.id.tvStatus);
        }
    }
}