package com.example.urbanmart.model;

import java.util.List;

public class Order {
    private String customerName;
    private String orderDate;
    private double totalAmount;
    private List<OrderItem> orderItems;
    private String status;

    public Order(String customerName, String orderDate, double totalAmount, List<OrderItem> orderItems, String status) {
        this.customerName = customerName;
        this.orderDate = orderDate;
        this.totalAmount = totalAmount;
        this.orderItems = orderItems;
        this.status = status;
    }

    // Getters and setters (optional)
}


