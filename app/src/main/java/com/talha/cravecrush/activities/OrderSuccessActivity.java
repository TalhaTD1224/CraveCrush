package com.talha.cravecrush.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.talha.cravecrush.R;

public class OrderSuccessActivity extends AppCompatActivity {

    private TextView tvOrderId, tvSuccessMessage;
    private Button btnHome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_success);

        tvOrderId = findViewById(R.id.tvOrderId);
        tvSuccessMessage = findViewById(R.id.tvSuccessMessage);
        btnHome = findViewById(R.id.btnHome);

        // Get the orderId from intent
        String orderId = getIntent().getStringExtra("orderId");

        if (orderId != null) {
            tvOrderId.setText("Order ID: #" + orderId);
        }

        btnHome.setOnClickListener(v -> {
            // Go back to dashboard
            Intent intent = new Intent(OrderSuccessActivity.this, DashboardActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });
    }
}