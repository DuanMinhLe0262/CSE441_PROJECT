package com.example.shoeshopee_admin.Model;

public class CartProduct {

    private String id; // ID sản phẩm
    private String name; // Tên sản phẩm
    private String colorName; // Tên màu sắc
    private String image; // Hình ảnh sản phẩm
    private String sizeName; // Tên kích thước
    private double price;
    private String brandName;
    private int quantity; // Số lượng sản phẩm

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

    public void setSizeName(String sizeName) {
        this.sizeName = sizeName;
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

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public CartProduct(String id, String name, String colorName, String image, String sizeName, double price, String brandName, int quantity) {
        this.id = id;
        this.name = name;
        this.colorName = colorName;
        this.image = image;
        this.sizeName = sizeName;
        this.price = price;
        this.brandName = brandName;
        this.quantity = quantity;
    }

    public CartProduct() {
    }
}
