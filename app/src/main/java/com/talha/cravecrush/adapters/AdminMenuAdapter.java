package com.talha.cravecrush.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.FirebaseFirestore;
import com.talha.cravecrush.R;
import com.talha.cravecrush.admin.AdminAddEditItemActivity;
import com.talha.cravecrush.models.MenuItem;

import java.util.List;

public class AdminMenuAdapter extends RecyclerView.Adapter<AdminMenuAdapter.AdminMenuViewHolder> {

    private Context context;
    private List<MenuItem> menuItems;

    public AdminMenuAdapter(Context context, List<MenuItem> menuItems) {
        this.context = context;
        this.menuItems = menuItems;
    }

    @NonNull
    @Override
    public AdminMenuViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_admin_menu, parent, false);
        return new AdminMenuViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdminMenuViewHolder holder, int position) {
        MenuItem item = menuItems.get(position);
        holder.tvTitle.setText(item.getTitle());
        holder.tvPrice.setText("Rs. " + item.getPrice());

        Glide.with(context).load(item.getImageUrl()).into(holder.ivImage);

        holder.btnEdit.setOnClickListener(v -> {
            Intent intent = new Intent(context, AdminAddEditItemActivity.class);
            intent.putExtra("menuItemId", item.getId());
            context.startActivity(intent);
        });

        holder.btnDelete.setOnClickListener(v -> {
            FirebaseFirestore.getInstance().collection("menuItems").document(item.getId()).delete()
                    .addOnSuccessListener(aVoid -> {
                        if (position != RecyclerView.NO_POSITION) {
                            menuItems.remove(position);
                            notifyItemRemoved(position);
                            notifyItemRangeChanged(position, menuItems.size());
                            Toast.makeText(context, "Item deleted", Toast.LENGTH_SHORT).show();
                        }
                    });
        });
    }

    @Override
    public int getItemCount() {
        return menuItems.size();
    }

    static class AdminMenuViewHolder extends RecyclerView.ViewHolder {
        ImageView ivImage;
        TextView tvTitle, tvPrice;
        ImageButton btnEdit, btnDelete;

        public AdminMenuViewHolder(@NonNull View itemView) {
            super(itemView);
            ivImage = itemView.findViewById(R.id.ivImage);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}