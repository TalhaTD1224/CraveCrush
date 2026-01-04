package com.talha.cravecrush.admin;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;
import com.talha.cravecrush.R;
import com.talha.cravecrush.models.MenuItem;

public class AdminAddEditItemActivity extends AppCompatActivity {

    private EditText etTitle, etDescription, etPrice, etImageUrl;
    private Spinner spCategory;
    private Button btnSave;

    private FirebaseFirestore db;
    private String menuItemId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_add_edit_item);

        etTitle = findViewById(R.id.etTitle);
        etDescription = findViewById(R.id.etDescription);
        etPrice = findViewById(R.id.etPrice);
        etImageUrl = findViewById(R.id.etImageUrl);
        spCategory = findViewById(R.id.spCategory);
        btnSave = findViewById(R.id.btnSave);

        db = FirebaseFirestore.getInstance();

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.categories, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spCategory.setAdapter(adapter);

        menuItemId = getIntent().getStringExtra("menuItemId");

        if (menuItemId != null) {
            loadMenuItem();
        }

        btnSave.setOnClickListener(v -> saveMenuItem());
    }

    private void loadMenuItem() {
        db.collection("menuItems").document(menuItemId).get().addOnSuccessListener(documentSnapshot -> {
            MenuItem item = documentSnapshot.toObject(MenuItem.class);
            if (item != null) {
                etTitle.setText(item.getTitle());
                etDescription.setText(item.getDescription());
                etPrice.setText(String.valueOf(item.getPrice()));
                etImageUrl.setText(item.getImageUrl());
                // Set spinner selection
                String[] categories = getResources().getStringArray(R.array.categories);
                for (int i = 0; i < categories.length; i++) {
                    if (categories[i].equals(item.getCategory())) {
                        spCategory.setSelection(i);
                        break;
                    }
                }
            }
        });
    }

    private void saveMenuItem() {
        String title = etTitle.getText().toString();
        String description = etDescription.getText().toString();
        double price = Double.parseDouble(etPrice.getText().toString());
        String imageUrl = etImageUrl.getText().toString();
        String category = spCategory.getSelectedItem().toString();

        MenuItem item;
        if (menuItemId != null) {
            item = new MenuItem(menuItemId, title, description, price, category, imageUrl);
        } else {
            String id = db.collection("menuItems").document().getId();
            item = new MenuItem(id, title, description, price, category, imageUrl);
        }

        db.collection("menuItems").document(item.getId()).set(item).addOnSuccessListener(aVoid -> {
            Toast.makeText(this, "Menu item saved", Toast.LENGTH_SHORT).show();
            finish();
        });
    }
}