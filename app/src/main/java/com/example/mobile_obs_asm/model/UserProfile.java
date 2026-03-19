package com.example.mobile_obs_asm.model;

public class UserProfile {

    private final String name;
    private final String role;
    private final String email;
    private final String city;
    private final int completedOrders;
    private final int savedListings;

    public UserProfile(String name, String role, String email, String city, int completedOrders, int savedListings) {
        this.name = name;
        this.role = role;
        this.email = email;
        this.city = city;
        this.completedOrders = completedOrders;
        this.savedListings = savedListings;
    }

    public String getName() {
        return name;
    }

    public String getRole() {
        return role;
    }

    public String getEmail() {
        return email;
    }

    public String getCity() {
        return city;
    }

    public int getCompletedOrders() {
        return completedOrders;
    }

    public int getSavedListings() {
        return savedListings;
    }

    public String getInitials() {
        String[] parts = name.trim().split("\\s+");
        if (parts.length == 0) {
            return "OB";
        }
        if (parts.length == 1) {
            return parts[0].substring(0, Math.min(2, parts[0].length())).toUpperCase();
        }
        return (parts[0].substring(0, 1) + parts[parts.length - 1].substring(0, 1)).toUpperCase();
    }
}
