package com.example.urbanmart.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.urbanmart.R;
import com.example.urbanmart.model.Order;

import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {

    private List<Order> orders;

    public OrderAdapter(List<Order> orders) {
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

        // Set order status
        holder.statusTextView.setText(order.getStatus());
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    static class OrderViewHolder extends RecyclerView.ViewHolder {

        TextView customerNameTextView, orderDateTextView, totalAmountTextView, statusTextView;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            customerNameTextView = itemView.findViewById(R.id.customerNameTextView);
            orderDateTextView = itemView.findViewById(R.id.orderDateTextView);
            totalAmountTextView = itemView.findViewById(R.id.totalAmountTextView);
            statusTextView = itemView.findViewById(R.id.statusTextView);
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
        return currencyFormat.format(amount);
    }
}
