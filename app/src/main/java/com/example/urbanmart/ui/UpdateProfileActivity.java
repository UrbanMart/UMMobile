package com.example.urbanmart.ui;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

        usernameEditText = findViewById(R.id.usernameEditText);
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        updateButton = findViewById(R.id.updateButton);

        apiService = new ApiService(this);
        gson = new Gson();

        // Get user info from SharedPreferences (for example)
        SharedPreferences sharedPreferences = getSharedPreferences("UrbanMartPrefs", MODE_PRIVATE);
        userId = sharedPreferences.getString("userId", "");
        String userName = sharedPreferences.getString("userName", "");
        String userEmail = sharedPreferences.getString("userEmail", "");

        // Pre-fill the current user data
        usernameEditText.setText(userName);
        emailEditText.setText(userEmail);

        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String updatedUsername = usernameEditText.getText().toString().trim();
                String updatedEmail = emailEditText.getText().toString().trim();
                String updatedPassword = passwordEditText.getText().toString().trim();

                if (updatedUsername.isEmpty() || updatedEmail.isEmpty() || updatedPassword.isEmpty()) {
                    Toast.makeText(UpdateProfileActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                } else {
                    updateUserProfile(userId, updatedUsername, updatedEmail, updatedPassword);
                }
            }
        });

    }

    private void updateUserProfile(String userId, String name, String email, String password) {
        User updatedUser = new User(name, email, password);
        apiService.updateUserProfile(userId, updatedUser, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("UpdateProfileActivity", "Update request failed", e);
                runOnUiThread(() -> Toast.makeText(UpdateProfileActivity.this, "Profile update failed", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    runOnUiThread(() -> Toast.makeText(UpdateProfileActivity.this, "Profile updated successfully", Toast.LENGTH_SHORT).show());
                } else {
                    runOnUiThread(() -> Toast.makeText(UpdateProfileActivity.this, "Update failed", Toast.LENGTH_SHORT).show());
                }
            }
        });
    }
}
