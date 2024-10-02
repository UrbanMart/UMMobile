package com.example.urbanmart.ui;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.urbanmart.R;
import com.example.urbanmart.adapter.OrderAdapter;
import com.example.urbanmart.model.Order;
import com.example.urbanmart.network.ApiService;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class OrderActivity extends AppCompatActivity {

    private RecyclerView ordersRecyclerView;
    private OrderAdapter orderAdapter;
    private ApiService apiService;
    private String customerName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);

        // Get customerName from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("UrbanMartPrefs", MODE_PRIVATE);
        customerName = sharedPreferences.getString("userName", "User");

        ordersRecyclerView = findViewById(R.id.ordersRecyclerView);
        ordersRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        orderAdapter = new OrderAdapter(new ArrayList<>());
        ordersRecyclerView.setAdapter(orderAdapter);

        apiService = new ApiService(this);
        fetchAndFilterOrders();
    }

    private void fetchAndFilterOrders() {
        apiService.fetchOrders(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> Toast.makeText(OrderActivity.this, "Failed to fetch orders", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseData = response.body().string();
                    Type orderListType = new TypeToken<List<Order>>() {}.getType();
                    List<Order> allOrders = new Gson().fromJson(responseData, orderListType);

                    // Filter orders by customerName
                    List<Order> filteredOrders = new ArrayList<>();
                    for (Order order : allOrders) {
                        if (order.getCustomerName().equals(customerName)) {
                            filteredOrders.add(order);
                        }
                    }

                    runOnUiThread(() -> {
                        orderAdapter.setOrders(filteredOrders);
                        orderAdapter.notifyDataSetChanged();
                    });
                } else {
                    runOnUiThread(() -> Toast.makeText(OrderActivity.this, "Failed to fetch orders", Toast.LENGTH_SHORT).show());
                }
            }
        });
    }
}
