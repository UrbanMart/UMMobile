package com.example.urbanmart.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.urbanmart.R;
import com.example.urbanmart.adapter.ProductAdapter;
import com.example.urbanmart.model.Product;
import com.example.urbanmart.network.ApiService;
import com.google.gson.Gson;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class HomeActivity extends AppCompatActivity {

    private RecyclerView productRecyclerView;
    private ApiService apiService;
    private Gson gson;
    private TextView userNameTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Initialize views
        productRecyclerView = findViewById(R.id.productRecyclerView);
        userNameTextView = findViewById(R.id.userNameTextView);

        apiService = new ApiService(this);
        gson = new Gson();

        // Retrieve user details from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("UrbanMartPrefs", MODE_PRIVATE);
        String userName = sharedPreferences.getString("userName", "User");
        userNameTextView.setText("Welcome, " + userName);

        // Set OnClickListener for userNameTextView
        userNameTextView.setOnClickListener(v -> goToUpdateActivity()); // Add this line

        // Set up RecyclerView with GridLayoutManager
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2); // 2 columns
        productRecyclerView.setLayoutManager(gridLayoutManager);

        // Load products
        loadProducts();
    }

    private void goToUpdateActivity() {
        Intent intent = new Intent(HomeActivity.this, ProfileActivity.class);
        startActivity(intent);
    }


    private void loadProducts() {
        apiService.fetchProducts(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                // Handle failure
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    Product[] products = gson.fromJson(responseBody, Product[].class);
                    List<Product> productList = Arrays.asList(products);

                    runOnUiThread(() -> {
                        ProductAdapter adapter = new ProductAdapter(productList);
                        productRecyclerView.setAdapter(adapter);
                    });
                }
            }
        });
    }
}
