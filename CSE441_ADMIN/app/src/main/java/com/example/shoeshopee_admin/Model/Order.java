package com.example.shoeshopee_admin.Model;

import java.util.Map;

public class Order {
    private String id;
    private String userId;
    private String phone;
    private String name;
    private String address;
    private Map<String, CartProduct> items;
    private double total;
    private String status;
    private String time;

    private String note;

    public Order() {
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

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Map<String, CartProduct> getItems() {
        return items;
    }

    public void setItems(Map<String, CartProduct> items) {
        this.items = items;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Order(String id, String userId, String phone, String name, String address, Map<String, CartProduct> items, double total, String status, String time, String note) {
        this.id = id;
        this.userId = userId;
        this.phone = phone;
        this.name = name;
        this.address = address;
        this.items = items;
        this.total = total;
        this.status = status;
        this.time = time;
        this.note = note;
    }

    @Override
    public String toString() {
        return "Order{" +
                "id='" + id + '\'' +
                ", userId='" + userId + '\'' +
                ", customerPhone='" + phone + '\'' +
                ", customerName='" + name + '\'' +
                ", customerAddress='" + address + '\'' +
                ", items=" + items +
                ", total=" + total +
                ", status='" + status + '\'' +
                ", time='" + time + '\'' +
                ", note='" + note + '\'' +
                '}';
    }
}
