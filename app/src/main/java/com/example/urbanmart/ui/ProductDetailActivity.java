package com.example.urbanmart.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.example.urbanmart.R;
import com.example.urbanmart.model.Product;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ProductDetailActivity extends AppCompatActivity {

    private ImageView productImage;
    private TextView productName, productPrice, productCategory, quantityTextView;
    private Button addToCartButton, increaseQuantityButton, decreaseQuantityButton;
    private int quantity = 1; // Default quantity

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        productImage = findViewById(R.id.productImage);
        productName = findViewById(R.id.productName);
        productPrice = findViewById(R.id.productPrice);
        productCategory = findViewById(R.id.productCategory);
        quantityTextView = findViewById(R.id.quantityTextView);
        addToCartButton = findViewById(R.id.addToCartButton);
        increaseQuantityButton = findViewById(R.id.increaseQuantityButton);
        decreaseQuantityButton = findViewById(R.id.decreaseQuantityButton);

        // Get the product details from the intent
        String name = getIntent().getStringExtra("product_name");
        double price = getIntent().getDoubleExtra("product_price", 0);
        String imageUrl = getIntent().getStringExtra("product_image");
        String category = getIntent().getStringExtra("product_category");

        // Set product details to views
        productName.setText(name);
        productPrice.setText(String.format("$%.2f", price));
        productCategory.setText(category);

        // Load the product image or a placeholder if the URL is empty or null
        if (imageUrl == null || imageUrl.isEmpty()) {
            productImage.setImageResource(R.drawable.placeholder_image); // Set placeholder image
        } else {
            Glide.with(this)
                    .load(imageUrl)
                    .placeholder(R.drawable.placeholder_image)
                    .into(productImage);
        }

        // Set up quantity adjustment buttons
        increaseQuantityButton.setOnClickListener(v -> {
            quantity++;
            quantityTextView.setText(String.valueOf(quantity));
        });

        decreaseQuantityButton.setOnClickListener(v -> {
            if (quantity > 1) {
                quantity--;
                quantityTextView.setText(String.valueOf(quantity));
            }
        });

        addToCartButton.setOnClickListener(v -> {
            // Create a new product object with the selected quantity
            Product product = new Product(
                    getIntent().getStringExtra("product_id"), // Add product ID here
                    name,
                    price * quantity,
                    category,
                    imageUrl
            );

            // Add the product to the cart and navigate to the cart
            addToCart(product);
            Toast.makeText(this, "Product added to cart!", Toast.LENGTH_SHORT).show();

            // Start CartActivity
            Intent intent = new Intent(ProductDetailActivity.this, CartActivity.class);
            startActivity(intent);
            finish(); // Optionally, finish this activity if you don't want to return here
        });

    }

    private void addToCart(Product product) {
        SharedPreferences sharedPreferences = getSharedPreferences("UrbanMartPrefs", MODE_PRIVATE);
        String cartJson = sharedPreferences.getString("cart", "[]");

        Log.d("SharedPreferencesDebug", "Cart JSON: " + cartJson); // Fix this line

        Gson gson = new Gson();
        Type type = new TypeToken<List<Product>>() {}.getType();
        List<Product> cart = gson.fromJson(cartJson, type);

        if (cart == null) {
            cart = new ArrayList<>(); // Initialize the cart if it's null
        }

        // Flag to check if the product exists
        boolean productExists = false;

        for (int i = 0; i < cart.size(); i++) {
            Product cartProduct = cart.get(i);
            if (cartProduct.getId().equals(product.getId())) { // Compare product IDs
                cart.remove(i); // Remove the existing product
                productExists = true; // Set the flag to true
                break; // Exit the loop since we found and removed the product
            }
        }

        // Set the quantity for the new product
        product.setQuantity(quantity);
        // Add the new product to the cart
        cart.add(product);

        // Save the updated cart back to SharedPreferences
        String updatedCartJson = gson.toJson(cart);
        sharedPreferences.edit().putString("cart", updatedCartJson).apply(); // Apply changes
    }



}
