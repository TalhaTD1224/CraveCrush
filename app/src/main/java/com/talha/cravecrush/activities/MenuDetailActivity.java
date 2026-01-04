package com.talha.cravecrush.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.talha.cravecrush.R;
import com.talha.cravecrush.utils.CartManager;

public class MenuDetailActivity extends AppCompatActivity {

    private ImageView ivFoodImage;
    private TextView tvTitle, tvCategory, tvPrice, tvDescription;
    private Button btnAddToCart;
    private Toolbar toolbar;

    private com.talha.cravecrush.models.MenuItem currentMenuItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_detail);

        toolbar = findViewById(R.id.toolbar_menu_detail);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        ivFoodImage = findViewById(R.id.ivFoodImage);
        tvTitle = findViewById(R.id.tvTitle);
        tvCategory = findViewById(R.id.tvCategory);
        tvPrice = findViewById(R.id.tvPrice);
        tvDescription = findViewById(R.id.tvDescription);
        btnAddToCart = findViewById(R.id.btnAddToCart);

        // Get the MenuItem object from the intent
        currentMenuItem = (com.talha.cravecrush.models.MenuItem) getIntent().getSerializableExtra("menuItem");

        if (currentMenuItem != null) {
            populateUI(currentMenuItem);
        } else {
            Toast.makeText(this, "Failed to load item details.", Toast.LENGTH_SHORT).show();
            finish();
        }

        btnAddToCart.setOnClickListener(v -> {
            if (currentMenuItem != null) {
                CartManager.getInstance().addToCart(currentMenuItem, 1);
                Toast.makeText(this, currentMenuItem.getTitle() + " added to cart!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void populateUI(com.talha.cravecrush.models.MenuItem item) {
        if (isFinishing() || isDestroyed()) {
            return;
        }

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(item.getTitle());
        }

        tvTitle.setText(item.getTitle());
        tvCategory.setText(item.getCategory());
        tvPrice.setText("PKR " + item.getPrice());
        tvDescription.setText(item.getDescription());

        Glide.with(this)
                .load(item.getImageUrl())
                .placeholder(R.drawable.logo) // Use app logo as a placeholder
                .into(ivFoodImage);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish(); // Go back to the previous activity
            return true;
        } else if (id == R.id.action_cart) {
            startActivity(new Intent(this, CartActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}