package com.talha.cravecrush.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.talha.cravecrush.R;
import com.talha.cravecrush.adapters.OrderAdapter;
import com.talha.cravecrush.models.Order;

import java.util.ArrayList;
import java.util.List;

public class OrderHistoryActivity extends AppCompatActivity {

    private RecyclerView rvOrders;
    private View emptyOrdersView;
    private Toolbar toolbar;

    private OrderAdapter adapter;
    private List<Order> orderList;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_history);

        toolbar = findViewById(R.id.toolbar_order_history);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        rvOrders = findViewById(R.id.rvOrders);
        emptyOrdersView = findViewById(R.id.tvNoOrders); // The ID of the LinearLayout

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        orderList = new ArrayList<>();
        adapter = new OrderAdapter(this, orderList);

        rvOrders.setLayoutManager(new LinearLayoutManager(this));
        rvOrders.setAdapter(adapter);

        loadOrders();
    }

    private void loadOrders() {
        String currentUserId = mAuth.getCurrentUser() != null ?
                mAuth.getCurrentUser().getUid() : null;

        if (currentUserId == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            emptyOrdersView.setVisibility(View.VISIBLE);
            rvOrders.setVisibility(View.GONE);
            return;
        }

        db.collection("orders")
                .whereEqualTo("userId", currentUserId)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    orderList.clear();
                    for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                        Order order = doc.toObject(Order.class);
                        if (order != null) {
                            order.setId(doc.getId());
                            orderList.add(order);
                        }
                    }

                    adapter.notifyDataSetChanged();

                    if (orderList.isEmpty()) {
                        emptyOrdersView.setVisibility(View.VISIBLE);
                        rvOrders.setVisibility(View.GONE);
                    } else {
                        emptyOrdersView.setVisibility(View.GONE);
                        rvOrders.setVisibility(View.VISIBLE);
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to load orders", Toast.LENGTH_SHORT).show();
                    emptyOrdersView.setVisibility(View.VISIBLE);
                    rvOrders.setVisibility(View.GONE);
                });
    }
}