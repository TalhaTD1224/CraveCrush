package com.talha.cravecrush.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.talha.cravecrush.R;
import com.talha.cravecrush.activities.MenuDetailActivity;
import com.talha.cravecrush.models.MenuItem;
import com.talha.cravecrush.utils.CartManager;

import java.util.ArrayList;
import java.util.List;

public class MenuAdapter extends RecyclerView.Adapter<MenuAdapter.MenuViewHolder> {

    private Context context;
    private List<MenuItem> menuItems;
    private List<MenuItem> originalList;
    private CartManager cartManager;

    public MenuAdapter(Context context, List<MenuItem> menuItems) {
        this.context = context;
        this.menuItems = menuItems;
        this.originalList = new ArrayList<>(menuItems);
        this.cartManager = CartManager.getInstance();
    }

    @NonNull
    @Override
    public MenuViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_menu, parent, false);
        return new MenuViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MenuViewHolder holder, int position) {
        MenuItem item = menuItems.get(position);
        holder.tvTitle.setText(item.getTitle());
        holder.tvDesc.setText(item.getDescription());
        holder.tvPrice.setText("PKR " + item.getPrice());

        Glide.with(context).load(item.getImageUrl()).into(holder.ivImage);

        updateQuantityUI(holder, item);

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, MenuDetailActivity.class);
            intent.putExtra("menuItem", item); // Keep this for the cart functionality in detail view
            intent.putExtra("title", item.getTitle());
            intent.putExtra("description", item.getDescription());
            intent.putExtra("price", item.getPrice());
            intent.putExtra("imageUrl", item.getImageUrl());
            intent.putExtra("category", item.getCategory());
            context.startActivity(intent);
        });

        holder.btnAddToCart.setOnClickListener(v -> {
            cartManager.addToCart(item, 1);
            updateQuantityUI(holder, item);
            Toast.makeText(context, item.getTitle() + " added to cart", Toast.LENGTH_SHORT).show();
        });

        holder.btnAdd.setOnClickListener(v -> {
            cartManager.addToCart(item, 1);
            updateQuantityUI(holder, item);
        });

        holder.btnRemove.setOnClickListener(v -> {
            cartManager.removeFromCart(item);
            updateQuantityUI(holder, item);
        });
    }

    private void updateQuantityUI(MenuViewHolder holder, MenuItem item) {
        int quantity = cartManager.getQuantity(item);
        if (quantity > 0) {
            holder.btnAddToCart.setVisibility(View.GONE);
            holder.quantitySelector.setVisibility(View.VISIBLE);
            holder.tvQuantity.setText(String.valueOf(quantity));
        } else {
            holder.btnAddToCart.setVisibility(View.VISIBLE);
            holder.quantitySelector.setVisibility(View.GONE);
        }
    }

    public void filter(String category) {
        menuItems.clear();
        if (category.equalsIgnoreCase("All")) {
            menuItems.addAll(originalList);
        } else {
            for (MenuItem item : originalList) {
                if (item.getCategory().equalsIgnoreCase(category)) {
                    menuItems.add(item);
                }
            }
        }
        notifyDataSetChanged();
    }

    public void setOriginalList(List<MenuItem> originalList) {
        this.originalList = new ArrayList<>(originalList);
    }

    @Override
    public int getItemCount() {
        return menuItems.size();
    }

    public static class MenuViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvDesc, tvPrice, tvQuantity;
        ImageView ivImage;
        Button btnAddToCart;
        LinearLayout quantitySelector;
        ImageButton btnAdd, btnRemove;

        public MenuViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvDesc = itemView.findViewById(R.id.tvDesc);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            ivImage = itemView.findViewById(R.id.ivImage);
            btnAddToCart = itemView.findViewById(R.id.btnAddToCart);
            quantitySelector = itemView.findViewById(R.id.quantity_selector);
            tvQuantity = itemView.findViewById(R.id.tv_quantity);
            btnAdd = itemView.findViewById(R.id.btn_add);
            btnRemove = itemView.findViewById(R.id.btn_remove);
        }
    }
}