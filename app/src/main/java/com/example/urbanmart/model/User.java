package com.example.urbanmart.model;

public class User {
    private String id;
    private String email;
    private String password;
    private String name;
    private String role;
    private boolean isActive;

    // Constructor for sign-up (no id)
    public User(String name, String role, String email, String password, boolean isActive) {
        this.name = name;
        this.role = role;
        this.email = email;
        this.password = password;
        this.isActive = isActive;
    }

    // Constructor for update (with id)
    public User(String id, String name, String role, String email, String password, boolean isActive) {
        this.id = id;
        this.name = name;
        this.role = role;
        this.email = email;
        this.password = password;
        this.isActive = isActive;
    }

    public String getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getName() {
        return name;
    }

    public String getRole() {
        return role;
    }

    public boolean getIsActive() {
        return isActive;
    }

}

