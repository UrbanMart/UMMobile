package com.example.urbanmart.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.urbanmart.R;

public class PaymentActivity extends AppCompatActivity {

    private TextView orderJsonTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        orderJsonTextView = findViewById(R.id.orderJsonTextView);

        // Retrieve the JSON string passed from CartActivity
        String orderJson = getIntent().getStringExtra("orderJson");
        if (orderJson != null) {
            orderJsonTextView.setText(orderJson); // Display the JSON in the TextView
        }
    }
}
