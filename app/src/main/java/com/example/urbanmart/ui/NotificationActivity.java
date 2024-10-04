package com.example.urbanmart.ui;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.urbanmart.R;
import com.example.urbanmart.adapter.NotificationAdapter;
import com.example.urbanmart.model.Notification;
import com.example.urbanmart.network.ApiService;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class NotificationActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private NotificationAdapter adapter;
    private List<Notification> notificationList = new ArrayList<>();
    private ApiService apiService;
    private String customerId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        // Get customerName from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("UrbanMartPrefs", MODE_PRIVATE);
        customerId = sharedPreferences.getString("userId", null);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        apiService = new ApiService(this);

        fetchNotifications();
    }

    private void fetchNotifications() {
        apiService.fetchNotificationsForUser(customerId, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String json = response.body().string();
                    Notification[] notifications = new Gson().fromJson(json, Notification[].class);

                    // Filter unread notifications
                    notificationList = Arrays.stream(notifications)
                            .filter(notification -> !notification.isRead())
                            .collect(Collectors.toList());

                    runOnUiThread(() -> {
                        adapter = new NotificationAdapter(notificationList, apiService);
                        recyclerView.setAdapter(adapter);
                    });
                }
            }
        });
    }
}
