package com.example.urbanmart.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.urbanmart.R;
import com.example.urbanmart.adapter.FeedbackAdapter;
import com.example.urbanmart.model.Feedback;
import com.example.urbanmart.model.Product;
import com.example.urbanmart.model.User;
import com.example.urbanmart.network.ApiService;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class ProductDetailActivity extends AppCompatActivity {

    private ImageView productImage;
    private TextView productName, productPrice, productCategory, quantityTextView;
    private Button addToCartButton, increaseQuantityButton, decreaseQuantityButton;
    private Button submitFeedbackButton;
    private EditText feedbackCommentEditText;
    private RatingBar feedbackRatingBar;
    private RecyclerView feedbackRecyclerView;

    private int quantity = 1; // Default quantity
    private String customerId;
    private List<Feedback> feedbackList = new ArrayList<>();
    private FeedbackAdapter feedbackAdapter;
    private String vendorId; // Added for feedback submission
    private ApiService apiService;
    private Gson gson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        apiService = new ApiService(this);
        gson = new Gson();

        // Get customerId from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("UrbanMartPrefs", MODE_PRIVATE);
        customerId = sharedPreferences.getString("userId", null);

        // Initialize views
        productImage = findViewById(R.id.productImage);
        productName = findViewById(R.id.productName);
        productPrice = findViewById(R.id.productPrice);
        productCategory = findViewById(R.id.productCategory);
        quantityTextView = findViewById(R.id.quantityTextView);
        addToCartButton = findViewById(R.id.addToCartButton);
        increaseQuantityButton = findViewById(R.id.increaseQuantityButton);
        decreaseQuantityButton = findViewById(R.id.decreaseQuantityButton);

        // Initialize feedback views
        submitFeedbackButton = findViewById(R.id.submitFeedbackButton);
        feedbackCommentEditText = findViewById(R.id.feedbackCommentEditText);
        feedbackRatingBar = findViewById(R.id.feedbackRatingBar);
        feedbackRecyclerView = findViewById(R.id.feedbackRecyclerView);

        // Set up RecyclerView for feedback
        feedbackRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        feedbackAdapter = new FeedbackAdapter(feedbackList, sharedPreferences); // Pass SharedPreferences
        feedbackRecyclerView.setAdapter(feedbackAdapter);

        // Get the product details from the intent
        String name = getIntent().getStringExtra("product_name");
        double price = getIntent().getDoubleExtra("product_price", 0);
        String imageUrl = getIntent().getStringExtra("product_image");
        String category = getIntent().getStringExtra("product_category");
        vendorId = getIntent().getStringExtra("product_vendorid"); // Save vendorId for feedback submission

        // Set product details to views
        productName.setText(name);
        productPrice.setText(String.format("LKR %.2f", price));
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
                    getIntent().getStringExtra("product_id"),
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

        // Set up feedback submission
        submitFeedbackButton.setOnClickListener(v -> submitFeedback());

        // Fetch existing feedback
        fetchFeedback(vendorId);
    }

    private void addToCart(Product product) {
        SharedPreferences sharedPreferences = getSharedPreferences("UrbanMartPrefs", MODE_PRIVATE);
        String cartJson = sharedPreferences.getString("cart", "[]");

        Gson gson = new Gson();
        Type type = new TypeToken<List<Product>>() {}.getType();
        List<Product> cart = gson.fromJson(cartJson, type);

        if (cart == null) {
            cart = new ArrayList<>(); // Initialize the cart if it's null
        }

        for (int i = 0; i < cart.size(); i++) {
            Product cartProduct = cart.get(i);
            if (cartProduct.getId().equals(product.getId())) { // Compare product IDs
                cart.remove(i); // Remove the existing product
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

    private void submitFeedback() {
        String comment = feedbackCommentEditText.getText().toString().trim();
        int rating = (int) feedbackRatingBar.getRating();

        if (comment.isEmpty() || rating == 0) {
            Toast.makeText(this, "Please enter feedback and rating", Toast.LENGTH_SHORT).show();
            return;
        }

        Feedback feedback = new Feedback();
        feedback.setVendorId(vendorId);
        feedback.setCustomerId(customerId);
        feedback.setComment(comment);
        feedback.setRating(rating);

        apiService.postVendorFeedback(feedback, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("Feedback Submission Error", e.getMessage(), e);
                runOnUiThread(() -> {
                    Toast.makeText(ProductDetailActivity.this, "Failed to submit feedback", Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        Feedback submittedFeedback = gson.fromJson(response.body().string(), Feedback.class);
                        // Add the new feedback to the top of the list
                        feedbackList.add(0, submittedFeedback);
                        runOnUiThread(() -> {
                            feedbackAdapter.notifyItemInserted(0); // Notify adapter that an item was inserted at position 0
                            feedbackCommentEditText.setText("");
                            feedbackRatingBar.setRating(0);
                            feedbackRecyclerView.setVisibility(View.VISIBLE); // Ensure the RecyclerView is visible
                            Toast.makeText(ProductDetailActivity.this, "Feedback submitted successfully", Toast.LENGTH_SHORT).show();
                        });
                    } catch (Exception e) {
                        Log.e("Feedback Parsing Error", e.getMessage(), e);
                    }
                } else {
                    Log.e("Feedback Submission", "Response not successful");
                }
            }
        });
    }

    private void fetchFeedback(String vendorId) {
        apiService.fetchVendorFeedback(vendorId, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> {
                    Log.e("Feedback Fetch", "Failed to fetch feedback");
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful() && response.body() != null) {
                    Feedback[] feedbackArray = gson.fromJson(response.body().string(), Feedback[].class);
                    feedbackList.clear();

                    // Convert to a list and reverse it
                    List<Feedback> feedbackTempList = new ArrayList<>(Arrays.asList(feedbackArray));
                    Collections.reverse(feedbackTempList); // Reverse the list

                    // Fetch customer names for each feedback
                    for (Feedback feedback : feedbackTempList) {
                        fetchCustomerName(feedback); // Fetch name asynchronously
                    }
                } else {
                    Log.e("Feedback Fetch", "Failed to fetch feedback");
                }
            }
        });
    }

    // New method to fetch customer name by ID
    private void fetchCustomerName(Feedback feedback) {
        apiService.fetchUsersByIds(feedback.getCustomerId(), new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("Customer Fetch", "Failed to fetch customer name");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful() && response.body() != null) {
                    // Assuming the response body is a JSON object representing the user
                    String responseBody = response.body().string();
                    // Parse the JSON to get the name
                    JsonObject jsonObject = new JsonParser().parse(responseBody).getAsJsonObject();
                    String customerName = jsonObject.get("name").getAsString(); // Extract the name

                    feedback.setCustomerName(customerName); // Set the customer name in feedback
                    feedbackList.add(feedback); // Add feedback to the list

                    runOnUiThread(() -> {
                        feedbackAdapter.notifyItemInserted(feedbackList.size() - 1); // Notify adapter
                        feedbackRecyclerView.setVisibility(View.VISIBLE); // Ensure the RecyclerView is visible
                    });
                } else {
                    Log.e("Customer Fetch", "Failed to fetch customer name");
                }
            }
        });
    }
}
