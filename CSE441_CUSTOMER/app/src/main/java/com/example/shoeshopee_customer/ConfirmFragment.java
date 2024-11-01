package com.example.shoeshopee_customer;

import android.os.Bundle;

public class ConfirmFragment extends BaseOrderFragment {
    private static final String ARG_USER_ID = "user_id";

    public static ConfirmFragment newInstance(String userId) {
        ConfirmFragment fragment = new ConfirmFragment();
        Bundle args = new Bundle();
        args.putString(ARG_USER_ID, userId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected String getOrderStatus() {
        return "Chờ xác nhận";
    }

    @Override
    protected String getOrderUserId() {
        return getArguments() != null ? getArguments().getString(ARG_USER_ID) : "";
    }
}
