package com.example.shoeshopee_customer;

import android.os.Bundle;


public class CompleteFragment extends BaseOrderFragment {
    private static final String ARG_USER_ID = "user_id";

    public static CompleteFragment newInstance(String userId) {
        CompleteFragment fragment = new CompleteFragment();
        Bundle args = new Bundle();
        args.putString(ARG_USER_ID, userId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected String getOrderStatus() {
        return "Đã giao hàng";
    }

    @Override
    protected String getOrderUserId() {
        return getArguments() != null ? getArguments().getString(ARG_USER_ID) : "";
    }
}