package com.example.shoeshopee_customer;


public class CompleteFragment extends BaseOrderFragment {
    public static CompleteFragment newInstance() {
        return new CompleteFragment();
    }

    @Override
    protected String getOrderStatus() {
        return "Đã giao hàng";
    }
}