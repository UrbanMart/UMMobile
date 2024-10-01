package com.example.urbanmart.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.urbanmart.R;
import com.example.urbanmart.model.Product;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    private List<Product> cartItems;
    private Context context;

    public CartAdapter(List<Product> cartItems, Context context) {
        this.cartItems = cartItems;
        this.context = context;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cart, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        Product product = cartItems.get(position);
        holder.productNameTextView.setText(product.getName());
        holder.productPriceTextView.setText(String.format("$%.2f", product.getPrice()));
        holder.productQuantityTextView.setText("Quantity: 1");

        holder.removeButton.setOnClickListener(v -> {
            // Remove the item from the list
            cartItems.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, cartItems.size());

            // Update SharedPreferences
            updateCartInPreferences(cartItems);
        });
    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    private void updateCartInPreferences(List<Product> updatedCart) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        // Convert the updated cart list to JSON
        Gson gson = new Gson();
        String updatedCartJson = gson.toJson(updatedCart);

        // Save the updated cart JSON to SharedPreferences
        editor.putString("cart", updatedCartJson);
        editor.apply(); // Apply the changes asynchronously
    }

    static class CartViewHolder extends RecyclerView.ViewHolder {
        TextView productNameTextView;
        TextView productPriceTextView;
        TextView productQuantityTextView;
        Button removeButton;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            productNameTextView = itemView.findViewById(R.id.productNameTextView);
            productPriceTextView = itemView.findViewById(R.id.productPriceTextView);
            productQuantityTextView = itemView.findViewById(R.id.productQuantityTextView);
            removeButton = itemView.findViewById(R.id.removeButton);
        }
    }
}
