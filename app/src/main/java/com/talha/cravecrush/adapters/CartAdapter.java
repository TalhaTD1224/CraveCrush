package com.talha.cravecrush.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.talha.cravecrush.R;
import com.talha.cravecrush.models.CartItem;
import com.talha.cravecrush.utils.CartManager;

import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    private Context context;
    private List<CartItem> cartItems;
    private final Runnable updateCallback;
    private CartManager cartManager;

    public CartAdapter(Context context, List<CartItem> cartItems, Runnable updateCallback) {
        this.context = context;
        this.cartItems = cartItems;
        this.updateCallback = updateCallback;
        this.cartManager = CartManager.getInstance();
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_cart, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        CartItem item = cartItems.get(position);

        holder.tvTitle.setText(item.getMenuItem().getTitle());
        holder.tvQuantity.setText(String.valueOf(item.getQuantity()));
        holder.tvTotalPrice.setText("PKR " + String.format("%.2f", item.getTotalPrice()));

        Glide.with(context).load(item.getMenuItem().getImageUrl()).into(holder.ivImage);

        holder.btnAdd.setOnClickListener(v -> {
            int currentPosition = holder.getAdapterPosition();
            if (currentPosition == RecyclerView.NO_POSITION) return;

            cartManager.addToCart(item.getMenuItem(), 1);
            notifyItemChanged(currentPosition);
            updateCallback.run(); // Update the total in CartActivity
        });

        holder.btnRemove.setOnClickListener(v -> {
            int currentPosition = holder.getAdapterPosition();
            if (currentPosition == RecyclerView.NO_POSITION) return;

            cartManager.removeFromCart(item.getMenuItem());

            if (cartManager.getQuantity(item.getMenuItem()) == 0) {
                notifyDataSetChanged();
            } else {
                notifyItemChanged(currentPosition);
            }
            updateCallback.run(); // Update the total in CartActivity
        });
    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    public static class CartViewHolder extends RecyclerView.ViewHolder {
        ImageView ivImage;
        TextView tvTitle, tvQuantity, tvTotalPrice;
        ImageButton btnAdd, btnRemove;

        public CartViewHolder(View itemView) {
            super(itemView);
            ivImage = itemView.findViewById(R.id.ivImage);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvQuantity = itemView.findViewById(R.id.tv_quantity);
            tvTotalPrice = itemView.findViewById(R.id.tvTotalPrice);
            btnAdd = itemView.findViewById(R.id.btn_add);
            btnRemove = itemView.findViewById(R.id.btn_remove);
        }
    }
}