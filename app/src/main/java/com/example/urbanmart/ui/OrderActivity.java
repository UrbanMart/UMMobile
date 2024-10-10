package com.example.urbanmart.ui;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class OrderActivity extends AppCompatActivity {

    private RecyclerView ordersRecyclerView;
    private OrderAdapter orderAdapter;
    private ApiService apiService;
    private String customerId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);

        // Get customerName from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("UrbanMartPrefs", MODE_PRIVATE);
        customerId = sharedPreferences.getString("userId", null);

        ordersRecyclerView = findViewById(R.id.ordersRecyclerView);
        ordersRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        orderAdapter = new OrderAdapter(OrderActivity.this, new ArrayList<>());
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

                    // Filter orders by customerId, ignoring null customerId values
                    List<Order> filteredOrders = new ArrayList<>();
                    for (Order order : allOrders) {
                        Log.d("CustomerId", order.getCustomerId());
                        Log.d("OrderId", order.getOrderId());
                        if (order.getCustomerId() != null && order.getCustomerId().equals(customerId)) {
                            filteredOrders.add(order);
                        }
                    }

                    // Sort the filtered orders by date in descending order
                    Collections.sort(filteredOrders, (o1, o2) -> {
                        try {
                            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
                            inputFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
                            return inputFormat.parse(o2.getOrderDate()).compareTo(inputFormat.parse(o1.getOrderDate()));
                        } catch (ParseException e) {
                            e.printStackTrace();
                            return 0;
                        }
                    });

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
