package com.example.urbanmart.model;

public class OrderItem {
    private String productId;
    private String productName;
    private  String vendorId;
    private  String status;
    private int quantity;
    private double unitPrice;
    private double totalPrice;

    public OrderItem(String productId, String productName,String vendorId, int quantity, double unitPrice, double totalPrice,String status) {
        this.productId = productId;
        this.productName = productName;
        this.vendorId = vendorId;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.totalPrice = totalPrice;
        this.status = status;
    }

    // Getters
    public String getProductId() {
        return productId;
    }

    public String getProductName() {
        return productName;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    // Setters
    public void setProductId(String productId) {
        this.productId = productId;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void setUnitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }
}
