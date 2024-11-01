package com.example.shoeshopee_customer;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileFragment extends Fragment {
    String userId = "";
    private static final String ARG_USER_ID = "user_id";
    LinearLayout confirmIntentToOrderTracking,
            deliveyIntentToOrderTracking,
            completeIntentToOrderTracking,
            cancelIntentToOrderTracking;
    ImageView settingBtn;
    ImageView imgAvatar; // ImageView cho avatar
    TextView textViewUsername, textViewDisplayName;

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
        }

        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // Khởi tạo các thành phần giao diện
        confirmIntentToOrderTracking = view.findViewById(R.id.confirmIntentToOrderTracking);
        deliveyIntentToOrderTracking = view.findViewById(R.id.deliveyIntentToOrderTracking);
        completeIntentToOrderTracking = view.findViewById(R.id.completeIntentToOrderTracking);
        cancelIntentToOrderTracking = view.findViewById(R.id.cancelIntentToOrderTracking);
        settingBtn = view.findViewById(R.id.settingBtn);

        textViewUsername = view.findViewById(R.id.textView9); // TextView cho tên người dùng
        textViewDisplayName = view.findViewById(R.id.textView14); // TextView cho tên hiển thị
        imgAvatar = view.findViewById(R.id.img_avatar); // ImageView cho avatar

        // Gọi phương thức để tải dữ liệu người dùng
        if(userId != null){
            loadUserData();
        }
        checkCustomer();

        confirmIntentToOrderTracking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), OrderTrackingActivity.class);
                intent.putExtra("userId", userId);
                startActivity(intent);
            }
        });

        settingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), SettingActivity.class));
            }
        });

        return view;
    }

    private void loadUserData() {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(userId);
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String username = snapshot.child("name").getValue(String.class); // Lấy tên người dùng
                    String displayName = snapshot.child("email").getValue(String.class); // Lấy tên hiển thị
                    String avatarUrl = snapshot.child("avatarUrl").getValue(String.class); // Lấy URL ảnh đại diện

                    // Cập nhật giao diện với thông tin người dùng
                    textViewUsername.setText(username != null ? username : "Tên người dùng");
                    textViewDisplayName.setText(displayName != null ? displayName : "@email");

                    // Tải ảnh đại diện vào ImageView
                    if (avatarUrl != null && !avatarUrl.isEmpty()) {
                        Glide.with(getActivity())
                                .load(avatarUrl) // URL của ảnh đại diện
                                .placeholder(R.drawable.account_icon) // Hình ảnh hiển thị trong khi đang tải
                                .into(imgAvatar); // ImageView cho avatar
                    }
                } else {
                    Toast.makeText(getActivity(), "Không tìm thấy thông tin người dùng", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getActivity(), "Lỗi khi lấy dữ liệu", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void checkCustomer(){
        if(userId == null){
            Toast.makeText(getActivity(), "Vui lòng đăng nhập", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getContext(), LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
    }
}
