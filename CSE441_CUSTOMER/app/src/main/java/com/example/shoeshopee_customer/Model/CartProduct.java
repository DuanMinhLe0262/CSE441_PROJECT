package com.example.shoeshopee_customer.Model;

import java.io.Serializable;

public class CartProduct implements Serializable {
    private String id; // ID sản phẩm
    private String name; // Tên sản phẩm
    private String colorName; // Tên màu sắc
    private String image; // Hình ảnh sản phẩm
    private String sizeName; // Tên kích thước
    private double price;
    private String brandName;
    private int quantity; // Số lượng sản phẩm
    private boolean selected;

    public CartProduct(String id, String name, String colorName, String image, String sizeName, double price, String brandName, int quantity, boolean selected) {
        this.id = id;
        this.name = name;
        this.colorName = colorName;
        this.image = image;
        this.sizeName = sizeName;
        this.price = price;
        this.brandName = brandName;
        this.quantity = quantity;
        this.selected = selected;
    }

    // Constructor rỗng
    public CartProduct() {
    }

    // Getter và Setter
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getColorName() {
        return colorName;
    }

    public void setColorName(String colorName) {
        this.colorName = colorName;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getSizeName() {
        return sizeName;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getBrandName() {
        return brandName;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
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

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    @Override
    public String toString() {
        return "CartProduct{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", colorName='" + colorName + '\'' +
                ", image='" + image + '\'' +
                ", sizeName='" + sizeName + '\'' +
                ", price='" + price + '\'' +
                ", brandName='" + brandName + '\'' +
                ", quantity=" + quantity +
                ", selected=" + selected +
                '}';
    }
}
