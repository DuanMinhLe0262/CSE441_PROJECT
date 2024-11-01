package com.example.shoeshopee_customer.Model;

import java.util.List;

public class Order {
    private String id;
    private String userId;
    private String customerPhone;
    private String customerName;
    private String customerAddress;
    private List<CartProduct> products;
    private Double totalAmount;
    private String status;
    private String note;
    private String time;

    public Order() {
    }

    public Order(String id, String userId, String customerPhone, String customerName, String customerAddress, List<CartProduct> products, Double totalAmount, String status, String note, String time) {
        this.id = id;
        this.userId = userId;
        this.customerPhone = customerPhone;
        this.customerName = customerName;
        this.customerAddress = customerAddress;
        this.products = products;
        this.totalAmount = totalAmount;
        this.status = status;
        this.note = note;
        this.time = time;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getCustomerPhone() {
        return customerPhone;
    }

    public void setCustomerPhone(String customerPhone) {
        this.customerPhone = customerPhone;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerAddress() {
        return customerAddress;
    }

    public void setCustomerAddress(String customerAddress) {
        this.customerAddress = customerAddress;
    }

    public List<CartProduct> getProducts() {
        return products;
    }

    public void setProducts(List<CartProduct> products) {
        this.products = products;
    }

    public Double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(Double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return "Order{" +
                "id='" + id + '\'' +
                ", userId='" + userId + '\'' +
                ", customerPhone='" + customerPhone + '\'' +
                ", customerName='" + customerName + '\'' +
                ", customerAddress='" + customerAddress + '\'' +
                ", products=" + products +
                ", totalAmount=" + totalAmount +
                ", status='" + status + '\'' +
                ", note='" + note + '\'' +
                ", time='" + time + '\'' +
                '}';
    }
}
