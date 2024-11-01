package com.example.shoeshopee_customer;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.example.shoeshopee_customer.Adapter.ViewpagerOrdertrackingAdapter;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;
import java.util.List;

public class OrderTrackingActivity extends AppCompatActivity {
    TabLayout tabLayout;
    ViewPager2 viewPager;
    String userId = "";

    ImageView backBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_order_tracking);

        // Thiết lập Padding cho View
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        backBtn = findViewById(R.id.backBtn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);
        userId = getIntent().getStringExtra("userId");


        // Kiểm tra userId không null
        if (userId == null) {
            userId = ""; // Hoặc xử lý theo cách khác nếu cần
        }

        List<Fragment> fragments = new ArrayList<>();
        fragments.add(ConfirmFragment.newInstance(userId));
        fragments.add(DeliveryFragment.newInstance(userId)); // Bạn cần tạo Fragment này
        fragments.add(CompleteFragment.newInstance(userId)); // Bạn cần tạo Fragment này
        fragments.add(CancelFragment.newInstance(userId)); // Bạn cần tạo Fragment này

        ViewpagerOrdertrackingAdapter adapter = new ViewpagerOrdertrackingAdapter(this, fragments);
        viewPager.setAdapter(adapter);

        // Thiết lập TabLayout
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            switch (position) {
                case 0:
                    tab.setText("Chờ xác nhận");
                    break;
                case 1:
                    tab.setText("Đang giao hàng");
                    break;
                case 2:
                    tab.setText("Đã giao hàng");
                    break;
                case 3:
                    tab.setText("Đã hủy");
                    break;
            }
        }).attach();

    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra("fragmentToLoad", "0");
        intent.putExtra("userId", userId);
        startActivity(intent);
    }

    public void checkCustomer(){
        if(userId == null){
            Toast.makeText(this, "Vui lòng đăng nhập", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(OrderTrackingActivity.this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
    }
}
