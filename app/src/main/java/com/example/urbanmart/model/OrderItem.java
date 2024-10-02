package com.example.urbanmart.model;

public class OrderItem {
    private String productId;
    private String productName;
    private int quantity;
    private double unitPrice;
    private double totalPrice;

    public OrderItem(String productId, String productName, int quantity, double unitPrice, double totalPrice) {
        this.productId = productId;
        this.productName = productName;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.totalPrice = totalPrice;
    }

    // Getters and setters (optional)
}