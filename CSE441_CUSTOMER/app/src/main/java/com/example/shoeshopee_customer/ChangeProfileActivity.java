package com.example.shoeshopee_customer;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.shoeshopee_customer.Model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.UUID;

public class ChangeProfileActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private FirebaseAuth mAuth;
    private DatabaseReference userRef;
    private StorageReference storageRef;
    private ImageView avatarImageView;
    private EditText nameEditText, genderEditText, birthDateEditText, phoneEditText, emailEditText;
    private Uri avatarUri;
    private String existingAvatarUrl;  // Thêm biến để lưu URL ảnh hiện tại

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_profile);

        mAuth = FirebaseAuth.getInstance();
        userRef = FirebaseDatabase.getInstance().getReference("users").child(mAuth.getCurrentUser().getUid());
        storageRef = FirebaseStorage.getInstance().getReference("avatars");

        // Ánh xạ các thành phần trong giao diện
        avatarImageView = findViewById(R.id.imageView2);
        nameEditText = findViewById(R.id.nameEditText);
        genderEditText = findViewById(R.id.genderEditText);
        birthDateEditText = findViewById(R.id.birthDateEditText);
        phoneEditText = findViewById(R.id.phoneEditText);
        emailEditText = findViewById(R.id.emailEditText);

        loadUserData();

        // Bắt sự kiện chọn ảnh đại diện
        avatarImageView.setOnClickListener(v -> openImageChooser());

        // Sự kiện nút lưu thông tin
        Button saveButton = findViewById(R.id.saveBtn);
        saveButton.setOnClickListener(v -> updateUserProfile());
    }

    private void openImageChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Avatar"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            avatarUri = data.getData();
            avatarImageView.setImageURI(avatarUri);
        }
    }

    private void loadUserData() {
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                if (user != null) {
                    nameEditText.setText(user.getName());
                    genderEditText.setText(user.getGender());
                    birthDateEditText.setText(user.getBirthDate());
                    phoneEditText.setText(user.getPhone());
                    emailEditText.setText(user.getEmail());
                    existingAvatarUrl = user.getAvatarUrl();
                    if (existingAvatarUrl != null) {
                        // Tải ảnh đại diện bằng Glide
                        Glide.with(ChangeProfileActivity.this).load(existingAvatarUrl).into(avatarImageView);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ChangeProfileActivity.this, "Lỗi tải dữ liệu", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateUserProfile() {
        String name = nameEditText.getText().toString();
        String gender = genderEditText.getText().toString();
        String birthDate = birthDateEditText.getText().toString();
        String phone = phoneEditText.getText().toString();
        String email = emailEditText.getText().toString();

        // Nếu người dùng chọn ảnh mới, upload lên Firebase Storage
        if (avatarUri != null) {
            StorageReference avatarRef = storageRef.child(UUID.randomUUID().toString());
            avatarRef.putFile(avatarUri)
                    .addOnSuccessListener(taskSnapshot -> avatarRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        String avatarUrl = uri.toString();
                        saveUserData(new User(mAuth.getCurrentUser().getUid(), name, gender, birthDate, phone, email, avatarUrl, "customer"));
                    }))
                    .addOnFailureListener(e -> Toast.makeText(ChangeProfileActivity.this, "Lỗi upload ảnh đại diện", Toast.LENGTH_SHORT).show());
        } else {

            saveUserData(new User(mAuth.getCurrentUser().getUid(), name, gender, birthDate, phone, email, existingAvatarUrl,"customer"));
        }
    }

    private void saveUserData(User user) {
        userRef.setValue(user)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(ChangeProfileActivity.this, "Cập nhật thành công", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> Toast.makeText(ChangeProfileActivity.this, "Lỗi cập nhật thông tin", Toast.LENGTH_SHORT).show());
    }
}
