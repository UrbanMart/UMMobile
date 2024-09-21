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

public class SignUpActivity extends AppCompatActivity {

    private EditText nameEditText, emailEditText, passwordEditText;
    private Button signUpButton;
    private ApiService apiService;
    private Gson gson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        nameEditText = findViewById(R.id.nameEditText);
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        signUpButton = findViewById(R.id.signUpButton);
        Button loginButton = findViewById(R.id.loginButton);

        apiService = new ApiService(this);
        gson = new Gson();

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = nameEditText.getText().toString();
                String role = "Customer";
                String email = emailEditText.getText().toString();
                String password = passwordEditText.getText().toString();
                Boolean isActive = false;

                // Basic validation
                if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(SignUpActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                } else {
                    signUpUser(name,role, email, password,isActive);
                }
            }
        });
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToLoginPage();  // Navigate to LoginActivity
            }
        });
    }

    private void signUpUser(String name,String role, String email, String password, Boolean isActive) {
        User newUser = new User(name,role, email,password, isActive);
        apiService.signUpUser(newUser, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("SignUpActivity", "Sign up request failed", e);
                runOnUiThread(() -> Toast.makeText(SignUpActivity.this, "Sign up failed", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    runOnUiThread(() -> {
                        Toast.makeText(SignUpActivity.this, "Sign up successful", Toast.LENGTH_SHORT).show();
                        goToLoginPage();  // Redirect to login page after successful sign up
                    });
                } else {
                    runOnUiThread(() -> Toast.makeText(SignUpActivity.this, "Sign up failed", Toast.LENGTH_SHORT).show());
                }
            }
        });
    }

    private void goToLoginPage() {
        Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();  // Close the SignUpActivity
    }
}
