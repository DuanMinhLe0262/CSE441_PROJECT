package com.example.shoeshopee_admin;

import android.app.AlertDialog;
import android.app.ProgressDialog;
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
import android.widget.TextView;
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
    private ImageView backBtn;
    private FirebaseDatabase database;
    private DatabaseReference brandsRef;
    private List<String> brandIdList = new ArrayList<>();
    private List<String> brandNameList = new ArrayList<>();
    private int brandNumber = 0;
    private Map<String, Color> colors = new HashMap<>();
    private String brandId;
    private static final int REQUEST_CODE_IMAGE_PICKER = 1;
    private DatabaseReference productsRef;
    private List<LinearLayout> list_layout_Option = new ArrayList<>();
    private List<LinearLayout> allOptionLayoutsBeforeFilled = new ArrayList<>();
    //    private List<LinearLayout> list_layout_Option_efore = new ArrayList<>();
    private LinearLayout layout_Option;
    private TextInputEditText etProductName, etProductDescription;
    private Map<View, List<String>> optionLayoutImageMap = new HashMap<>();
    private LinearLayout selectedOptionLayout = null; // layout khi an add image
    private LinearLayout selectedLayoutImageSelection; // layout danh sach anh da chon
    private String productId = "";
    private TextView txtDeleteProduct;
    ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        productId = getIntent().getStringExtra("PRODUCT_ID");
        Log.d("TAG", "onCreate: " + productId);

        backBtn = findViewById(R.id.backBtn);
        backBtn.setOnClickListener(v -> finish());
        spinnerBrand = findViewById(R.id.spinner_brand);
        database = FirebaseDatabase.getInstance();
        brandsRef = database.getReference("brands");
        productsRef = FirebaseDatabase.getInstance().getReference("products");
        etProductName = findViewById(R.id.et_product_name);
        etProductDescription = findViewById(R.id.et_product_description);
        layout_Option = findViewById(R.id.layout_Option);
        txtDeleteProduct = findViewById(R.id.txtDeleteProduct);

        txtDeleteProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDeleteProductConfirmationDialog();
