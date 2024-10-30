package com.example.shoeshopee_admin;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.shoeshopee_admin.Model.Brand;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class BrandDetailActivity extends AppCompatActivity {

    private TextView brandName;
    private ImageView brandImage, back_detail_brand;
    private DatabaseReference mDatabase;
    private String brandId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_brand_detail);

        back_detail_brand = findViewById(R.id.back_detail_brand);
        back_detail_brand.setOnClickListener(v -> finish());


        brandName = findViewById(R.id.brandName);
        brandImage = findViewById(R.id.brandImage);

        brandId = getIntent().getStringExtra("brandId");

        mDatabase = FirebaseDatabase.getInstance().getReference("brands");

        mDatabase.child(brandId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Brand brand = dataSnapshot.getValue(Brand.class);
                if (brand != null) {
                    brandName.setText(brand.getName());
                    loadImage(brandImage, brand.getImageUrl());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void loadImage(ImageView brandImage, String imageUrl) {
        Glide.with(this).load(imageUrl).into(brandImage);
    }
}
