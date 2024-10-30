package com.example.shoeshopee_admin.Model;

import java.util.Map;

public class Product {
        private String id; // Product ID
        private String name;
        private String description;
        private String brandId;
        private Map<String, Color> colors;

    // Constructors, getters, and setters
    public Product() {}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getBrandId() {
        return brandId;
    }

    public void setBrandId(String brandId) {
        this.brandId = brandId;
    }

    public Map<String, Color> getColors() {
        return colors;
    }

    public void setColors(Map<String, Color> colors) {
        this.colors = colors;
    }

    public Product(String id, String name, String description, String brandId, Map<String, Color> colors) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.brandId = brandId;
        this.colors = colors;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Product{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", brandId='" + brandId + '\'' +
                ", colors=" + colors +
                '}';
    }
}


