package com.talha.cravecrush.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.talha.cravecrush.R;
import com.talha.cravecrush.adapters.CategoryAdapter;
import com.talha.cravecrush.adapters.MenuAdapter;
import com.talha.cravecrush.models.Category;
import com.talha.cravecrush.models.User;
import com.talha.cravecrush.utils.CartManager;
import com.talha.cravecrush.utils.FirebaseUtils;
import com.talha.cravecrush.utils.SessionManager;

import java.util.ArrayList;
import java.util.List;

public class DashboardActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, CartManager.CartListener, CategoryAdapter.OnCategoryClickListener {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;
    private TextView cartBadge;

    private RecyclerView rvMenuItems, rvCategories;
    private MenuAdapter menuAdapter;
    private CategoryAdapter categoryAdapter;
    private List<com.talha.cravecrush.models.MenuItem> menuItemList;
    private List<Category> categoryList;
    private FirebaseFirestore db;

    private FirebaseAuth mAuth;
    private SessionManager session;
    private CartManager cartManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        cartBadge = findViewById(R.id.cart_badge);

        // --- Setup Navigation Drawer ---
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // --- Click Listeners for Cart ---
        findViewById(R.id.fabCart).setOnClickListener(v -> 
            startActivity(new Intent(DashboardActivity.this, CartActivity.class)));

        findViewById(R.id.ivCart).setOnClickListener(v -> 
            startActivity(new Intent(DashboardActivity.this, CartActivity.class)));


        // --- Initialize Firebase, Session, and Cart ---
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        session = new SessionManager(this);
        cartManager = CartManager.getInstance();
        cartManager.setListener(this);

        // --- Setup Menu RecyclerView ---
        rvMenuItems = findViewById(R.id.rvMenuItems);
        menuItemList = new ArrayList<>();
        menuAdapter = new MenuAdapter(this, menuItemList);
        rvMenuItems.setLayoutManager(new GridLayoutManager(this, 2));
        rvMenuItems.setAdapter(menuAdapter);

        // --- Setup Category RecyclerView ---
        rvCategories = findViewById(R.id.rvCategories);
        setupCategories();

        loadMenuItems();
        updateBadge();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateNavHeader();
        onCartChanged(); // Update badge on resume
        menuAdapter.notifyDataSetChanged(); // Refresh menu items to show correct quantity
    }

    private void setupCategories() {
        categoryList = new ArrayList<>();
        categoryList.add(new Category("All", R.drawable.ic_all));
        categoryList.add(new Category("Deals", R.drawable.ic_deals));
        categoryList.add(new Category("Burger", R.drawable.ic_burger));
        categoryList.add(new Category("Pizza", R.drawable.ic_pizza));
        categoryList.add(new Category("Drinks", R.drawable.ic_drinks));
        categoryList.add(new Category("Dessert", R.drawable.ic_dessert));

        categoryAdapter = new CategoryAdapter(this, categoryList, this);
        rvCategories.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rvCategories.setAdapter(categoryAdapter);
    }

    @Override
    public void onCategoryClick(String category) {
        menuAdapter.filter(category);
    }

    private void updateBadge() {
        int itemCount = cartManager.getTotalItemCount();
        if (itemCount > 0) {
            cartBadge.setText(String.valueOf(itemCount));
            cartBadge.setVisibility(View.VISIBLE);
        } else {
            cartBadge.setVisibility(View.GONE);
        }
    }

    @Override
    public void onCartChanged() {
        updateBadge();
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
                    menuAdapter.setOriginalList(new ArrayList<>(menuItemList));
                    menuAdapter.filter("All"); // Initially show all items
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to load menu", Toast.LENGTH_SHORT).show());
    }

    private void updateNavHeader() {
        View headerView = navigationView.getHeaderView(0);
        TextView tvUserName = headerView.findViewById(R.id.tvUserName);
        TextView tvUserEmail = headerView.findViewById(R.id.tvUserEmail);

        FirebaseUser currentUser = mAuth.getCurrentUser();
        boolean isLoggedIn = currentUser != null;

        navigationView.getMenu().setGroupVisible(R.id.group_guest, !isLoggedIn);
        navigationView.getMenu().setGroupVisible(R.id.group_user, isLoggedIn);

        if (isLoggedIn) {
            String uid = currentUser.getUid();
            FirebaseUtils.getUserById(uid, user -> {
                if (user != null) {
                    tvUserName.setText(user.getName());
                    tvUserEmail.setText(user.getEmail());
                } else {
                    tvUserName.setText("User");
                    tvUserEmail.setText(currentUser.getEmail());
                }
            });

            MenuItem adminPanelItem = navigationView.getMenu().findItem(R.id.nav_admin_panel);
            if (adminPanelItem != null) {
                adminPanelItem.setVisible("admin".equals(session.getUserRole()));
            }
        } else {
            tvUserName.setText("CraveCrush");
            tvUserEmail.setText("guest@cravecrush.com");
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            // Already here
        } else if (id == R.id.nav_cart) {
            startActivity(new Intent(this, CartActivity.class));
        } else if (id == R.id.nav_login) {
            startActivity(new Intent(this, LoginActivity.class));
        } else if (id == R.id.nav_signup) {
            startActivity(new Intent(this, SignupActivity.class));
        } else if (id == R.id.nav_order_history) {
            startActivity(new Intent(this, OrderHistoryActivity.class));
        } else if (id == R.id.nav_admin_panel) {
            startActivity(new Intent(this, AdminActivity.class));
        } else if (id == R.id.nav_logout) {
            mAuth.signOut();
            session.clearSession();
            Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show();
            updateNavHeader(); // Refresh the drawer
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}