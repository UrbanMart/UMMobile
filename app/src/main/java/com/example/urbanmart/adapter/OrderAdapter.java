package com.example.urbanmart.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.urbanmart.network.ApiService;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.urbanmart.R;
import com.example.urbanmart.model.Order;
import com.example.urbanmart.ui.UpdateProfileActivity;

import java.io.IOException;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {

    private List<Order> orders;
    private Context context;

    public OrderAdapter(Context context, List<Order> orders) {
        this.context = context;
        this.orders = orders;
    }

    public void setOrders(List<Order> orders) {
        this.orders = orders;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Order order = orders.get(position);

        // Set Customer Name
        holder.customerNameTextView.setText(order.getCustomerName());

        // Format and display order date and time
        String formattedDate = formatOrderDate(order.getOrderDate());
        holder.orderDateTextView.setText(formattedDate);

        // Format and display total amount in LKR
        String formattedAmount = formatCurrency(order.getTotalAmount());
        holder.totalAmountTextView.setText(formattedAmount);

        // Set order status and color based on status
        holder.statusTextView.setText(order.getStatus());
        if (order.getStatus().equalsIgnoreCase("Delivered")) {
            holder.statusTextView.setTextColor(holder.itemView.getContext().getResources().getColor(R.color.green));
        } else {
            holder.statusTextView.setTextColor(holder.itemView.getContext().getResources().getColor(R.color.gray));
        }

        // Show or hide the cancellation button based on the order status
        if (order.getStatus().equalsIgnoreCase("Processing")) {
            holder.requestCancellationButton.setVisibility(View.VISIBLE);
            holder.requestCancellationButton.setEnabled(true);
        } else {
            holder.requestCancellationButton.setVisibility(View.GONE);
            holder.requestCancellationButton.setEnabled(false);
        }

        // Handle "Request Cancellation" button click
        holder.requestCancellationButton.setOnClickListener(v -> {
            requestOrderCancellation(order.getOrderId()); // Use orderId
        });
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    private void requestOrderCancellation(String orderId) {
        ApiService apiService = new ApiService(context);
        apiService.requestOrderCancellation(orderId, new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.e("OrderAdapter", "Failed to request order cancellation", e);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    Log.d("OrderAdapter", "Order cancellation requested successfully");

                    // Inform the user about the cancellation request status
                    ((Activity) context).runOnUiThread(() -> {
                        Toast.makeText(context, "Cancellation request sent successfully", Toast.LENGTH_SHORT).show();
                    });

                    // Start the update activity
                    Intent intent = new Intent(context, UpdateProfileActivity.class);
                    context.startActivity(intent);
                } else {
                    Log.e("OrderAdapter", "Failed to request order cancellation, Response code: " + response.code());
                }
            }
        });
    }

    static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView customerNameTextView, orderDateTextView, totalAmountTextView, statusTextView;
        Button requestCancellationButton;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            customerNameTextView = itemView.findViewById(R.id.customerNameTextView);
            orderDateTextView = itemView.findViewById(R.id.orderDateTextView);
            totalAmountTextView = itemView.findViewById(R.id.totalAmountTextView);
            statusTextView = itemView.findViewById(R.id.statusTextView);
            requestCancellationButton = itemView.findViewById(R.id.requestCancellationButton);
        }
    }

    // Method to format date and time
    private String formatOrderDate(String dateStr) {
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
        inputFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm a", Locale.getDefault());
        try {
            return outputFormat.format(inputFormat.parse(dateStr));
        } catch (ParseException e) {
            e.printStackTrace();
            return dateStr; // Fallback to the original if parsing fails
        }
    }

    // Method to format the currency in LKR
    private String formatCurrency(double amount) {
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("en", "LK"));
        String formattedAmount = currencyFormat.format(amount);

        // Adding a space between the currency symbol and the amount
        if (formattedAmount != null && formattedAmount.length() > 1) {
            String currencySymbol = currencyFormat.getCurrency().getSymbol();
            String amountPart = formattedAmount.substring(currencySymbol.length()).trim();
            return currencySymbol + " " + amountPart; // Add space between them
        }

        return formattedAmount; // Fallback if formatting fails
    }
}
