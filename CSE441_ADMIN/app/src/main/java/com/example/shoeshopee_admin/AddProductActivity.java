//package com.example.shoeshopee_admin;
//
//import android.app.AlertDialog;
//import android.content.ClipData;
//import android.content.Intent;
//import android.net.Uri;
//import android.os.Bundle;
//import android.provider.MediaStore;
//import android.util.Log;
//import android.view.View;
//import android.widget.ArrayAdapter;
//import android.widget.Button;
//import android.widget.ImageView;
//import android.widget.LinearLayout;
//import android.widget.Spinner;
//import android.widget.Toast;
//
//import androidx.annotation.NonNull;
//import androidx.annotation.Nullable;
//import androidx.appcompat.app.AppCompatActivity;
//
//import com.bumptech.glide.Glide;
//import com.example.shoeshopee_admin.Model.Color;
//import com.example.shoeshopee_admin.Model.Product;
//import com.example.shoeshopee_admin.Model.Size;
//import com.google.android.material.textfield.TextInputEditText;
//import com.google.firebase.database.DataSnapshot;
//import com.google.firebase.database.DatabaseError;
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;
//import com.google.firebase.database.ValueEventListener;
//import com.google.firebase.storage.FirebaseStorage;
//import com.google.firebase.storage.StorageReference;
//
//import java.io.FileNotFoundException;
//import java.io.InputStream;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//public class AddProductActivity extends AppCompatActivity {
//
//    private Spinner spinnerBrand;
//    private FirebaseDatabase database;
//    private DatabaseReference brandsRef;
//    private List<String> brandIds = new ArrayList<>();
//    private List<String> brandNames = new ArrayList<>();
//    private int brandNumber = 0;
//
//    private String brandId;
//
//    private static final int REQUEST_CODE_IMAGE_PICKER = 1;
//    private LinearLayout selectedLayoutImageSelection;
//    private DatabaseReference productsRef;
//    private List<LinearLayout> allOptionLayouts = new ArrayList<>();
//    private List<LinearLayout> allOptionLayoutsBeforeFilled = new ArrayList<>();
//    private LinearLayout layoutOptionList;
//    private TextInputEditText etProductName, etProductDescription;
//    private  Map<View, List<String>> optionLayoutImageMap = new HashMap<>();
//    private LinearLayout selectedOptionLayout = null;
//
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_add_product);
//
//        spinnerBrand = findViewById(R.id.spinner_brand);
//        database = FirebaseDatabase.getInstance();
//        brandsRef = database.getReference("brands");
//
//
//        fetchBrandsFromFirebase(new OnloadedListener() {
//            @Override
//            public void onImageUploaded(String imageUrl) {
//
//            }
//
//            @Override
//            public void onBrandLoaded(List<String> brandNames, List<String> brandIds) {
//                // Được gọi khi danh sách thương hiệu đã được tải
//                ArrayAdapter<String> adapter = new ArrayAdapter<>(AddProductActivity.this, android.R.layout.simple_spinner_item, brandNames);
//                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//                spinnerBrand.setAdapter(adapter);
//            }
//        });
//
//
//        productsRef = FirebaseDatabase.getInstance().getReference("products");
//
//        etProductName = findViewById(R.id.et_product_name);
//        etProductDescription = findViewById(R.id.et_product_description);
//        layoutOptionList = findViewById(R.id.layout_option_list);
//
//        Button btnAddOption = findViewById(R.id.btn_add_option);
//        btnAddOption.setOnClickListener(v -> addOption());
//
//        Button btnAddProduct = findViewById(R.id.btn_add_product);
//        btnAddProduct.setOnClickListener(v -> addProductInFirebase());
//    }
//
//
//    private void openImagePicker(LinearLayout layoutOption) {
//        selectedOptionLayout = layoutOption;
//        Log.d("fd", "openImagePicker: "+ selectedOptionLayout.toString());
//        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
//        startActivityForResult(intent, REQUEST_CODE_IMAGE_PICKER);
//    }
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == REQUEST_CODE_IMAGE_PICKER && resultCode == RESULT_OK && data != null && selectedOptionLayout != null) {
//            // Tạo danh sách tạm thời để lưu ảnh đã chọn cho layout hiện tại
//            List<String> selectedImageUris = new ArrayList<>();
//            if (data.getClipData() != null) {
//                ClipData clipData = data.getClipData();
//                for (int i = 0; i < clipData.getItemCount(); i++) {
//                    Uri imageUri = clipData.getItemAt(i).getUri();
//                    selectedImageUris.add(imageUri.toString());
//                    addImageToSelection(imageUri);
//                }
//            } else if (data.getData() != null) {
//                Uri imageUri = data.getData();
//                selectedImageUris.add(imageUri.toString());
//                addImageToSelection(imageUri);
//            }
//
//            // Kiểm tra nếu có ảnh được chọn, thì thêm layout vào danh sách
//            if (!selectedImageUris.isEmpty()) {
//                optionLayoutImageMap.put(selectedOptionLayout, selectedImageUris);
//                Log.d("df", optionLayoutImageMap.toString());
//                allOptionLayouts.add(selectedOptionLayout);
//            }
//
//            // Đặt selectedOptionLayout về null để tránh lỗi nếu chọn lại
//            selectedOptionLayout = null;
//        }
//    }
//
//    private Boolean checkInforOption() {
//        LinearLayout optionView = allOptionLayoutsBeforeFilled.get(allOptionLayoutsBeforeFilled.size() - 1);
//        TextInputEditText colorEditText = optionView.findViewById(R.id.et_color);
//        TextInputEditText priceEditText = optionView.findViewById(R.id.et_price);
//        TextInputEditText sizeEditText = optionView.findViewById(R.id.et_size);
//        TextInputEditText quantityEditText = optionView.findViewById(R.id.et_quantity);
//        LinearLayout layoutImageSelection = optionView.findViewById(R.id.layout_image_selection);
//
//        // Kiểm tra giá trị của các trường nhập liệu, thay vì chỉ kiểm tra null của EditText
//        if (colorEditText.getText() == null || colorEditText.getText().toString().trim().isEmpty()) {
//            colorEditText.setError("Vui lòng nhập màu lựa chọn");
//            return false;
//        }
//        if (priceEditText.getText() == null || priceEditText.getText().toString().trim().isEmpty()) {
//            priceEditText.setError("Vui lòng nhập giá lựa chọn");
//            return false;
//        }
//        if (sizeEditText.getText() == null || sizeEditText.getText().toString().trim().isEmpty()) {
//            sizeEditText.setError("Vui lòng nhập kích thước lựa chọn");
//            return false;
//        }
//        if (quantityEditText.getText() == null || quantityEditText.getText().toString().trim().isEmpty()) {
//            quantityEditText.setError("Vui lòng nhập số lượng lựa chọn");
//            return false;
//        }
//        if (layoutImageSelection.getChildCount() == 0){
//            Toast.makeText(optionView.getContext(), "Vui lòng chọn ít nhất một ảnh", Toast.LENGTH_SHORT).show();
//            return false;
//        }
//        return true;
//    }
//
//
//    private void addOption() {
//        Log.d("sizeliedddd", allOptionLayoutsBeforeFilled.size()+"");
//        Boolean check = true;
//        if (allOptionLayoutsBeforeFilled.size() >= 1){
//             check = checkInforOption();
//        }
//        if (check){
//            LinearLayout newOptionLayout = (LinearLayout) getLayoutInflater().inflate(R.layout.layout_option_item, null);
//            allOptionLayoutsBeforeFilled.add(newOptionLayout);
//            Button btnSelectImages = newOptionLayout.findViewById(R.id.btn_select_images);
//            LinearLayout layoutImageSelection = newOptionLayout.findViewById(R.id.layout_image_selection);
//
//            btnSelectImages.setOnClickListener(v -> {
//                openImagePicker(newOptionLayout);
//                selectedLayoutImageSelection = layoutImageSelection;
//            });
//            Button btnDeleteOption = newOptionLayout.findViewById(R.id.btn_delete_option);
//            btnDeleteOption.setOnClickListener(v -> showDeleteConfirmationDialog(newOptionLayout));
//            layoutOptionList.addView(newOptionLayout);
//        }
//
//    }
//
//    private void showDeleteConfirmationDialog(LinearLayout optionLayout) {
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        builder.setTitle("Xóa lựa chọn");
//        builder.setMessage("Bạn chắc chắn muốn xóa lựa chọn này?");
//        builder.setPositiveButton("Yes", (dialog, which) -> {
//            layoutOptionList.removeView(optionLayout);
//            allOptionLayouts.remove(optionLayout);
//            Log.d("???", optionLayoutImageMap.values().toString());
//            allOptionLayoutsBeforeFilled.remove(optionLayout);
//            optionLayoutImageMap.remove(optionLayout);
//            Log.d("qwertyujgf",  optionLayoutImageMap.values().toString());
//        });
//        builder.setNegativeButton("No", (dialog, which) -> dialog.dismiss());
//        builder.create().show();
//    }
//
//    private void addImageToSelection(Uri imageUri) {
//        if (selectedLayoutImageSelection == null) return;
//        ImageView imageView = new ImageView(this);
//        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
//                100, 100
//        );
//        layoutParams.setMargins(8, 8, 8, 8);
//        imageView.setLayoutParams(layoutParams);
//        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
//        imageView.setTag(imageUri);
//        Glide.with(this).load(imageUri).into(imageView);
//        selectedLayoutImageSelection.addView(imageView);
//    }
//
//    public void addProductInFirebase() {
//        String productName = etProductName.getText().toString();
//        String productDescription = etProductDescription.getText().toString();
//
//        // Kiểm tra tên sản phẩm
//        if (productName == null || productName.trim().isEmpty()) {
//            etProductName.setError("Vui lòng nhập tên sản phẩm");
//            return;
//        }
//
//        // Kiểm tra mô tả sản phẩm
//        if (productDescription == null || productDescription.trim().isEmpty()) {
//            etProductDescription.setError("Vui lòng nhập mô tả sản phẩm");
//            return;
//        }
//        int position = spinnerBrand.getSelectedItemPosition();
//        Log.d("positon", position + "");
//        brandId = brandIds.get(position);
//        Log.d("brandId", brandId);
//
//        DatabaseReference productRef = FirebaseDatabase.getInstance().getReference("products");
//        productRef.orderByChild("name").equalTo(productName).addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot snapshot) {
//                String productId = "";
//                Map<String, Color> colors = new HashMap<>();
//                if (snapshot.exists()) {
//                    DataSnapshot existingProductSnapshot = snapshot.getChildren().iterator().next();
//                    productId = existingProductSnapshot.getKey();
//                    Product existingProduct = existingProductSnapshot.getValue(Product.class);
//                    if (existingProduct != null && existingProduct.getColors() != null) {
//                        colors = existingProduct.getColors();
//                    }
//                } else {
//                    productId = productRef.push().getKey();
//                    if (productId == null) {
//                        Toast.makeText(AddProductActivity.this, "Failed to generate product ID!", Toast.LENGTH_SHORT).show();
//                        return;
//                    }
//                }
//
//                for (int i = 0; i < allOptionLayouts.size(); i++) {
//                    View optionView = allOptionLayouts.get(i);
//                    Log.d("??1", optionView.toString());
//
//                    List<String> imagelist = optionLayoutImageMap.get(optionView);
//
//                    Log.d("??2", imagelist.toString());
//                    TextInputEditText colorEditText = optionView.findViewById(R.id.et_color);
//                    TextInputEditText priceEditText = optionView.findViewById(R.id.et_price);
//                    TextInputEditText sizeEditText = optionView.findViewById(R.id.et_size);
//                    TextInputEditText quantityEditText = optionView.findViewById(R.id.et_quantity);
//
//                    String colorName = colorEditText.getText().toString();
//                    double price = Double.parseDouble(priceEditText.getText().toString());
//
//                    if (colors.containsKey(colorName)) {
//                        Color existingColor = colors.get(colorName);
//                        String sizeName = sizeEditText.getText().toString();
//                        int quantity = Integer.parseInt(quantityEditText.getText().toString());
//
//                        if (existingColor.getSizes().containsKey(sizeName)) {
//                            Size existingSize = existingColor.getSizes().get(sizeName);
//                            existingSize.setQuantity(existingSize.getQuantity() + quantity);
//
//                        } else {
//                            existingColor.getSizes().put(sizeName, new Size(sizeName, quantity));
//                        }
//
//                        existingColor.setPrice(price);
//                        existingColor.setImages(imagelist);
//                        Log.d("ioes", "onDataChange: "+existingColor.getImages().toString());
//                    } else {
//                        Map<String, Size> sizes = new HashMap<>();
//                        String sizeName = sizeEditText.getText().toString();
//                        int quantity = Integer.parseInt(quantityEditText.getText().toString());
//                        sizes.put(sizeName, new Size(sizeName, quantity));
//
//                        colors.put(colorName, new Color(colorName, price, imagelist, sizes));
//                        Log.d("ioes4", "onDataChange: "+colors.size());
//                    }
//                }
//
//                Product product = new Product(productId, productName, productDescription, brandId, colors);
//
//                uploadImagesAndAddProduct(product);
//            }
//
//            @Override
//            public void onCancelled(DatabaseError error) {
//                Toast.makeText(AddProductActivity.this, "Database error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
//            }
//        });
//    }
//
//    private void uploadImagesAndAddProduct(Product product) {
//        int[] uploadedCount = {0};
//        final int[] totalImages = {0};
//
//
//        for (Color color : product.getColors().values()) {
//            totalImages[0] += color.getImages().size();
//        }
//
//        for (String colorName : product.getColors().keySet()) {
//            Color color = product.getColors().get(colorName);
//            List<String> uploadedImageUrls = new ArrayList<>();
//
//            for (String imageUri : color.getImages()) {
//                Uri uri = Uri.parse(imageUri);
//                uploadImageToStorage(uri, new OnloadedListener() {
//                    @Override
//                    public void onImageUploaded(String uploadedImageUrl) {
//                        uploadedImageUrls.add(uploadedImageUrl);
//
//                        if (uploadedImageUrls.size() == color.getImages().size()) {
//                            color.setImages(uploadedImageUrls);
//                        }
//
//                        uploadedCount[0]++;
//                        if (uploadedCount[0] == totalImages[0]) {
//                            saveProductToFirebase(product);
//                        }
//                    }
//
//                    @Override
//                    public void onBrandLoaded(List<String> brandNames, List<String> brandIds) {
//
//                    }
//
//                });
//            }
//        }
//    }
//
//    private void saveProductToFirebase(Product product) {
//        Log.d("Firebase", "Saving product: " + product.toString());
//        // Save the product to Firebase Database
//        productsRef.child(product.getId()).setValue(product)
//                .addOnCompleteListener(task -> {
//                    if (task.isSuccessful()) {
//                        Toast.makeText(AddProductActivity.this, "Product added successfully", Toast.LENGTH_SHORT).show();
//                        finish();
//
//
//                    } else {
//                        Toast.makeText(AddProductActivity.this, "Failed to add product", Toast.LENGTH_SHORT).show();
//                    }
//                });
//    }
//
//    private void uploadImageToStorage(Uri imageUri, OnloadedListener listener) {
//        StorageReference storageReference = FirebaseStorage.getInstance().getReference("images/" + imageUri.getLastPathSegment());
//        try {
//            InputStream inputStream = getContentResolver().openInputStream(imageUri);
//            if (inputStream == null) {
//                throw new FileNotFoundException("Failed to open stream for URI: " + imageUri);
//            }
//
//            // Tải lên hình ảnh
//            storageReference.putStream(inputStream)
//                    .addOnSuccessListener(taskSnapshot -> {
//                        storageReference.getDownloadUrl().addOnSuccessListener(downloadUri -> {
//                            String imageUrl = downloadUri.toString();
//                            listener.onImageUploaded(imageUrl);
//                        });
//                    })
//                    .addOnFailureListener(e -> {
//                        Toast.makeText(this, "Upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
//                    });
//        } catch (FileNotFoundException e) {
//            Toast.makeText(this, "File not found: " + e.getMessage(), Toast.LENGTH_SHORT).show();
//        }
//    }
//
//    private void fetchBrandsFromFirebase(OnloadedListener listener) {
//        brandsRef.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                brandNumber = 0;
//                for (DataSnapshot brandSnapshot : dataSnapshot.getChildren()) {
//                    brandNumber += 1;
//                    String brandId = brandSnapshot.getKey();
//                    String brandName = brandSnapshot.child("name").getValue(String.class);
//                    brandIds.add(brandId);
//                    brandNames.add(brandName);
//                }
//                listener.onBrandLoaded(brandNames, brandIds);
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//                Log.e("AddProductActivity", "Failed to fetch brands: " + databaseError.getMessage());
//            }
//        });
//    }
//
//    interface OnloadedListener {
//        void onImageUploaded(String imageUrl);
//        void onBrandLoaded(List<String> brandNames, List<String> brandIds);
//    }
//
//
//
//}

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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddProductActivity extends AppCompatActivity {

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
    private List<LinearLayout> allOptionLayoutsBeforeFilled = new ArrayList<>();
    private LinearLayout layoutOptionList;

    private TextInputEditText etProductName, etProductDescription;

    private  Map<View, List<String>> optionLayoutImageMap = new HashMap<>();
    private LinearLayout selectedOptionLayout = null;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);

        spinnerBrand = findViewById(R.id.spinner_brand);
        database = FirebaseDatabase.getInstance();
        brandsRef = database.getReference("brands");


        fetchBrandsFromFirebase(new OnloadedListener() {
            @Override
            public void onImageUploaded(String imageUrl) {

            }

            @Override
            public void onBrandLoaded(List<String> brandNames, List<String> brandIds) {
                // Được gọi khi danh sách thương hiệu đã được tải
                ArrayAdapter<String> adapter = new ArrayAdapter<>(AddProductActivity.this, android.R.layout.simple_spinner_item, brandNames);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerBrand.setAdapter(adapter);
            }
        });


        productsRef = FirebaseDatabase.getInstance().getReference("products");

        etProductName = findViewById(R.id.et_product_name);
        etProductDescription = findViewById(R.id.et_product_description);
        layoutOptionList = findViewById(R.id.layout_option_list);

        Button btnAddOption = findViewById(R.id.btn_add_option);
        btnAddOption.setOnClickListener(v -> addOption());

        Button btnAddProduct = findViewById(R.id.btn_add_product);
        btnAddProduct.setOnClickListener(v -> addOrUpdateProductInFirebase());
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
            // Tạo danh sách tạm thời để lưu ảnh đã chọn cho layout hiện tại
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

            // Kiểm tra nếu có ảnh được chọn, thì thêm layout vào danh sách
            if (!selectedImageUris.isEmpty()) {
                optionLayoutImageMap.put(selectedOptionLayout, selectedImageUris);
                Log.d("df", optionLayoutImageMap.toString());
                allOptionLayouts.add(selectedOptionLayout);
            }

            // Đặt selectedOptionLayout về null để tránh lỗi nếu chọn lại
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
        Log.d("sizeliedddd", allOptionLayoutsBeforeFilled.size()+"");
        Boolean check = true;
        if (allOptionLayoutsBeforeFilled.size() >= 1){
             check = checkInforOption();
        }
        if (check){
            LinearLayout newOptionLayout = (LinearLayout) getLayoutInflater().inflate(R.layout.layout_option_item, null);
            allOptionLayoutsBeforeFilled.add(newOptionLayout);
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
    }

    private void showDeleteConfirmationDialog(LinearLayout optionLayout) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete Option");
        builder.setMessage("Are you sure you want to delete this option?");
        builder.setPositiveButton("Yes", (dialog, which) -> {
            layoutOptionList.removeView(optionLayout);
            allOptionLayouts.remove(optionLayout);
            allOptionLayoutsBeforeFilled.remove(optionLayout);
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
                        Toast.makeText(AddProductActivity.this, "Failed to generate product ID!", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(AddProductActivity.this, "Database error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
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
                Uri uri = Uri.parse(imageUri);
                uploadImageToStorage(uri, new OnloadedListener() {
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
                        Toast.makeText(AddProductActivity.this, "Product added successfully", Toast.LENGTH_SHORT).show();
                        finish();


                    } else {
                        Toast.makeText(AddProductActivity.this, "Failed to add product", Toast.LENGTH_SHORT).show();
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
                brandNumber = 0;
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