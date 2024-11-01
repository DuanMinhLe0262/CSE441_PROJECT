package com.example.shoeshopee_customer;


public class DeliveryFragment extends BaseOrderFragment {
    public static DeliveryFragment newInstance() {
        return new DeliveryFragment();
    }

    @Override
    protected String getOrderStatus() {
        return "Đang giao hàng";
    }
}