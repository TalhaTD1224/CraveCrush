package com.talha.cravecrush.admin;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.talha.cravecrush.R;
import com.talha.cravecrush.adapters.AdminMenuAdapter;

import java.util.ArrayList;
import java.util.List;

public class AdminManageMenuActivity extends AppCompatActivity {

    private RecyclerView rvAdminMenuItems;
    private FloatingActionButton fabAddNewItem;
    private Toolbar toolbar;

    private AdminMenuAdapter adapter;
    private List<com.talha.cravecrush.models.MenuItem> menuItemList;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_manage_menu);

        toolbar = findViewById(R.id.toolbar_manage_menu);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        rvAdminMenuItems = findViewById(R.id.rvAdminMenuItems);
        fabAddNewItem = findViewById(R.id.fabAddNewItem);

        db = FirebaseFirestore.getInstance();
        menuItemList = new ArrayList<>();

        adapter = new AdminMenuAdapter(this, menuItemList);
        rvAdminMenuItems.setLayoutManager(new LinearLayoutManager(this));
        rvAdminMenuItems.setAdapter(adapter);

        fabAddNewItem.setOnClickListener(v -> {
            startActivity(new Intent(this, AdminAddEditItemActivity.class));
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadMenuItems();
    }

    private void loadMenuItems() {
        db.collection("menuItems").get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    menuItemList.clear();
                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        com.talha.cravecrush.models.MenuItem item = doc.toObject(com.talha.cravecrush.models.MenuItem.class);
                        if (item != null) {
                            item.setId(doc.getId());
                            menuItemList.add(item);
                        }
                    }
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(AdminManageMenuActivity.this, "Failed to load menu items.", Toast.LENGTH_SHORT).show();
                });
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