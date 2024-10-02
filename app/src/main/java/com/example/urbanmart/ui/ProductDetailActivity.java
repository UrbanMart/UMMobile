package com.example.urbanmart.ui;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
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

        // Set up the "Add to Cart" button click listener
        addToCartButton.setOnClickListener(v -> {
            // Create a new product object with the selected quantity
            Product product = new Product(name, price * quantity, category, imageUrl);

            // Add the product to the cart
            addToCart(product);
        });
    }

    private void addToCart(Product product) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String cartJson = sharedPreferences.getString("cart", "[]");

        Gson gson = new Gson();
        Type type = new TypeToken<List<Product>>() {}.getType();
        List<Product> cart = gson.fromJson(cartJson, type);

        if (cart == null) {
            cart = new ArrayList<>(); // Initialize the cart if it's null
        }

        boolean productExists = false; // Flag to check if the product exists

        for (Product cartProduct : cart) {
            // Check if the product already exists in the cart (based on name or some unique identifier)
            if (cartProduct.getName().equals(product.getName())) {
                cartProduct.setPrice(cartProduct.getPrice() + product.getPrice()); // Update the price by adding new price
                productExists = true; // Set the flag to true
                break; // Exit the loop since we found the product
            }
        }

        if (!productExists) {
            cart.add(product); // Add the new product to the cart if it doesn't exist
        }

        // Save the updated cart back to SharedPreferences
        String updatedCartJson = gson.toJson(cart);
        sharedPreferences.edit().putString("cart", updatedCartJson).apply(); // Apply the changes
    }


}
