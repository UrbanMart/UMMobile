package com.example.urbanmart.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.urbanmart.R;

public class ProfileActivity extends AppCompatActivity {

    private TextView userNameTextView, userEmailTextView;
    private Button updateProfileButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Find views by ID
        userNameTextView = findViewById(R.id.txtFullName);
        userEmailTextView = findViewById(R.id.txtEmail);
        updateProfileButton = findViewById(R.id.btnEditInfo);

        // Fetch user info from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("UrbanMartPrefs", MODE_PRIVATE);
        String userName = sharedPreferences.getString("userName", "Default Name");
        String userEmail = sharedPreferences.getString("userEmail", "Default Email");

        // Display user info
        userNameTextView.setText(userName);
        userEmailTextView.setText(userEmail);

        // Set up the update profile button click listener
        updateProfileButton.setOnClickListener(v -> {
            // Navigate to UpdateProfileActivity when button is clicked
            Intent intent = new Intent(ProfileActivity.this, UpdateProfileActivity.class);
            startActivity(intent);
        });
    }
}
