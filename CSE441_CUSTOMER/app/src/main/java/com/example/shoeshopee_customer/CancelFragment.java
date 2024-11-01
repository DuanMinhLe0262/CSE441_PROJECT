package com.example.shoeshopee_customer;

public class CancelFragment extends BaseOrderFragment {
    public static CancelFragment newInstance() {
        return new CancelFragment();
    }

    @Override
    protected String getOrderStatus() {
        return "Đã hủy";
    }
}