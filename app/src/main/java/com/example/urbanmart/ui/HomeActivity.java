package com.example.urbanmart.ui;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.example.urbanmart.R;

public class HomeActivity extends AppCompatActivity {

    private TextView welcomeTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        welcomeTextView = findViewById(R.id.welcomeTextView);

        // Get the username passed from the LoginActivity
        String userName = getIntent().getStringExtra("USER_NAME");

        // Display the username
        welcomeTextView.setText("Welcome, " + userName + "!");
    }
}
