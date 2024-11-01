package com.example.shoeshopee_customer;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

public class ProfileFragment extends Fragment {
    String userId = "";
    private static final String ARG_USER_ID = "user_id";
    LinearLayout confirmIntentToOrderTracking,
            deliveyIntentToOrderTracking,
            completeIntentToOrderTracking,
            cancelIntentToOrderTracking;

    public static ProfileFragment newInstance(String userId) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_USER_ID, userId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (getArguments() != null) {
            userId = getArguments().getString(ARG_USER_ID);
            // Use the userId as needed
        }
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        confirmIntentToOrderTracking = view.findViewById(R.id.confirmIntentToOrderTracking);
        deliveyIntentToOrderTracking = view.findViewById(R.id.deliveyIntentToOrderTracking);
        completeIntentToOrderTracking = view.findViewById(R.id.completeIntentToOrderTracking);
        cancelIntentToOrderTracking = view.findViewById(R.id.cancelIntentToOrderTracking);

        confirmIntentToOrderTracking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), OrderTrackingActivity.class);
                intent.putExtra("userId", userId);
                startActivity(intent);
            }
        });
        return view;
    }
}