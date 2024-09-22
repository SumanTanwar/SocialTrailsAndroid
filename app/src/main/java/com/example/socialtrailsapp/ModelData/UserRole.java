package com.example.socialtrailsapp.ModelData;

public enum UserRole {
    USER("user"),
    MODERATOR("moderator"),
    ADMIN("admin");

    private final String role;

    UserRole(String role) {
        this.role = role;
    }

    public String getRole() {
        return role;
    }
}