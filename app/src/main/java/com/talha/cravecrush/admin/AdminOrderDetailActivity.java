package com.talha.cravecrush.admin;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.talha.cravecrush.R;
import com.talha.cravecrush.adapters.OrderItemAdapter;
import com.talha.cravecrush.models.Order;
import com.talha.cravecrush.utils.EmailUtils;

public class AdminOrderDetailActivity extends AppCompatActivity {

    private TextView tvOrderId, tvOrderStatus, tvOrderTotal, tvUserName, tvUserEmail, tvUserAddress;
    private RecyclerView rvOrderItems;
    private Button btnApprove, btnReject;
    private Toolbar toolbar;

    private FirebaseFirestore db;
    private String orderId;
    private Order currentOrder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_order_detail);

        toolbar = findViewById(R.id.toolbar_order_detail);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        toolbar.setNavigationOnClickListener(v -> finish());

        tvOrderId = findViewById(R.id.tvOrderId);
        tvOrderStatus = findViewById(R.id.tvOrderStatus);
        tvOrderTotal = findViewById(R.id.tvOrderTotal);
        tvUserName = findViewById(R.id.tvUserName);
        tvUserEmail = findViewById(R.id.tvUserEmail);
        tvUserAddress = findViewById(R.id.tvUserAddress);
        rvOrderItems = findViewById(R.id.rvOrderItems);
        btnApprove = findViewById(R.id.btnApprove);
        btnReject = findViewById(R.id.btnReject);

        db = FirebaseFirestore.getInstance();

        orderId = getIntent().getStringExtra("orderId");

        if (orderId != null) {
            loadOrderDetails();
        }

        btnApprove.setOnClickListener(v -> handleOrderAction("approved"));
        btnReject.setOnClickListener(v -> handleOrderAction("rejected"));
    }

    private void loadOrderDetails() {
        db.collection("orders").document(orderId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        currentOrder = documentSnapshot.toObject(Order.class);
                        if (currentOrder != null) {
                            currentOrder.setId(documentSnapshot.getId());
                            populateUI();
                        }
                    }
                });
    }

    private void populateUI() {
        tvOrderId.setText("Order ID: #" + currentOrder.getId().substring(0, 6));
        tvOrderStatus.setText("Status: " + currentOrder.getStatus());
        tvOrderTotal.setText("Total: PKR " + String.format("%.2f", currentOrder.getTotalPrice()));
        tvUserName.setText("Name: " + currentOrder.getGuestName());
        tvUserEmail.setText("Email: " + currentOrder.getGuestEmail());
        tvUserAddress.setText("Address: " + currentOrder.getAddress());

        OrderItemAdapter adapter = new OrderItemAdapter(this, currentOrder.getItems());
        rvOrderItems.setLayoutManager(new LinearLayoutManager(this));
        rvOrderItems.setAdapter(adapter);
    }

    private void handleOrderAction(final String newStatus) {
        if (currentOrder == null) return;

        db.collection("orders").document(currentOrder.getId())
                .update("status", newStatus)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Order " + newStatus, Toast.LENGTH_SHORT).show();

                    String customerEmail = currentOrder.getGuestEmail();
                    String customerName = currentOrder.getGuestName();
                    String subject;
                    String body;

                    if ("approved".equals(newStatus)) {
                        subject = "Your CraveCrush Order is Approved!";
                        body = "Dear " + customerName + ",\n\nYour order has been approved and will be ready and delivered in 20 mins. Thanks";
                    } else { // rejected
                        subject = "Regarding your CraveCrush Order";
                        body = "Dear " + customerName + ",\n\nWe are sorry, but your order can not be sent right now due to work load....";
                    }

                    EmailUtils.sendOrderConfirmation(customerEmail, subject, body, isSuccess -> {
                        if (isSuccess) {
                            Log.d("AdminOrderDetail", "Status update email sent to " + customerEmail);
                        } else {
                            Log.e("AdminOrderDetail", "Failed to send status update email to " + customerEmail);
                        }
                    });
                    finish(); // Go back to the previous screen
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to update status", Toast.LENGTH_SHORT).show();
                });
    }
}