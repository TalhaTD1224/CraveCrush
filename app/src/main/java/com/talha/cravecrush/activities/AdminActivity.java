package com.talha.cravecrush.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.talha.cravecrush.R;
import com.talha.cravecrush.adapters.AdminOrderAdapter;
import com.talha.cravecrush.admin.AdminManageMenuActivity;
import com.talha.cravecrush.models.Order;
import com.talha.cravecrush.utils.SessionManager;

import java.util.ArrayList;
import java.util.List;

public class AdminActivity extends AppCompatActivity {

    private RecyclerView rvAdminOrders;
    private List<Order> orderList;
    private AdminOrderAdapter adapter;
    private Button btnLogout, btnManageMenu;

    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SessionManager session = new SessionManager(this);
        if (!session.getUserRole().equals("admin")) {
            Toast.makeText(this, "Access denied", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        setContentView(R.layout.activity_admin);

        rvAdminOrders = findViewById(R.id.rvAdminOrders);
        btnLogout = findViewById(R.id.btnLogout);
        btnManageMenu = findViewById(R.id.btnManageMenu);
        db = FirebaseFirestore.getInstance();

        btnLogout.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            new SessionManager(this).clearSession();

            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });

        btnManageMenu.setOnClickListener(v -> {
            startActivity(new Intent(this, AdminManageMenuActivity.class));
        });

        orderList = new ArrayList<>();
        adapter = new AdminOrderAdapter(this, orderList);

        rvAdminOrders.setLayoutManager(new LinearLayoutManager(this));
        rvAdminOrders.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadOrders();
    }

    private void loadOrders() {
        db.collection("orders")
                .whereEqualTo("status", "pending") // Only show pending orders
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    orderList.clear();
                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        Order order = doc.toObject(Order.class);
                        if (order != null) {
                            order.setId(doc.getId());
                            orderList.add(order);
                        }
                    }
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Error loading orders", Toast.LENGTH_SHORT).show());
    }
}