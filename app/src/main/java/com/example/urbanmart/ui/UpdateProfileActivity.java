package com.example.urbanmart.ui;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.urbanmart.R;
import com.example.urbanmart.model.User;
import com.example.urbanmart.network.ApiService;
import com.google.gson.Gson;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import java.io.IOException;

public class UpdateProfileActivity extends AppCompatActivity {

    private EditText nameEditText, emailEditText;
    private Button updateProfileButton;
    private ApiService apiService;
    private Gson gson;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);

        nameEditText = findViewById(R.id.nameEditText);
        emailEditText = findViewById(R.id.emailEditText);
        updateProfileButton = findViewById(R.id.updateProfileButton);

        apiService = new ApiService(this);
        gson = new Gson();

        // Load user info from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("UrbanMartPrefs", MODE_PRIVATE);
        userId = sharedPreferences.getString("userId", null);
        String userName = sharedPreferences.getString("userName", "");
        String userEmail = sharedPreferences.getString("userEmail", "");

        // Populate fields with user data
        nameEditText.setText(userName);
        emailEditText.setText(userEmail);

        updateProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String updatedName = nameEditText.getText().toString();
                String updatedEmail = emailEditText.getText().toString();

                // Basic validation
                if (updatedName.isEmpty() || updatedEmail.isEmpty()) {
                    Toast.makeText(UpdateProfileActivity.this, "Please fill out all fields", Toast.LENGTH_SHORT).show();
                } else {
                    updateUserProfile(userId, updatedName, updatedEmail);
                }
            }
        });
    }

    private void updateUserProfile(String userId, String name, String email) {
        User updatedUser = new User(name, email);
        apiService.updateUserProfile(userId, updatedUser, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("UpdateProfileActivity", "Profile update failed", e);
                runOnUiThread(() -> Toast.makeText(UpdateProfileActivity.this, "Profile update failed", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    runOnUiThread(() -> {
                        Toast.makeText(UpdateProfileActivity.this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
                        // Optionally, update SharedPreferences with the new info
                        saveUpdatedUserInfo(name, email);
                    });
                } else {
                    runOnUiThread(() -> Toast.makeText(UpdateProfileActivity.this, "Profile update failed", Toast.LENGTH_SHORT).show());
                }
            }
        });
    }

    // Save updated user info in SharedPreferences
    private void saveUpdatedUserInfo(String name, String email) {
        SharedPreferences sharedPreferences = getSharedPreferences("UrbanMartPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("userName", name);
        editor.putString("userEmail", email);
        editor.apply();
    }
}
