package com.example.shoeshopee_admin.Model;

import java.util.List;
import java.util.Map;

public class Color {
    private String colorName;
    private double price;
    private List<String> images;
    private Map<String, Size> sizes;

    public String getColorName() {
        return colorName;
    }

    public void setColorName(String colorName) {
        this.colorName = colorName;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }

    public Map<String, Size> getSizes() {
        return sizes;
    }

    public void setSizes(Map<String, Size> sizes) {
        this.sizes = sizes;
    }



    // Constructor, getters, and setters
    public Color() {}

    public Color(String colorName, double price, List<String> images, Map<String, Size> sizes) {
        this.colorName = colorName;
        this.price = price;
        this.images = images;
        this.sizes = sizes;
    }

    @Override
    public String toString() {
        return "Color{" +
                "colorName='" + colorName + '\'' +
                ", price=" + price +
                ", images=" + images +
                ", sizes=" + sizes +
                '}';
    }
// Getters and Setters
}

