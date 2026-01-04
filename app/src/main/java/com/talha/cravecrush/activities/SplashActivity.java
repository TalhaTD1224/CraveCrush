package com.talha.cravecrush.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.talha.cravecrush.R;
import com.talha.cravecrush.utils.SessionManager;

public class SplashActivity extends AppCompatActivity {

    private static final int SPLASH_DELAY = 2000; // 2 seconds

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Handler().postDelayed(() -> {
            SessionManager session = new SessionManager(this);
            String role = session.getUserRole();

            // If a user is logged in and their role is 'admin', send them to the AdminActivity.
            // Otherwise, send everyone (customers and guests) to the main DashboardActivity.
            if (FirebaseAuth.getInstance().getCurrentUser() != null && "admin".equals(role)) {
                startActivity(new Intent(this, AdminActivity.class));
            } else {
                startActivity(new Intent(this, DashboardActivity.class));
            }
            finish();
        }, SPLASH_DELAY);
    }
}