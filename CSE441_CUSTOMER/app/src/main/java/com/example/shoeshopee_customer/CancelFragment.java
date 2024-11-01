package com.example.shoeshopee_customer;

import android.os.Bundle;

public class CancelFragment extends BaseOrderFragment {
    private static final String ARG_USER_ID = "user_id";

    public static CancelFragment newInstance(String userId) {
        CancelFragment fragment = new CancelFragment();
        Bundle args = new Bundle();
        args.putString(ARG_USER_ID, userId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected String getOrderStatus() {
        return "Đã hủy";
    }

    @Override
    protected String getOrderUserId() {
        return getArguments() != null ? getArguments().getString(ARG_USER_ID) : "";
    }
}