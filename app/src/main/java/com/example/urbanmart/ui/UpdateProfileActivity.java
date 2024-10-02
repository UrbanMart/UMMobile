package com.example.urbanmart.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.urbanmart.R;
import com.example.urbanmart.model.User;
import com.example.urbanmart.network.ApiService;
import com.google.gson.Gson;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import java.io.IOException;

public class UpdateProfileActivity extends AppCompatActivity {

    private EditText usernameEditText, emailEditText, passwordEditText;
    private Button updateButton;
    private ApiService apiService;
    private Gson gson;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);

        // Apply system window insets for edge-to-edge layout
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        ImageView notificationIcon = findViewById(R.id.notificationIcon);
        ImageView historyImage = findViewById(R.id.historyImage);

        // Set up click listener for notification icon
        notificationIcon.setOnClickListener(v -> {
            Intent intent = new Intent(UpdateProfileActivity.this, NotificationActivity.class);
            startActivity(intent);
        });

        // Set up click listener for history icon
        historyImage.setOnClickListener(v -> {
            Intent intent = new Intent(UpdateProfileActivity.this, OrderActivity.class);
            startActivity(intent);
        });

        usernameEditText = findViewById(R.id.usernameEditText);
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        updateButton = findViewById(R.id.updateButton);
        Button logoutButton = findViewById(R.id.logoutButton); // Initialize the logout button

        apiService = new ApiService(this);
        gson = new Gson();

        // Get user info from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("UrbanMartPrefs", MODE_PRIVATE);
        userId = sharedPreferences.getString("userId", "");
        String userRole = sharedPreferences.getString("role", "");
        String userName = sharedPreferences.getString("userName", "");
        String userEmail = sharedPreferences.getString("userEmail", "");
        String userPassword = sharedPreferences.getString("password", "");
        Boolean userIsActive = sharedPreferences.getBoolean("isActive", false);

        // Pre-fill the current user data
        usernameEditText.setText(userName);
        emailEditText.setText(userEmail);
        passwordEditText.setText(userPassword);

        updateButton.setOnClickListener(v -> {
            String updatedUsername = usernameEditText.getText().toString().trim();
            String updatedEmail = emailEditText.getText().toString().trim();
            String updatedPassword = passwordEditText.getText().toString().trim();

            if (updatedUsername.isEmpty() || updatedEmail.isEmpty() || updatedPassword.isEmpty()) {
                Toast.makeText(UpdateProfileActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            } else {
                updateUserProfile(userId, updatedUsername, userRole, updatedEmail, updatedPassword, userIsActive);
            }
        });

        // Set up logout button click listener
        logoutButton.setOnClickListener(v -> {
            clearSession();
            goToLoginActivity();
        });
    }

    private void clearSession() {
        SharedPreferences sharedPreferences = getSharedPreferences("UrbanMartPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear(); // Clear all saved preferences
        editor.apply();
    }

    private void goToLoginActivity() {
        Intent intent = new Intent(UpdateProfileActivity.this, LoginActivity.class);
        startActivity(intent);
        finish(); // Close the UpdateProfileActivity
    }

    private void updateUserProfile(String userId, String name, String role, String email, String password, Boolean isActive) {
        User updatedUser = new User(userId, name, role, email, password, isActive);
        apiService.updateUserProfile(userId, updatedUser, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("UpdateProfileActivity", "Update request failed", e);
                runOnUiThread(() -> Toast.makeText(UpdateProfileActivity.this, "Profile update failed", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    runOnUiThread(() -> {
                        Toast.makeText(UpdateProfileActivity.this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
                        saveUserSession(updatedUser); // Save updated user data to cache
                        goToHomeActivity(); // Navigate to HomeActivity after success
                    });
                } else {
                    runOnUiThread(() -> Toast.makeText(UpdateProfileActivity.this, "Update failed", Toast.LENGTH_SHORT).show());
                }
            }
        });
    }

    private void goToHomeActivity() {
        Intent intent = new Intent(UpdateProfileActivity.this, HomeActivity.class);
        startActivity(intent);
        finish(); // Close the UpdateProfileActivity
    }

    private void saveUserSession(User user) {
        SharedPreferences sharedPreferences = getSharedPreferences("UrbanMartPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("userName", user.getName());
        editor.putString("userEmail", user.getEmail());
        editor.putString("password", user.getPassword());
        editor.apply();  // Apply changes
    }
}
