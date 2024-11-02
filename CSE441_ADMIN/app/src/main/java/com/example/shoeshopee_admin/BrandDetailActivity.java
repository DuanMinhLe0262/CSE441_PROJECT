package com.example.shoeshopee_admin;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.UUID;

public class BrandDetailActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private TextView brandName, deleteText;
    private ImageView brandImage, backDetailBrand;
    private Button editButton;
    private DatabaseReference mDatabase;
    private StorageReference storageRef;
    private String brandId;
    private Uri imageUri;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_brand_detail);

        backDetailBrand = findViewById(R.id.back_detail_brand);
        brandName = findViewById(R.id.edtBrandName);
        brandImage = findViewById(R.id.brandImage);
        deleteText = findViewById(R.id.deleteBtn);
        editButton = findViewById(R.id.editBtn);

        backDetailBrand.setOnClickListener(v -> finish());

        brandId = getIntent().getStringExtra("brandId");

        mDatabase = FirebaseDatabase.getInstance().getReference("brands");
        storageRef = FirebaseStorage.getInstance().getReference("brand_images");

        loadBrandData();

        deleteText.setOnClickListener(v -> deleteBrand());

        editButton.setOnClickListener(v -> updateBrand());

        brandImage.setOnClickListener(v -> openImagePicker());
    }

    private void loadBrandData() {
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
                Toast.makeText(BrandDetailActivity.this, "Lỗi tải thương hiệu", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadImage(ImageView imageView, String imageUrl) {
        Glide.with(this).load(imageUrl).into(imageView);
    }

    private void deleteBrand() {
        DatabaseReference productsRef = FirebaseDatabase.getInstance().getReference("products");

        productsRef.orderByChild("brandId").equalTo(brandId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Toast.makeText(BrandDetailActivity.this, "Không thể xóa vì có sản phẩm thuộc thương hiệu này", Toast.LENGTH_SHORT).show();
                } else {
                    mDatabase.child(brandId).removeValue().addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(BrandDetailActivity.this, "Xóa thương hiệu thành công", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(BrandDetailActivity.this, "Xóa thương hiệu thất bại", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(BrandDetailActivity.this, "Lỗi kiểm tra sản phẩm liên kết", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void updateBrand() {
        String updatedName = brandName.getText().toString().trim();
        if (updatedName.isEmpty()) {
            Toast.makeText(this, "Tên thương hiệu không được để trống", Toast.LENGTH_SHORT).show();
            return;
        }

        mDatabase.child(brandId).child("name").setValue(updatedName).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(BrandDetailActivity.this, "Cập nhật thương hiệu thành công", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(BrandDetailActivity.this, "Cập nhật thương hiệu thất bại", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void openImagePicker() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Chọn ảnh"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            brandImage.setImageURI(imageUri); // Show the selected image
            uploadImage();
        }
    }

    private void uploadImage() {
        if (imageUri != null) {
            // Generate a unique filename
            String fileName = UUID.randomUUID().toString();
            StorageReference fileRef = storageRef.child(fileName);

            fileRef.putFile(imageUri).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    // Get download URL
                    fileRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        String imageUrl = uri.toString();
                        updateBrandImage(imageUrl);
                    });
                } else {
                    Toast.makeText(this, "Tải ảnh thất bại", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void updateBrandImage(String imageUrl) {
        mDatabase.child(brandId).child("imageUrl").setValue(imageUrl).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(BrandDetailActivity.this, "Chỉnh sửa ảnh thành công", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(BrandDetailActivity.this, "Chỉnh sửa ảnh thất bại", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
