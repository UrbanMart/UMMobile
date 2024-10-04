package com.example.urbanmart.adapter;

import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.urbanmart.R;
import com.example.urbanmart.model.Feedback;

import java.util.List;

public class FeedbackAdapter extends RecyclerView.Adapter<FeedbackAdapter.FeedbackViewHolder> {
    private List<Feedback> feedbackList;
    private SharedPreferences sharedPreferences; // Store SharedPreferences

    public FeedbackAdapter(List<Feedback> feedbackList, SharedPreferences sharedPreferences) {
        this.feedbackList = feedbackList;
        this.sharedPreferences = sharedPreferences; // Initialize SharedPreferences
    }

    @NonNull
    @Override
    public FeedbackViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_feedback, parent, false);
        return new FeedbackViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FeedbackViewHolder holder, int position) {
        Feedback feedback = feedbackList.get(position);

        // Check if customer name is null or empty, set to the username from SharedPreferences if so
        String customerName = feedback.getCustomerName();
        if (customerName == null || customerName.isEmpty()) {
            // Get user info from SharedPreferences
            customerName = sharedPreferences.getString("userName", "anonymous");
        }
        holder.customerNameTextView.setText(customerName); // Set the customer name

        holder.commentTextView.setText(feedback.getComment());
        holder.ratingBar.setRating(feedback.getRating());
    }

    @Override
    public int getItemCount() {
        return feedbackList.size();
    }

    static class FeedbackViewHolder extends RecyclerView.ViewHolder {
        TextView customerNameTextView;
        TextView commentTextView;
        RatingBar ratingBar;

        public FeedbackViewHolder(@NonNull View itemView) {
            super(itemView);
            customerNameTextView = itemView.findViewById(R.id.customerNameTextView);
            commentTextView = itemView.findViewById(R.id.commentTextView);
            ratingBar = itemView.findViewById(R.id.ratingBar);
        }
    }
}