//                finish();
            }
        });

        fetchProductData();

        Button btnAddOption = findViewById(R.id.btn_add_option);
        btnAddOption.setOnClickListener(v -> addOption());

        Button btnEditProduct = findViewById(R.id.btn_edit_product);
        btnEditProduct.setOnClickListener(v -> updateProduct());
    }

    private void fetchProductData() {
        productsRef.child(productId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String productName = dataSnapshot.child("name").getValue(String.class);
                    String productDescription = dataSnapshot.child("description").getValue(String.class);
                    String brandId = dataSnapshot.child("brandId").getValue(String.class);

                    fetchBrandsFromFirebase(new OnloadedListener() {
                        @Override
                        public void onImageUploaded(String imageUrl) {
                        }

                        @Override
                        public void onBrandLoaded(List<String> brandNames, List<String> brandIds) {
                            ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplicationContext(),
                                    android.R.layout.simple_spinner_item, brandNames);
                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                            spinnerBrand.setAdapter(adapter);

                            int selectedIndex = brandIds.indexOf(brandId);
                            if (selectedIndex != -1) {
                                spinnerBrand.setSelection(selectedIndex);
                            }
                        }
                    });

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
                        colors.put(colorName, color);
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
        Log.d("Size number", color.getSizes().size() + "");
        if (color.getSizes() != null && !color.getSizes().isEmpty()) {
            for (Map.Entry<String, Size> entry : color.getSizes().entrySet()) {
                Size size = entry.getValue();
                if (size != null) {
                    LinearLayout layout_option = (LinearLayout) getLayoutInflater().inflate(R.layout.layout_option_item, null);

                    ((TextInputEditText) layout_option.findViewById(R.id.et_color)).setText(color.getColorName());
                    ((TextInputEditText) layout_option.findViewById(R.id.et_price)).setText(String.valueOf(color.getPrice()));

                    ((TextInputEditText) layout_option.findViewById(R.id.et_size)).setText(size.getSizeName());
                    ((TextInputEditText) layout_option.findViewById(R.id.et_quantity)).setText(String.valueOf(size.getQuantity()));

                    // Hiển thị danh sách ảnh
                    LinearLayout layoutImageSelection = layout_option.findViewById(R.id.layout_image_selection);
                    List<String> imgList = new ArrayList<>();
                    if (color.getImages() != null) {
                        for (String imageUrl : color.getImages()) {
                            if (imageUrl != null) {
                                imgList.add(imageUrl);
                                ImageView imageView = new ImageView(this);
                                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                                        100, 100
                                );
                                layoutParams.setMargins(8, 8, 8, 8);
                                imageView.setLayoutParams(layoutParams);
                                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

                                Glide.with(this)
                                        .load(imageUrl)
                                        .into(imageView);
                                layoutImageSelection.addView(imageView);
                            } else {
                                Log.d("ImageURL", "Image URL is null.");
                            }
                        }
                    }

                    Button btnSelectImages = layout_option.findViewById(R.id.btn_select_images);
                    btnSelectImages.setOnClickListener(v -> {
                        openImagePicker(layout_option);
                        selectedLayoutImageSelection = layoutImageSelection;
                    });

                    Button btnDeleteOption = layout_option.findViewById(R.id.btn_delete_option);
                    btnDeleteOption.setOnClickListener(v -> showDeleteOptionConfirmationDialog(layout_option));
                    layout_Option.addView(layout_option);
                    list_layout_Option.add(layout_option);
                    allOptionLayoutsBeforeFilled.add(layout_option);
                    optionLayoutImageMap.put(layout_option, imgList);
                    Log.d("imggg", optionLayoutImageMap.toString());
                    Log.d("option", layout_option.toString());
                }
            }
        } else {
            Log.d("Sizes", "No sizes available or sizes is null.");
        }
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
            Log.d("option1", selectedOptionLayout.toString());
            optionLayoutImageMap.remove(selectedOptionLayout);
            selectedLayoutImageSelection.removeAllViews();
            Log.d("imggg1", optionLayoutImageMap.toString());
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
                Log.d("imggg2", optionLayoutImageMap.toString());
                list_layout_Option.add(selectedOptionLayout);
            }
            selectedOptionLayout = null;
        }
    }

    private Boolean checkInforOption() {
        LinearLayout optionView = allOptionLayoutsBeforeFilled.get(allOptionLayoutsBeforeFilled.size() - 1);
        TextInputEditText colorEditText = optionView.findViewById(R.id.et_color);
        TextInputEditText priceEditText = optionView.findViewById(R.id.et_price);
        TextInputEditText sizeEditText = optionView.findViewById(R.id.et_size);
        TextInputEditText quantityEditText = optionView.findViewById(R.id.et_quantity);
        LinearLayout layoutImageSelection = optionView.findViewById(R.id.layout_image_selection);

        // Kiểm tra giá trị của các trường nhập liệu, thay vì chỉ kiểm tra null của EditText
        if (colorEditText.getText() == null || colorEditText.getText().toString().trim().isEmpty()) {
            colorEditText.setError("Vui lòng nhập màu lựa chọn");
            return false;
        }
        if (priceEditText.getText() == null || priceEditText.getText().toString().trim().isEmpty()) {
            priceEditText.setError("Vui lòng nhập giá lựa chọn");
            return false;
        }
        if (sizeEditText.getText() == null || sizeEditText.getText().toString().trim().isEmpty()) {
            sizeEditText.setError("Vui lòng nhập kích thước lựa chọn");
            return false;
        }
        if (quantityEditText.getText() == null || quantityEditText.getText().toString().trim().isEmpty()) {
            quantityEditText.setError("Vui lòng nhập số lượng lựa chọn");
            return false;
        }
        if (layoutImageSelection.getChildCount() == 0){
            Toast.makeText(optionView.getContext(), "Vui lòng chọn ít nhất một ảnh", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void addOption() {
        Boolean check = true;
        if (allOptionLayoutsBeforeFilled.size() >= 1){
            check = checkInforOption();
        }
        if(check){
            LinearLayout newLayout_Option = (LinearLayout) getLayoutInflater().inflate(R.layout.layout_option_item, null);
            allOptionLayoutsBeforeFilled.add(newLayout_Option);
            Button btnSelectImages = newLayout_Option.findViewById(R.id.btn_select_images);
            LinearLayout layoutImageSelection = newLayout_Option.findViewById(R.id.layout_image_selection); // layout danh sach anh da chon

            btnSelectImages.setOnClickListener(v -> {
                openImagePicker(newLayout_Option);
                selectedLayoutImageSelection = layoutImageSelection;
            });
            Button btnDeleteOption = newLayout_Option.findViewById(R.id.btn_delete_option);
            btnDeleteOption.setOnClickListener(v -> showDeleteOptionConfirmationDialog(newLayout_Option));
            layout_Option.addView(newLayout_Option);
        }

//        list_layout_Option.add(newLayout_Option);

    }

    private void showDeleteOptionConfirmationDialog(LinearLayout layout_option) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Xóa lựa chọn");
        builder.setMessage("Bạn chắc chắn muốn xóa lựa chọn này?");
        builder.setPositiveButton("Có", (dialog, which) -> {
            layout_Option.removeView(layout_option);
            if(list_layout_Option.contains(layout_option)){
                list_layout_Option.remove(layout_option);
                allOptionLayoutsBeforeFilled.remove(layout_option);
                Log.d("qwer", list_layout_Option.size()+"");
            }
            Log.d("qwer", list_layout_Option.toString());
            Log.d("qwer", layout_option.toString());
        });
        builder.setNegativeButton("Không", (dialog, which) -> dialog.dismiss());
        builder.create().show();
    }

    private void showDeleteProductConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Xóa lựa chọn");
        builder.setMessage("Bạn chắc chắn muốn xóa sản phẩm này?");
        builder.setPositiveButton("Có", (dialog, which) -> {
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference productRef = database.getReference("products").child(productId);
            productRef.removeValue();
            finish();
        });
        builder.setNegativeButton("Không", (dialog, which) -> dialog.dismiss());
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

    private void updateProduct() {
        String productName = ((TextInputEditText) findViewById(R.id.et_product_name)).getText().toString().trim();
        String productDescription = ((TextInputEditText) findViewById(R.id.et_product_description)).getText().toString().trim();
        if (productName == null || productName.trim().isEmpty()) {
            etProductName.setError("Vui lòng nhập tên sản phẩm");
            return;
        }

        // Kiểm tra mô tả sản phẩm
        if (productDescription == null || productDescription.trim().isEmpty()) {
            etProductDescription.setError("Vui lòng nhập mô tả sản phẩm");
            return;
        }
        if (allOptionLayoutsBeforeFilled.size() >= 1){
            if(!checkInforOption()) return;
        }

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Đang chỉnh sửa...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        int selectedIndex = spinnerBrand.getSelectedItemPosition();
        String brandId = brandIdList.get(selectedIndex);
        Log.d("sizeeee", list_layout_Option.size() + "");

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference productRef = database.getReference("products").child(productId); // Giữ lại khóa sản phẩm cũ

        if (productId != null) {
            productRef.removeValue();
            Map<String, Color> colorMap = new HashMap<>();

            for (int i = 0; i < list_layout_Option.size(); i++) {
                View optionView = list_layout_Option.get(i);
                Log.d("??1", optionView.toString());

                TextInputEditText colorEditText = optionView.findViewById(R.id.et_color);
                TextInputEditText priceEditText = optionView.findViewById(R.id.et_price);
                TextInputEditText sizeEditText = optionView.findViewById(R.id.et_size);
                TextInputEditText quantityEditText = optionView.findViewById(R.id.et_quantity);

                String colorName = colorEditText.getText().toString();
                double price = Double.parseDouble(priceEditText.getText().toString());
                String sizeName = sizeEditText.getText().toString();
                int quantity = Integer.parseInt(quantityEditText.getText().toString());
                List<String> imagelist = optionLayoutImageMap.get(optionView); // Giả sử bạn đã lưu hình ảnh vào map

                Size size = new Size(sizeName, quantity);

                // Kiểm tra xem `colorName` đã tồn tại trong `colorMap` chưa
                if (colorMap.containsKey(colorName)) {
                    // Nếu tồn tại, cập nhật `Color` và thêm `Size` vào `sizes` của `Color`
                    Color existingColor = colorMap.get(colorName);
                    existingColor.setPrice(price); // Cập nhật giá cho màu này

                    // Kiểm tra kích thước đã tồn tại chưa
                    if (existingColor.getSizes().containsKey(sizeName)) {
                        // Nếu kích thước đã tồn tại, gộp số lượng
                        Size existingSize = existingColor.getSizes().get(sizeName);
                        existingSize.setQuantity(existingSize.getQuantity() + quantity); // Cộng thêm số lượng
                    } else {
                        // Nếu kích thước chưa tồn tại, thêm mới
                        Size newSize = new Size(sizeName, quantity);
                        existingColor.getSizes().put(sizeName, newSize); // Thêm kích thước mới vào sizes
                    }
                } else {
                    // Nếu không tồn tại màu, tạo mới
                    Map<String, Size> sizes = new HashMap<>();
                    sizes.put(sizeName, size);
                    Color color = new Color(colorName, price, imagelist, sizes);
                    colorMap.put(colorName, color); // Thêm màu mới vào colorMap
                }
            }

            // Chuyển đổi `colorMap` sang `List<Color>` để lưu vào `Product`
//            List<Color> colorList = new ArrayList<>(colorMap.values());

            // Tạo đối tượng `Product` mới và cập nhật vào Firebase
            Product updatedProduct = new Product(productId, productName, productDescription, brandId, colorMap);
            uploadImages(updatedProduct);
        }
    }




    private void uploadImages(Product product) {
        int[] uploadedCount = {0};
        final int[] totalImages = {0};

        for (Color color : product.getColors().values()) {
            totalImages[0] += color.getImages().size();
        }

        for (String colorName : product.getColors().keySet()) {
            Color color = product.getColors().get(colorName);
            List<String> uploadedImageUrls = new ArrayList<>();

            for (String imageUri : color.getImages()) {
                Uri uri = Uri.parse(imageUri);
                uploadImageToStorage(uri, new AddProductActivity.OnloadedListener() {
                    @Override
                    public void onImageUploaded(String uploadedImageUrl) {
                        uploadedImageUrls.add(uploadedImageUrl);

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

    private void saveProductToFirebase(Product product) {
        Log.d("Firebase", "Saving product: " + product.toString());
        // Save the product to Firebase Database
        productsRef.child(product.getId()).setValue(product)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(ProductDetailActivity.this, "Chỉnh sửa sản phẩm thành công", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(ProductDetailActivity.this, MainActivity.class);
                        intent.putExtra("fragmentToLoad", "product");
                        startActivity(intent);
                    } else {
                        Toast.makeText(ProductDetailActivity.this, "Chỉnh sửa sản phẩm thất bại", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void uploadImageToStorage(Uri imageUri, AddProductActivity.OnloadedListener listener) {
        if (imageUri.toString().startsWith("http://") || imageUri.toString().startsWith("https://")) {
            listener.onImageUploaded(imageUri.toString());
            return;
        }

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
                        progressDialog.dismiss();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        } catch (FileNotFoundException e) {
            Toast.makeText(this, "File not found: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }



    private void fetchBrandsFromFirebase(OnloadedListener listener) {
        brandsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                brandNumber = 0;
                for (DataSnapshot brandSnapshot : dataSnapshot.getChildren()) {
                    brandNumber += 1;
                    String brandId = brandSnapshot.getKey();
                    String brandName = brandSnapshot.child("name").getValue(String.class);
                    brandIdList.add(brandId);
                    brandNameList.add(brandName);
                }
                listener.onBrandLoaded(brandNameList, brandIdList);

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