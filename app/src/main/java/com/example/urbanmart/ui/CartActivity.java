package com.example.urbanmart.ui;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.urbanmart.R;
import com.example.urbanmart.adapter.CartAdapter;
import com.example.urbanmart.model.Product;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.List;

public class CartActivity extends AppCompatActivity {

    private RecyclerView cartRecyclerView;
    private CartAdapter cartAdapter;
    private TextView totalPriceTextView;
    private Button checkoutButton; // Declare checkout button

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        cartRecyclerView = findViewById(R.id.cartRecyclerView);
        totalPriceTextView = findViewById(R.id.totalPriceTextView);
        checkoutButton = findViewById(R.id.checkoutButton); // Initialize checkout button

        loadCartItems();

        // Set up the click listener for the checkout button
        checkoutButton.setOnClickListener(v -> checkout());
    }

    private void loadCartItems() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String cartJson = sharedPreferences.getString("cart", "[]");

        Gson gson = new Gson();
        Type type = new TypeToken<List<Product>>() {}.getType();
        List<Product> cart = gson.fromJson(cartJson, type);

        // Check if the cart is empty
        if (cart == null || cart.isEmpty()) {
            totalPriceTextView.setText("$0.00"); // Set total price to $0.00
            return; // Exit the method early if the cart is empty
        }

        double totalPrice = 0;
        for (Product product : cart) {
            totalPrice += product.getPrice();
        }

        totalPriceTextView.setText(String.format("$%.2f", totalPrice));

        cartAdapter = new CartAdapter(cart, this);
        cartRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        cartRecyclerView.setAdapter(cartAdapter);
    }

    private void checkout() {
        // Get the cart items again
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String cartJson = sharedPreferences.getString("cart", "[]");

        // Show a Toast with the cart JSON
        Toast.makeText(this, "Cart Items: " + cartJson, Toast.LENGTH_LONG).show();
    }
}
