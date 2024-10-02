package com.example.urbanmart.ui;

import android.content.Context;
import android.content.Intent;
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

public class LoginActivity extends AppCompatActivity {

    private EditText emailEditText, passwordEditText;
    private ApiService apiService;
    private Gson gson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        Button loginButton = findViewById(R.id.loginButton);
        Button signupButton = findViewById(R.id.signupButton);

        apiService = new ApiService(this);  // Pass context for saving session
        gson = new Gson();

        // Check if user is already logged in
        checkIfLoggedIn();

        signupButton.setOnClickListener(new View.OnClickListener() {  // Add this block
            @Override
            public void onClick(View v) {
                goToSignupPage();  // Navigate to SignupActivity
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailEditText.getText().toString();
                String password = passwordEditText.getText().toString();

                // Basic validation
                if (email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "Please enter both email and password", Toast.LENGTH_SHORT).show();
                } else {
                    loginUser(email, password);
                }
            }
        });
    }

    private void checkIfLoggedIn() {
        SharedPreferences sharedPreferences = getSharedPreferences("UrbanMartPrefs", Context.MODE_PRIVATE);
        String userId = sharedPreferences.getString("userId", null);

        // If userId exists, navigate to HomeActivity
        if (userId != null) {
            goToHomePage();
        }
    }

    private void loginUser(String email, String password) {
        apiService.loginUser(email, password, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("LoginActivity", "Login request failed", e);
                runOnUiThread(() -> Toast.makeText(LoginActivity.this, "Login failed", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    User user = gson.fromJson(responseBody, User.class);
                    saveUserSession(user);  // Save user session
                    runOnUiThread(() -> {
                        Toast.makeText(LoginActivity.this, "Login successful", Toast.LENGTH_SHORT).show();
                        goToHomePage();  // Go to the home page
                    });
                } else {
                    runOnUiThread(() -> Toast.makeText(LoginActivity.this, "Invalid login", Toast.LENGTH_SHORT).show());
                }
            }
        });
    }

    // Save user data in SharedPreferences
    private void saveUserSession(User user) {
        SharedPreferences sharedPreferences = getSharedPreferences("UrbanMartPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("userId", user.getId());
        editor.putString("userName", user.getName());
        editor.putString("userEmail", user.getEmail());
        editor.putBoolean("isActive", user.getIsActive());
        editor.putString("password", user.getPassword());
        editor.putString("role", user.getRole());
        editor.apply();  // Apply changes
    }

    // Navigate to the home page
    private void goToHomePage() {
        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
        startActivity(intent);
        finish();  // Close the LoginActivity
    }

    private void goToSignupPage() {
        Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
        startActivity(intent);
    }
}
