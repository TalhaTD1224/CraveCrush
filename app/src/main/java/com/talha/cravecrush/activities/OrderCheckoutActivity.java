package com.talha.cravecrush.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.talha.cravecrush.R;
import com.talha.cravecrush.models.CartItem;
import com.talha.cravecrush.models.Order;
import com.talha.cravecrush.models.OrderItem;
import com.talha.cravecrush.models.User;
import com.talha.cravecrush.utils.CartManager;
import com.talha.cravecrush.utils.EmailUtils;
import com.talha.cravecrush.utils.FirebaseUtils;

import java.util.ArrayList;
import java.util.List;

public class OrderCheckoutActivity extends AppCompatActivity {

    private EditText etName, etEmail, etMobile, etAddress, etNote;
    private TextView tvTotalPrice;
    private Button btnConfirmOrder;
    private Toolbar toolbar;

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    private double totalAmount;
    private List<CartItem> cartItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_checkout);

        toolbar = findViewById(R.id.toolbar_checkout);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        etMobile = findViewById(R.id.etMobile);
        etAddress = findViewById(R.id.etAddress);
        etNote = findViewById(R.id.etNote);
        tvTotalPrice = findViewById(R.id.tvTotalPrice);
        btnConfirmOrder = findViewById(R.id.btnConfirmOrder);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        cartItems = CartManager.getInstance().getCartItems();
        totalAmount = CartManager.getInstance().getTotal();

        tvTotalPrice.setText(String.format("Total: Rs. %.2f", totalAmount));

        // --- Pre-fill user data if they are logged in ---
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            loadUserData(currentUser.getUid());
        }

        btnConfirmOrder.setOnClickListener(v -> placeOrder());
    }

    private void loadUserData(String userId) {
        FirebaseUtils.getUserById(userId, user -> {
            if (user != null) {
                etName.setText(user.getName());
                etEmail.setText(user.getEmail());
                etMobile.setText(user.getMobileNumber());
            }
        });
    }

    private void placeOrder() {
        String name = etName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String mobile = etMobile.getText().toString().trim();
        String address = etAddress.getText().toString().trim();
        String note = etNote.getText().toString().trim();

        if (name.isEmpty() || email.isEmpty() || mobile.isEmpty() || address.isEmpty()) {
            Toast.makeText(this, "Please fill all required fields", Toast.LENGTH_SHORT).show();
            return;
        }

        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Placing your order...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        List<OrderItem> orderItems = new ArrayList<>();
        for (CartItem ci : this.cartItems) {
            orderItems.add(new OrderItem(
                    ci.getMenuItem().getId(),
                    ci.getMenuItem().getTitle(),
                    ci.getMenuItem().getPrice(),
                    ci.getQuantity()
            ));
        }

        String orderId = db.collection("orders").document().getId();
        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        String userId = (firebaseUser != null) ? firebaseUser.getUid() : null;

        Order order;
        if (userId != null) {
            order = new Order(orderId, userId, name, email, address, mobile, note, totalAmount, "pending", Timestamp.now(), orderItems);
        } else {
            order = new Order(orderId, name, email, address, mobile, note, totalAmount, "pending", Timestamp.now(), orderItems);
        }

        db.collection("orders").document(orderId).set(order)
                .addOnSuccessListener(aVoid -> {
                    progressDialog.dismiss();
                    Toast.makeText(this, "Order placed successfully!", Toast.LENGTH_SHORT).show();
                    CartManager.getInstance().clearCart();

                    sendConfirmationEmails(name, email, orderId, totalAmount);

                    Intent intent = new Intent(this, OrderSuccessActivity.class);
                    intent.putExtra("orderId", orderId);
                    startActivity(intent);
                    finish();
                })
                .addOnFailureListener(e -> {
                    progressDialog.dismiss();
                    Toast.makeText(this, "Failed to place order: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void sendConfirmationEmails(String customerName, String customerEmail, String orderId, double total) {
        String customerSubject = "Your CraveCrush Order is Confirmed!";
        String customerBody = "Dear " + customerName + ",\n\nThank you for your order! Please wait for conformation.\n\nOrder ID: " + orderId + "\nTotal Amount: Rs. " + String.format("%.2f", total) + "\n\nWe\'ll notify you once it\'s on its way.\n\n- The CraveCrush Team";

        EmailUtils.sendOrderConfirmation(customerEmail, customerSubject, customerBody, isSuccess -> {
            if (isSuccess) {
                Log.d("EmailUtils", "Customer confirmation email sent.");
            } else {
                Log.e("EmailUtils", "Failed to send customer confirmation email.");
            }
        });

        String adminSubject = "New Order Received: " + orderId;
        String adminBody = "A new order has been placed:\n\nCustomer: " + customerName + "\nOrder ID: " + orderId + "\nTotal: Rs. " + String.format("%.2f", total);
        
        FirebaseUtils.sendEmailToAllAdmins(adminSubject, adminBody);
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