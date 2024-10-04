package com.example.urbanmart.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.urbanmart.R;
import com.example.urbanmart.model.Notification;
import com.example.urbanmart.network.ApiService;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder> {

    private List<Notification> notificationList;
    private ApiService apiService;

    public NotificationAdapter(List<Notification> notifications, ApiService apiService) {
        this.notificationList = notifications;
        this.apiService = apiService;
    }

    @NonNull
    @Override
    public NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.notification_item, parent, false);
        return new NotificationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Notification notification = notificationList.get(position);

        // Set message and formatted date-time
        holder.messageTextView.setText(notification.getMessage());
        holder.createdAtTextView.setText(formatDateTime(notification.getCreatedAt()));

        // Show related order ID if available
        if (notification.getRelatedOrderId() != null && !notification.getRelatedOrderId().isEmpty()) {
            holder.relatedOrderIdTextView.setVisibility(View.VISIBLE);
            holder.relatedOrderIdTextView.setText("Related Order ID: " + notification.getRelatedOrderId());
        } else {
            holder.relatedOrderIdTextView.setVisibility(View.GONE);
        }

        // Mark as read button click listener
        holder.markAsReadButton.setOnClickListener(v -> {
            apiService.markNotificationAsRead(notification.getId(), new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    // Handle failure (optional: you can show a Toast message)
                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    if (response.isSuccessful()) {
                        // Run on the UI thread to update RecyclerView
                        ((Activity) holder.itemView.getContext()).runOnUiThread(() -> {
                            notificationList.remove(position);
                            notifyDataSetChanged();  // Notify the adapter to refresh the whole list
                        });
                    }
                }
            });
        });
    }

    @Override
    public int getItemCount() {
        return notificationList.size();
    }

    public class NotificationViewHolder extends RecyclerView.ViewHolder {

        TextView messageTextView;
        TextView createdAtTextView;
        TextView relatedOrderIdTextView;
        ImageView markAsReadButton;

        public NotificationViewHolder(@NonNull View itemView) {
            super(itemView);
            messageTextView = itemView.findViewById(R.id.messageTextView);
            createdAtTextView = itemView.findViewById(R.id.createdAtTextView);
            relatedOrderIdTextView = itemView.findViewById(R.id.relatedOrderIdTextView);
            markAsReadButton = itemView.findViewById(R.id.deleteIconImageView);
        }
    }

    // Helper method to format date-time strings
    private String formatDateTime(String dateTime) {
        try {
            SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
            isoFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date date = isoFormat.parse(dateTime);

            SimpleDateFormat desiredFormat = new SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault());
            return desiredFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return dateTime;  // Return original date-time if parsing fails
        }
    }
}
