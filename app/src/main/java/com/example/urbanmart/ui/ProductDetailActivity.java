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
    private TextView productName, productPrice, productCategory;
    private Button addToCartButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        productImage = findViewById(R.id.productImage);
        productName = findViewById(R.id.productName);
        productPrice = findViewById(R.id.productPrice);
        productCategory = findViewById(R.id.productCategory);
        addToCartButton = findViewById(R.id.addToCartButton); // Make sure this button exists in your layout

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
                    .placeholder(R.drawable.placeholder_image) // Use the placeholder image while loading
                    .into(productImage);
        }

        // Set up the "Add to Cart" button click listener
        addToCartButton.setOnClickListener(v -> {
            // Create a new product object
            Product product = new Product(name, price, category, imageUrl);

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

        cart.add(product); // Add the new product to the cart

        // Save the updated cart back to SharedPreferences
        String updatedCartJson = gson.toJson(cart);
        sharedPreferences.edit().putString("cart", updatedCartJson).apply(); // Apply the changes
    }
}
