package com.example.urbanmart.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.urbanmart.R;
import com.example.urbanmart.adapter.CartAdapter;
import com.example.urbanmart.model.Order;
import com.example.urbanmart.model.OrderItem;
import com.example.urbanmart.model.Product;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class CartActivity extends AppCompatActivity implements CartAdapter.CartTotalPriceListener {

    private RecyclerView cartRecyclerView;
    private CartAdapter cartAdapter;
    private Button checkoutButton;
    private double totalPrice = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        cartRecyclerView = findViewById(R.id.cartRecyclerView);
        checkoutButton = findViewById(R.id.checkoutButton);

        loadCartItems();

        checkoutButton.setOnClickListener(v -> checkout());
    }

    private void loadCartItems() {
        SharedPreferences sharedPreferences = getSharedPreferences("UrbanMartPrefs", MODE_PRIVATE);
        String cartJson = sharedPreferences.getString("cart", "[]");

        try {
            Gson gson = new Gson();
            Type type = new TypeToken<List<Product>>() {}.getType();
            List<Product> cart = gson.fromJson(cartJson, type);

            if (cart == null || cart.isEmpty()) {
                checkoutButton.setText("Checkout");
                totalPrice = 0; // Reset total price
                return;
            }

            totalPrice = 0; // Reset total price before calculation
            for (Product product : cart) {
                totalPrice += product.getPrice(); // Calculate total price based on quantity
            }

            checkoutButton.setText(String.format("Checkout - LKR %.2f", totalPrice)); // Update checkout button text

            cartAdapter = new CartAdapter(cart, this, this);
            cartRecyclerView.setLayoutManager(new LinearLayoutManager(this));
            cartRecyclerView.setAdapter(cartAdapter);

        } catch (JsonSyntaxException e) {
            // Handle the case where JSON parsing fails
            Toast.makeText(this, "Error loading cart", Toast.LENGTH_SHORT).show();
        }
    }

    private void checkout() {
        SharedPreferences sharedPreferences = getSharedPreferences("UrbanMartPrefs", MODE_PRIVATE);
        String cartJson = sharedPreferences.getString("cart", "[]");
        String userName = sharedPreferences.getString("userName", "User");
        String customerId = sharedPreferences.getString("userId", null);

        Gson gson = new Gson();
        Type type = new TypeToken<List<Product>>() {}.getType();
        List<Product> cart = gson.fromJson(cartJson, type);

        if (cart == null || cart.isEmpty()) {
            Toast.makeText(this, "Cart is empty", Toast.LENGTH_SHORT).show();
            return;
        }

        List<OrderItem> orderItems = new ArrayList<>();
        for (Product product : cart) {
            orderItems.add(new OrderItem(product.getId(), product.getName(), product.getVendorId(), product.getQuantity(), product.getPrice()/ product.getQuantity(), product.getPrice(),"Created"));
        }

        String orderDate = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            orderDate = java.time.Instant.now().toString();
        }
        Order order = new Order(customerId, userName, orderDate, totalPrice, orderItems, "Processing");

        // Show confirmation dialog before finalizing the order
        new AlertDialog.Builder(this)
                .setTitle("Confirm Order")
                .setMessage("Are you sure you want to place this order?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    String orderJson = gson.toJson(order);
                    Log.d("OrderDetails", orderJson);
                    Intent intent = new Intent(CartActivity.this, PaymentActivity.class);
                    intent.putExtra("orderJson", orderJson);
                    startActivity(intent);
                })
                .setNegativeButton("No", null)
                .show();
    }

    @Override
    public void onTotalPriceUpdated(double amount) {
        totalPrice += amount;
        checkoutButton.setText(String.format("Checkout - LKR %.2f", totalPrice)); // Update checkout button text
    }
}
