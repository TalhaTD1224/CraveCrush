package com.talha.cravecrush.utils;
import android.util.Log;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.talha.cravecrush.models.User;
public class FirebaseUtils {
    public interface UserCallback {
        void onComplete(User user);
    }
    public static void getUserById(String userId, UserCallback callback) {
        FirebaseFirestore.getInstance()
                .collection("users")
                .document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        User user = documentSnapshot.toObject(User.class);
                        callback.onComplete(user);
                    } else {
                        callback.onComplete(null);
                    }
                })
                .addOnFailureListener(e -> callback.onComplete(null));
    }
    public static void addUserToFirestore(User user, final OnUserSavedListener listener) {
        FirebaseFirestore.getInstance()
                .collection("users")
                .document(user.getId())
                .set(user)
                .addOnSuccessListener(unused -> listener.onComplete(true))
                .addOnFailureListener(e -> listener.onComplete(false));
    }

    public interface OnUserSavedListener {
        void onComplete(boolean isSuccess);
    }

    public static void sendEmailToAllAdmins(String subject, String body) {
        FirebaseFirestore.getInstance()
                .collection("users")
                .whereEqualTo("role", "admin")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (queryDocumentSnapshots.isEmpty()) {
                        Log.w("EmailUtils", "No admin users found to notify.");
                        return;
                    }
                    for (QueryDocumentSnapshot adminSnapshot : queryDocumentSnapshots) {
                        User admin = adminSnapshot.toObject(User.class);
                        if (admin != null && admin.getEmail() != null && !admin.getEmail().isEmpty()) {
                            EmailUtils.sendOrderConfirmation(admin.getEmail(), subject, body, isSuccess -> {
                                if (isSuccess) {
                                    Log.i("EmailUtils", "Admin notification sent to: " + admin.getEmail());
                                } else {
                                    Log.e("EmailUtils", "Failed to send admin notification to: " + admin.getEmail());
                                }
                            });
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("FirebaseUtils", "Failed to fetch admin users: ", e);
                });
    }
}