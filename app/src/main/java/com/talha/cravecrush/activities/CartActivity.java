package com.talha.cravecrush.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.talha.cravecrush.R;
import com.talha.cravecrush.adapters.CartAdapter;
import com.talha.cravecrush.models.CartItem;
import com.talha.cravecrush.utils.CartManager;

import java.util.List;

public class CartActivity extends AppCompatActivity {

    private RecyclerView rvCartItems;
    private TextView tvCartTotal;
    private Button btnCheckout;
    private Toolbar toolbar;
    private LinearLayout emptyCartView;

    private CartAdapter adapter;
    private List<CartItem> cartItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        toolbar = findViewById(R.id.toolbar_cart);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        rvCartItems = findViewById(R.id.rvCartItems);
        tvCartTotal = findViewById(R.id.tvCartTotal);
        btnCheckout = findViewById(R.id.btnCheckout);
        emptyCartView = findViewById(R.id.empty_cart_view);

        cartItems = CartManager.getInstance().getCartItems();

        // The adapter now takes a `Runnable` to update the total and view
        adapter = new CartAdapter(this, cartItems, this::updateCartView);
        rvCartItems.setLayoutManager(new LinearLayoutManager(this));
        rvCartItems.setAdapter(adapter);

        updateCartView();

        btnCheckout.setOnClickListener(v -> {
            if (cartItems.isEmpty()) {
                Toast.makeText(this, "Cart is empty!", Toast.LENGTH_SHORT).show();
            } else {
                startActivity(new Intent(this, OrderCheckoutActivity.class));
            }
        });
    }

    private void updateCartView() {
        double total = CartManager.getInstance().getTotal();
        tvCartTotal.setText("PKR " + String.format("%.2f", total));

        if (cartItems.isEmpty()) {
            rvCartItems.setVisibility(View.GONE);
            emptyCartView.setVisibility(View.VISIBLE);
        } else {
            rvCartItems.setVisibility(View.VISIBLE);
            emptyCartView.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish(); // Go back to the previous activity
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}