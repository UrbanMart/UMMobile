package com.example.urbanmart.model;
public class User {
    private String id;
    private String email;
    private String password;
    private String name;
    private String role = "Customer"; // Default role
    private boolean isActive = true;  // Default active status

    // Constructor for SignUp
    public User(String name, String email) {
        this.name = name;
        this.email = email;
        this.password = password;
    }

    // Getters
    public String getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

    public boolean isActive() {
        return isActive;
    }
}
