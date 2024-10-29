package com.example.shoeshopee_admin;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.shoeshopee_admin.Model.Color;
import com.example.shoeshopee_admin.Model.Product;
import com.example.shoeshopee_admin.Model.Size;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProductDetailActivity extends AppCompatActivity {

    private Spinner spinnerBrand;
    private FirebaseDatabase database;
    private DatabaseReference brandsRef;
    private List<String> brandIds = new ArrayList<>();
    private List<String> brandNames = new ArrayList<>();
    private int brandNumber = 0;

    private String brandId;

    private static final int REQUEST_CODE_IMAGE_PICKER = 1;
    private LinearLayout selectedLayoutImageSelection;
    private DatabaseReference productsRef;
    private List<LinearLayout> allOptionLayouts = new ArrayList<>();
    private LinearLayout layoutOptionList;

    private TextInputEditText etProductName, etProductDescription;

    private  Map<View, List<String>> optionLayoutImageMap = new HashMap<>();
    private LinearLayout selectedOptionLayout = null;
    private String productId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        productId = getIntent().getStringExtra("PRODUCT_ID");
        Log.d("TAG", "onCreate: " + productId);



        spinnerBrand = findViewById(R.id.spinner_brand);
        database = FirebaseDatabase.getInstance();
        brandsRef = database.getReference("brands");

        fetchBrandsFromFirebase(new OnloadedListener() {
            @Override
            public void onImageUploaded(String imageUrl) {
            }

            @Override
            public void onBrandLoaded(List<String> brandNames, List<String> brandIds) {
                ArrayAdapter<String> adapter = new ArrayAdapter<>(ProductDetailActivity.this, android.R.layout.simple_spinner_item, brandNames);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerBrand.setAdapter(adapter);
            }
        });


        productsRef = FirebaseDatabase.getInstance().getReference("products");


        etProductName = findViewById(R.id.et_product_name);
        etProductDescription = findViewById(R.id.et_product_description);
        layoutOptionList = findViewById(R.id.layout_option_list);

        fetchProductData(productId);

        Button btnAddOption = findViewById(R.id.btn_add_option);
        btnAddOption.setOnClickListener(v -> addOption());

        Button btnAddProduct = findViewById(R.id.btn_edit_product);
        btnAddProduct.setOnClickListener(v -> addOrUpdateProductInFirebase());
    }

    private void fetchProductData(String productId) {
        productsRef.child(productId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String productName = dataSnapshot.child("name").getValue(String.class);
                    String productDescription = dataSnapshot.child("description").getValue(String.class);

                    ((TextInputEditText) findViewById(R.id.et_product_name)).setText(productName);
                    ((TextInputEditText) findViewById(R.id.et_product_description)).setText(productDescription);

                    for (DataSnapshot colorSnapshot : dataSnapshot.child("colors").getChildren()) {
                        String colorName = colorSnapshot.child("colorName").getValue(String.class);
                        double price = colorSnapshot.child("price").getValue(Double.class);
                        List<String> images = (List<String>) colorSnapshot.child("images").getValue();

                        Map<String, Size> sizes = new HashMap<>();
                        Map<String, Object> sizesMap = (Map<String, Object>) colorSnapshot.child("sizes").getValue();
                        for (Map.Entry<String, Object> sizeEntry : sizesMap.entrySet()) {
                            Map<String, Object> sizeData = (Map<String, Object>) sizeEntry.getValue();
                            String sizeName = (String) sizeData.get("sizeName");
                            int quantity = ((Long) sizeData.get("quantity")).intValue();
                            sizes.put(sizeEntry.getKey(), new Size(sizeName, quantity));
                        }

                        Color color = new Color(colorName, price, images, sizes);
                        Log.d("TAG", color.toString());

                        DisplayOptionLayout(color);

                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("Error", databaseError.getMessage());
            }
        });
    }


    private void DisplayOptionLayout(Color color) {
        View optionLayout = getLayoutInflater().inflate(R.layout.layout_option_item, null);

        ((TextInputEditText) optionLayout.findViewById(R.id.et_color)).setText(color.getColorName());
        ((TextInputEditText) optionLayout.findViewById(R.id.et_price)).setText(String.valueOf(color.getPrice()));

        if (color.getSizes() != null && !color.getSizes().isEmpty()) {
            for (Map.Entry<String, Size> entry : color.getSizes().entrySet()) {
                Size size = entry.getValue();
                if (size != null) {
                    ((TextInputEditText) optionLayout.findViewById(R.id.et_size)).setText(size.getSizeName());
                    ((TextInputEditText) optionLayout.findViewById(R.id.et_quantity)).setText(String.valueOf(size.getQuantity()));
                    break;
                }
            }
        } else {
            Log.d("Sizes", "No sizes available or sizes is null.");
        }

        LinearLayout layoutImageSelection = optionLayout.findViewById(R.id.layout_image_selection);

        if (color.getImages() != null) {
            Log.d("image", color.getImages().toString());
            for (String imageUrl : color.getImages()) {
                Log.d("imgurl", imageUrl);
                if (imageUrl != null) {
                    ImageView imageView = new ImageView(this);
                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                            100, 100
                    );
                    layoutParams.setMargins(8, 8, 8, 8);
                    imageView.setLayoutParams(layoutParams);
                    imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

                    Log.d("Sizes2", "No sizes available or sizes is null.");
                    Glide.with(this)
                            .load(imageUrl)
                            .into(imageView);
                    layoutImageSelection.addView(imageView);
                    Log.d("Sizes3", "No sizes available or sizes is null.");
                } else {
                    Log.d("ImageURL", "Image URL is null.");
                }
            }
        } else {
            Log.d("Images", "No images available or images is null.");
        }


        Button btnSelectImages = optionLayout.findViewById(R.id.btn_select_images);

        btnSelectImages.setOnClickListener(v -> {
            openImagePicker(layoutImageSelection);
            selectedLayoutImageSelection = layoutImageSelection;
        });

        Button btnDeleteOption = optionLayout.findViewById(R.id.btn_delete_option);
        btnDeleteOption.setOnClickListener(v -> showDeleteConfirmationDialog((LinearLayout) optionLayout));

        layoutOptionList.addView(optionLayout);
    }




    private void openImagePicker(LinearLayout layoutOption) {
        selectedOptionLayout = layoutOption;
        Log.d("fd", "openImagePicker: "+ selectedOptionLayout.toString());
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        startActivityForResult(intent, REQUEST_CODE_IMAGE_PICKER);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_IMAGE_PICKER && resultCode == RESULT_OK && data != null && selectedOptionLayout != null) {
            List<String> selectedImageUris = new ArrayList<>();

            if (data.getClipData() != null) {

                ClipData clipData = data.getClipData();
                for (int i = 0; i < clipData.getItemCount(); i++) {
                    Uri imageUri = clipData.getItemAt(i).getUri();
                    selectedImageUris.add(imageUri.toString());
                    addImageToSelection(imageUri);
                }
            } else if (data.getData() != null) {
                Uri imageUri = data.getData();
                selectedImageUris.add(imageUri.toString());
                addImageToSelection(imageUri);
            }

            if (!selectedImageUris.isEmpty()) {
                optionLayoutImageMap.put(selectedOptionLayout, selectedImageUris);
                Log.d("df", optionLayoutImageMap.toString());
                allOptionLayouts.add(selectedOptionLayout);
            }

            selectedOptionLayout = null;
        }
    }



    private void addOption() {
        LinearLayout newOptionLayout = (LinearLayout) getLayoutInflater().inflate(R.layout.layout_option_item, null);

        Button btnSelectImages = newOptionLayout.findViewById(R.id.btn_select_images);
        LinearLayout layoutImageSelection = newOptionLayout.findViewById(R.id.layout_image_selection);

        btnSelectImages.setOnClickListener(v -> {

            openImagePicker(newOptionLayout);

            selectedLayoutImageSelection = layoutImageSelection;
        });


        Button btnDeleteOption = newOptionLayout.findViewById(R.id.btn_delete_option);
        btnDeleteOption.setOnClickListener(v -> showDeleteConfirmationDialog(newOptionLayout));


        layoutOptionList.addView(newOptionLayout);

    }

    private void showDeleteConfirmationDialog(LinearLayout optionLayout) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete Option");
        builder.setMessage("Are you sure you want to delete this option?");
        builder.setPositiveButton("Yes", (dialog, which) -> {
            layoutOptionList.removeView(optionLayout);
            allOptionLayouts.remove(optionLayout);
        });
        builder.setNegativeButton("No", (dialog, which) -> dialog.dismiss());
        builder.create().show();
    }

    private void addImageToSelection(Uri imageUri) {
        if (selectedLayoutImageSelection == null) return;
        ImageView imageView = new ImageView(this);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                100, 100
        );
        layoutParams.setMargins(8, 8, 8, 8);
        imageView.setLayoutParams(layoutParams);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imageView.setTag(imageUri);
        Glide.with(this).load(imageUri).into(imageView);
        selectedLayoutImageSelection.addView(imageView);
    }

    public void addOrUpdateProductInFirebase() {
        String productName = etProductName.getText().toString();
        String productDescription = etProductDescription.getText().toString();
        int position = spinnerBrand.getSelectedItemPosition();
        Log.d("positon", position + "");
        brandId = brandIds.get(position);
        Log.d("brandId", brandId);

        DatabaseReference productRef = FirebaseDatabase.getInstance().getReference("products");

        productRef.orderByChild("name").equalTo(productName).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                String productId;
                Map<String, Color> colors = new HashMap<>();

                if (snapshot.exists()) {
                    DataSnapshot existingProductSnapshot = snapshot.getChildren().iterator().next();
                    productId = existingProductSnapshot.getKey();

                    Product existingProduct = existingProductSnapshot.getValue(Product.class);
                    if (existingProduct != null && existingProduct.getColors() != null) {
                        colors = existingProduct.getColors();
                    }
                } else {
                    productId = productRef.push().getKey();
                    if (productId == null) {
                        Toast.makeText(ProductDetailActivity.this, "Failed to generate product ID!", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }


                for (int i = 0; i < allOptionLayouts.size(); i++) {
                    View optionView = allOptionLayouts.get(i);
                    Log.d("??1", optionView.toString());

                    List<String> imagelist = optionLayoutImageMap.get(optionView);
                    Log.d("??2", imagelist.toString());
                    TextInputEditText colorEditText = optionView.findViewById(R.id.et_color);
                    TextInputEditText priceEditText = optionView.findViewById(R.id.et_price);
                    TextInputEditText sizeEditText = optionView.findViewById(R.id.et_size);
                    TextInputEditText quantityEditText = optionView.findViewById(R.id.et_quantity);

                    String colorName = colorEditText.getText().toString();
                    double price = Double.parseDouble(priceEditText.getText().toString());


                    if (colors.containsKey(colorName)) {
                        Color existingColor = colors.get(colorName);
                        String sizeName = sizeEditText.getText().toString();
                        int quantity = Integer.parseInt(quantityEditText.getText().toString());

                        if (existingColor.getSizes().containsKey(sizeName)) {
                            Size existingSize = existingColor.getSizes().get(sizeName);
                            existingSize.setQuantity(existingSize.getQuantity() + quantity);
                        } else {
                            existingColor.getSizes().put(sizeName, new Size(sizeName, quantity));
                        }

                        existingColor.setPrice(price);
                        existingColor.setImages(imagelist);
                        Log.d("ioes", "onDataChange: "+existingColor.getImages().toString());
                    } else {
                        Map<String, Size> sizes = new HashMap<>();
                        String sizeName = sizeEditText.getText().toString();
                        int quantity = Integer.parseInt(quantityEditText.getText().toString());
                        sizes.put(sizeName, new Size(sizeName, quantity));

                        colors.put(colorName, new Color(colorName, price, imagelist, sizes));
                        Log.d("ioes4", "onDataChange: "+colors.size());
                    }
                }

                Product product = new Product(productId, productName, productDescription, brandId, colors);

                Log.d("product", product.toString());

                uploadImagesAndAddProduct(product);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(ProductDetailActivity.this, "Database error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void uploadImagesAndAddProduct(Product product) {
        int[] uploadedCount = {0};
        final int[] totalImages = {0};

        for (Color color : product.getColors().values()) {
            totalImages[0] += color.getImages().size();
        }

        for (String colorName : product.getColors().keySet()) {
            Color color = product.getColors().get(colorName);
            List<String> uploadedImageUrls = new ArrayList<>();

            for (String imageUri : color.getImages()) {
                if (isUrl(imageUri)) {
                    uploadedImageUrls.add(imageUri);
                    uploadedCount[0]++;

                    if (uploadedImageUrls.size() == color.getImages().size()) {
                        color.setImages(uploadedImageUrls);
                    }
                } else {
                    Uri uri = Uri.parse(imageUri);
                    uploadImageToStorage(uri, new OnloadedListener() {
                        @Override
                        public void onImageUploaded(String uploadedImageUrl) {
                            uploadedImageUrls.add(uploadedImageUrl);
                            Log.d("uploadedImageUrls", "onImageUploaded: " + uploadedImageUrl);

                            if (uploadedImageUrls.size() == color.getImages().size()) {
                                color.setImages(uploadedImageUrls);
                            }

                            uploadedCount[0]++;
                            if (uploadedCount[0] == totalImages[0]) {
                                saveProductToFirebase(product);
                            }
                        }

                        @Override
                        public void onBrandLoaded(List<String> brandNames, List<String> brandIds) {
                        }
                    });
                }
            }
        }
    }

    private boolean isUrl(String uriString) {
        try {
            new URL(uriString);
            return true;
        } catch (MalformedURLException e) {
            return false;
        }
    }


    private void saveProductToFirebase(Product product) {
        Log.d("Firebase", "Saving product: " + product.toString());
        productsRef.child(product.getId()).setValue(product)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(ProductDetailActivity.this, "Product added successfully", Toast.LENGTH_SHORT).show();
                        finish();


                    } else {
                        Toast.makeText(ProductDetailActivity.this, "Failed to add product", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void uploadImageToStorage(Uri imageUri, OnloadedListener listener) {
        StorageReference storageReference = FirebaseStorage.getInstance().getReference("images/" + imageUri.getLastPathSegment());

        try {
            InputStream inputStream = getContentResolver().openInputStream(imageUri);
            if (inputStream == null) {
                throw new FileNotFoundException("Failed to open stream for URI: " + imageUri);
            }

            storageReference.putStream(inputStream)
                    .addOnSuccessListener(taskSnapshot -> {
                        storageReference.getDownloadUrl().addOnSuccessListener(downloadUri -> {
                            String imageUrl = downloadUri.toString();
                            listener.onImageUploaded(imageUrl);
                        });
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        } catch (FileNotFoundException e) {
            Toast.makeText(this, "File not found: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void fetchBrandsFromFirebase(OnloadedListener listener) {
        brandsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                brandNumber = -1;
                for (DataSnapshot brandSnapshot : dataSnapshot.getChildren()) {
                    brandNumber += 1;
                    String brandId = brandSnapshot.getKey();
                    String brandName = brandSnapshot.child("name").getValue(String.class);
                    brandIds.add(brandId);
                    brandNames.add(brandName);
                }
                listener.onBrandLoaded(brandNames, brandIds);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("AddProductActivity", "Failed to fetch brands: " + databaseError.getMessage());
            }
        });
    }

    interface OnloadedListener {
        void onImageUploaded(String imageUrl);
        void onBrandLoaded(List<String> brandNames, List<String> brandIds);
    }
}