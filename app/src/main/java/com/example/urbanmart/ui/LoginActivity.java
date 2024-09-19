package com.example.urbanmart.ui;

import android.content.Intent;
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
    private Button loginButton;
    private ApiService apiService;
    private Gson gson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        loginButton = findViewById(R.id.loginButton);

        apiService = new ApiService();
        gson = new Gson();

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailEditText.getText().toString();
                String password = passwordEditText.getText().toString();
                loginUser(email, password);
            }
        });
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
                    runOnUiThread(() -> {
                        Toast.makeText(LoginActivity.this, "Login successful", Toast.LENGTH_SHORT).show();
                        goToHomePage(user.getName());
                    });
                } else {
                    runOnUiThread(() -> Toast.makeText(LoginActivity.this, "Invalid login", Toast.LENGTH_SHORT).show());
                }
            }
        });
    }

    private void goToHomePage(String userName) {
        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
        intent.putExtra("USER_NAME", userName);
        startActivity(intent);
    }
}
