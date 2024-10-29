package com.example.shoeshopee_admin.Model;

public class Size {
    private String sizeName;
    private int quantity;

    // Constructor, getters, and setters
    public Size() {}

    public String getSizeName() {
        return sizeName;
    }

    public void setSizeName(String sizeName) {
        this.sizeName = sizeName;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public Size(String sizeName, int quantity) {
        this.sizeName = sizeName;
        this.quantity = quantity;
    }

    // Getters and Setters
}

