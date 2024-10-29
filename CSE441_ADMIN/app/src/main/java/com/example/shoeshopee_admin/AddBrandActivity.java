package com.example.shoeshopee_admin;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.shoeshopee_admin.Model.Brand;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.UUID;

public class AddBrandActivity extends AppCompatActivity {

    private ImageView back_login;
    private static final int PICK_IMAGE_REQUEST = 1;
    private EditText editTextBrandName;
    private ImageButton buttonChooseImage;
    private Button buttonUploadBrand;
    private ProgressBar progressBar;

    private Uri imageUri;
    private DatabaseReference mDatabase;
    private StorageReference mStorageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_brand);

        editTextBrandName = findViewById(R.id.editTextBrandName);
        buttonUploadBrand = findViewById(R.id.buttonUploadBrand);
        buttonChooseImage = findViewById(R.id.buttonChooseImage);
        back_login = findViewById(R.id.back_login);
        progressBar = findViewById(R.id.progressBar);

        // Khởi tạo Firebase Database và Storage
        mDatabase = FirebaseDatabase.getInstance().getReference("brands");
        mStorageReference = FirebaseStorage.getInstance().getReference("brand_images");

        buttonChooseImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage();
            }
        });

        buttonUploadBrand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadBrand();
            }
        });

        back_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void uploadBrand() {
        String brandName = editTextBrandName.getText().toString().trim();
        if (TextUtils.isEmpty(brandName)) {
            Toast.makeText(this, "Please enter a brand name", Toast.LENGTH_SHORT).show();
            return;
        }

        if (imageUri == null) {
            Toast.makeText(this, "Please choose an image", Toast.LENGTH_SHORT).show();
            return;
        }


        progressBar.setVisibility(View.VISIBLE);


        String imageId = UUID.randomUUID().toString();
        StorageReference imageRef = mStorageReference.child(imageId);

        imageRef.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                progressBar.setVisibility(View.GONE);

                if (task.isSuccessful()) {
                    imageRef.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if (task.isSuccessful()) {
                                String imageUrl = task.getResult().toString();
                                saveBrandToDatabase(brandName, imageUrl);
                            } else {
                                Log.e("UploadError", "Failed to get image URL: " + task.getException().getMessage());
                                Toast.makeText(AddBrandActivity.this, "Image URL retrieval failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else {
                    Toast.makeText(AddBrandActivity.this, "Image upload failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void saveBrandToDatabase(String brandName, String imageUrl) {
        String brandId = mDatabase.push().getKey();
        Brand brand = new Brand(brandId, brandName, imageUrl);
        if (brandId != null) {
            mDatabase.child(brandId).setValue(brand).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(AddBrandActivity.this, "Brand added successfully", Toast.LENGTH_SHORT).show();

                        // Quay lại BrandFragment
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.fragment_container, new BrandFragment())
                                .commit();
                    } else {
                        Toast.makeText(AddBrandActivity.this, "Failed to add brand", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            buttonChooseImage.setImageURI(imageUri);
        }
    }
}
