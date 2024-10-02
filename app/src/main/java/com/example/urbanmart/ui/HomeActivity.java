package com.example.urbanmart.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.urbanmart.R;
import com.example.urbanmart.adapter.ProductAdapter;
import com.example.urbanmart.model.Product;
import com.example.urbanmart.network.ApiService;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class HomeActivity extends AppCompatActivity {

    private RecyclerView productRecyclerView;
    private ApiService apiService;
    private Gson gson;
    private TextView userNameTextView;
    private ImageView cartIcon;
    private ImageView userImage;
    private EditText searchBar; // Declare search bar
    private List<Product> productList; // Original product list

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        userImage = findViewById(R.id.menuImage);
        cartIcon = findViewById(R.id.cartIcon);
        productRecyclerView = findViewById(R.id.productRecyclerView);
        searchBar = findViewById(R.id.searchBar); // Initialize the search bar

        apiService = new ApiService(this);
        gson = new Gson();

        SharedPreferences sharedPreferences = getSharedPreferences("UrbanMartPrefs", MODE_PRIVATE);

        userImage.setOnClickListener(v -> goToUpdateActivity());
        cartIcon.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, CartActivity.class);
            startActivity(intent);
        });

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        productRecyclerView.setLayoutManager(gridLayoutManager);

        loadProducts(); // Load products

        // Set up a TextWatcher for the search bar
        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // No action needed
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // Call the filter method when the text changes
                filterProducts(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {
                // No action needed
            }
        });
    }

    private void goToUpdateActivity() {
        Intent intent = new Intent(HomeActivity.this, UpdateProfileActivity.class);
        startActivity(intent);
    }

    private void loadProducts() {
        apiService.fetchProducts(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> Toast.makeText(HomeActivity.this, "Failed to load products", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    Product[] products = gson.fromJson(responseBody, Product[].class);
                    productList = Arrays.asList(products); // Save original product list

                    runOnUiThread(() -> {
                        ProductAdapter adapter = new ProductAdapter(productList);
                        productRecyclerView.setAdapter(adapter);
                    });
                }
            }
        });
    }

    private void filterProducts(String query) {
        List<Product> filteredList = new ArrayList<>();
        for (Product product : productList) {
            if (product.getName().toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(product);
            }
        }
        // Update the adapter with the filtered list
        ProductAdapter adapter = new ProductAdapter(filteredList);
        productRecyclerView.setAdapter(adapter);
    }
}
