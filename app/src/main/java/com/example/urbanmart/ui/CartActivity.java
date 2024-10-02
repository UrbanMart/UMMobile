package com.example.urbanmart.ui;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.Button;
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
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String cartJson = sharedPreferences.getString("cart", "[]");

        Gson gson = new Gson();
        Type type = new TypeToken<List<Product>>() {}.getType();
        List<Product> cart = gson.fromJson(cartJson, type);

        if (cart == null || cart.isEmpty()) {
            checkoutButton.setText("Checkout - $0.00");
            return;
        }

        totalPrice = 0;
        for (Product product : cart) {
            totalPrice += product.getPrice();
        }

        checkoutButton.setText(String.format("Checkout - $%.2f", totalPrice));

        cartAdapter = new CartAdapter(cart, this, this);
        cartRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        cartRecyclerView.setAdapter(cartAdapter);
    }

    private void checkout() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String cartJson = sharedPreferences.getString("cart", "[]");

        Toast.makeText(this, "Cart Items: " + cartJson, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onTotalPriceUpdated(double amount) {
        totalPrice += amount;
        checkoutButton.setText(String.format("Checkout - $%.2f", totalPrice));
    }
}
