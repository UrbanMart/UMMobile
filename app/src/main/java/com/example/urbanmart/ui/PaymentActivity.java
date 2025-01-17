package com.example.urbanmart.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.urbanmart.R;
import com.example.urbanmart.network.ApiService;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import java.io.IOException;

public class PaymentActivity extends AppCompatActivity {

    private EditText cardNumberEditText, expirationDateEditText, cvvEditText;
    private Button proceedButton;
    private ApiService apiService;
    private String orderJson; // Hold the order JSON

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        // Initialize views
        cardNumberEditText = findViewById(R.id.cardNumberEditText);
        expirationDateEditText = findViewById(R.id.expirationDateEditText);
        cvvEditText = findViewById(R.id.cvvEditText);
        proceedButton = findViewById(R.id.proceedButton);

        // Initialize ApiService
        apiService = new ApiService(this);

        // Retrieve the order JSON string passed from CartActivity
        orderJson = getIntent().getStringExtra("orderJson");

        // Set up click listener for Proceed button
        proceedButton.setOnClickListener(v -> {
            if (validateCardDetails()) {
                // Perform the API call to post the order after successful validation
                submitOrder();
            }
        });

        // Add TextWatcher to handle expiration date input
        expirationDateEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // No action needed before text changes
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // No action needed while text is changing
            }

            @Override
            public void afterTextChanged(Editable s) {
                String text = s.toString();
                // Add '/' after the month if the length is 2 and does not end with '/'
                if (text.length() == 2 && !text.endsWith("/")) {
                    text += "/";
                    expirationDateEditText.setText(text);
                    expirationDateEditText.setSelection(text.length()); // Move cursor to the end
                }
                // Limit to 5 characters (MM/YY)
                if (text.length() > 5) {
                    expirationDateEditText.setText(text.substring(0, 5));
                    expirationDateEditText.setSelection(5);
                }
            }
        });

        // Limit card number input to 16 digits
        cardNumberEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // No action needed before text changes
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // No action needed while text is changing
            }

            @Override
            public void afterTextChanged(Editable s) {
                String text = s.toString();
                // Limit to 16 digits
                if (text.length() > 16) {
                    cardNumberEditText.setText(text.substring(0, 16));
                    cardNumberEditText.setSelection(16);
                }
            }
        });
    }

    // Method to validate card details
    private boolean validateCardDetails() {
        String cardNumber = cardNumberEditText.getText().toString().trim();
        String expirationDate = expirationDateEditText.getText().toString().trim();
        String cvv = cvvEditText.getText().toString().trim();

        // Basic validation
        if (TextUtils.isEmpty(cardNumber) || cardNumber.length() != 16) {
            Toast.makeText(this, "Invalid card number", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (TextUtils.isEmpty(expirationDate) || !expirationDate.matches("(0[1-9]|1[0-2])/[0-9]{2}")) {
            Toast.makeText(this, "Invalid expiration date", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (TextUtils.isEmpty(cvv) || cvv.length() != 3) {
            Toast.makeText(this, "Invalid CVV", Toast.LENGTH_SHORT).show();
            return false;
        }

        // If all validations pass
        return true;
    }

    // Method to submit the order to the server
    private void submitOrder() {
        apiService.submitOrder(orderJson, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                // Handle failure in API call
                runOnUiThread(() -> {
                    Toast.makeText(PaymentActivity.this, "Failed to submit order", Toast.LENGTH_LONG).show();
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    // On success, show toast and potentially navigate the user
                    runOnUiThread(() -> {
                        Toast.makeText(PaymentActivity.this, "Order submitted successfully!", Toast.LENGTH_LONG).show();
                        clearCartCache();
                        goToOrderActivity();
                    });
                } else {
                    // Handle error response
                    runOnUiThread(() -> {
                        Toast.makeText(PaymentActivity.this, "Error submitting order", Toast.LENGTH_LONG).show();
                    });
                }
            }
        });
    }

    private void clearCartCache() {
        SharedPreferences sharedPreferences = getSharedPreferences("UrbanMartPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove("cart"); // Remove the cart key to clear the cart data
        editor.apply(); // Apply the changes

        Toast.makeText(PaymentActivity.this, "Cart has been cleared!", Toast.LENGTH_SHORT).show();
    }

    // Method to navigate to OrderActivity
    private void goToOrderActivity() {
        Intent intent = new Intent(PaymentActivity.this, OrderActivity.class);
        startActivity(intent);
        finish(); // Close PaymentActivity to prevent going back to it
    }
}
