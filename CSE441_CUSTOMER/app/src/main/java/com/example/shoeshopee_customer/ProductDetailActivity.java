package com.example.shoeshopee_customer;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.example.shoeshopee_customer.Adapter.ColorAdapter;
import com.example.shoeshopee_customer.Adapter.ProductAdapter;
import com.example.shoeshopee_customer.Adapter.ProductSliderAdapter;
import com.example.shoeshopee_customer.Adapter.SizeAdapter;
import com.example.shoeshopee_customer.Model.CartProduct;
import com.example.shoeshopee_customer.Model.Color;
import com.example.shoeshopee_customer.Model.Product;
import com.example.shoeshopee_customer.Model.Size;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tbuonomo.viewpagerdotsindicator.WormDotsIndicator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class ProductDetailActivity extends AppCompatActivity {
    private ViewPager2 viewPager;
    private ProductSliderAdapter productSliderAdapter;
    private List<String> sliderList;
    private WormDotsIndicator wormDotsIndicator;
    private String productId;
    private ImageView backImgBtn, cartImgBtn;
    Button buyNowButton;
    LinearLayout addToCartButton;
    private RecyclerView recyclerView;
    private ProductAdapter productAdapter;
    private List<Product> productList;
    private DatabaseReference databaseReference;
    TextView txtProductPriceInDetail;
    TextView txtProductNameInDetail;
    TextView txtProductDescriptionInDetail;
    Product product = null;
    CartProduct cartProduct = new CartProduct();
    private String selectedColor = null;
    private String selectedSize = null;
    List<Color> colorList = new ArrayList<>();
    List<String> sizes = new ArrayList<>();
    int quantity = 0;
    ImageView imgProductInBottomSheet;
    TextView txtProductNameInBottomSheet;
    TextView txtProductPriceInBottomSheet;
    RecyclerView recyclerviewProductColor;
    RecyclerView recyclerviewProductSize;
    ImageView imgVDeleteProductBottomSheet;
    ImageView imgVAddProductBottomSheet;
    TextView txtProductQuantityBottomSheet;
    Button btnAddProductInBottomSheet;
    String userId = "";
    BottomSheetDialog bottomSheetDialog;
    LinearLayout linearQuantityBottomSheet;
    ProgressBar progressBar;
    String linkImage = null;
    int quantityTmp = 0;
    int quantityInventory = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);
        productId = getIntent().getStringExtra("productId");
        userId = getIntent().getStringExtra("userId");

        //Anh xa id
        buyNowButton = findViewById(R.id.buyNowButton);
        addToCartButton = findViewById(R.id.addToCartButton);
        buyNowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showBottomSheet("Mua ngay");
            }
        });
        addToCartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showBottomSheet("Thêm vào giỏ hàng");
            }
        });
        recyclerView = findViewById(R.id.recyclerView);
        txtProductPriceInDetail = findViewById(R.id.txtProductPriceInDetail);
        txtProductNameInDetail = findViewById(R.id.txtProductNameInDetail);
        txtProductDescriptionInDetail = findViewById(R.id.txtProductDescriptionInDetail);
        int numberOfColumns = 2;
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, numberOfColumns);
        recyclerView.setLayoutManager(gridLayoutManager);
        productList = new ArrayList<>();
        productAdapter = new ProductAdapter(ProductDetailActivity.this, productList, userId);
        recyclerView.setAdapter(productAdapter);
        bottomSheetDialog = new BottomSheetDialog(ProductDetailActivity.this);
        bottomSheetDialog.setContentView(R.layout.bottomsheet_product_detail);
        imgProductInBottomSheet = bottomSheetDialog.findViewById(R.id.imgProductInBottomSheet);
        txtProductNameInBottomSheet = bottomSheetDialog.findViewById(R.id.txtProductNameInBottomSheet);
        txtProductPriceInBottomSheet = bottomSheetDialog.findViewById(R.id.txtProductPriceInBottomSheet);
        recyclerviewProductColor = bottomSheetDialog.findViewById(R.id.recyclerviewProductColor);
        recyclerviewProductSize = bottomSheetDialog.findViewById(R.id.recyclerviewProductSize);
        imgVDeleteProductBottomSheet = bottomSheetDialog.findViewById(R.id.imgVDeleteProductBottomSheet);
        imgVAddProductBottomSheet = bottomSheetDialog.findViewById(R.id.imgVAddProductBottomSheet);
        txtProductQuantityBottomSheet = bottomSheetDialog.findViewById(R.id.txtProductQuantityBottomSheet);
        btnAddProductInBottomSheet = bottomSheetDialog.findViewById(R.id.btnAddProductInBottomSheet);
        linearQuantityBottomSheet = bottomSheetDialog.findViewById(R.id.LinearQuantityBottomSheet);
        progressBar = bottomSheetDialog.findViewById(R.id.progressBar);
        linearQuantityBottomSheet.setVisibility(View.GONE);


        //Lay danh sach goi y
        databaseReference = FirebaseDatabase.getInstance().getReference("products");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                productList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Product product = dataSnapshot.getValue(Product.class);
                    productList.add(product);
                }
                productAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ProductDetailActivity.this, "Failed to load data.", Toast.LENGTH_SHORT).show();
            }
        });

        backImgBtn = findViewById(R.id.backImgBtn);
        cartImgBtn = findViewById(R.id.cartImgBtn);
        backImgBtn.setOnClickListener(v -> {
            finish();
        });

        cartImgBtn.setOnClickListener(v -> {
            Intent intent = new Intent(ProductDetailActivity.this, MainActivity.class);
            intent.putExtra("fragmentToLoad", "2"); // Truyền thông tin về fragment cần tải
            startActivity(intent);
        });


        //Hien thi anh
        sliderList = new ArrayList<>();
        wormDotsIndicator = findViewById(R.id.worm_dots_indicator);
        viewPager = findViewById(R.id.viewpagerSlider);
        productSliderAdapter = new ProductSliderAdapter(ProductDetailActivity.this, sliderList);
        viewPager.setAdapter(productSliderAdapter);
        wormDotsIndicator.attachTo(viewPager);


        //Hien thi chi tiet san pham
        if (productId != null) {
            Log.d("id", productId);
            loadProductDetails();
        }
    }

    private void loadProductDetails() {
        databaseReference.child(productId);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                product = snapshot.child(productId).getValue(Product.class);
                Log.d("branddddd", product.getBrandId());
                if (product != null) {
                    sliderList.clear();

                    Double firstColorPrice = null;
                    if (product.getColors() != null) {
                        for (Color color : product.getColors().values()) {
                            if (firstColorPrice == null) {
                                firstColorPrice = color.getPrice(); // Lấy giá đầu tiên và lưu vào biến
                            }
                            if (color.getImages() != null) {
                                sliderList.addAll(color.getImages());
                            }
                        }
                    }
                    txtProductPriceInDetail.setText(firstColorPrice + " ");
                    txtProductNameInDetail.setText(product.getName());
                    txtProductDescriptionInDetail.setText(product.getDescription());

                    productSliderAdapter.notifyDataSetChanged(); // Cập nhật adapter của slider
                } else {
                    Toast.makeText(ProductDetailActivity.this, "Product not found.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ProductDetailActivity.this, "Failed to load product details.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public interface OnColorsLoadedListener {
        void onColorsLoaded(Map<String, Color> colors);
        void onLoadFailed(DatabaseError error);
    }

    private void loadProductColor(OnColorsLoadedListener listener) {
        Map<String, Color> colorMap = new HashMap<>();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("products");
        databaseReference.child(productId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot colorSnapshot : dataSnapshot.child("colors").getChildren()) {
                        String colorKey = colorSnapshot.getKey();
                        String colorName = colorSnapshot.child("colorName").getValue(String.class);
                        Double price = colorSnapshot.child("price").getValue(Double.class);
                        List<String> images = new ArrayList<>();
                        for (DataSnapshot imageSnapshot : colorSnapshot.child("images").getChildren()) {
                            String imageUrl = imageSnapshot.getValue(String.class);
                            if (imageUrl != null) {
                                images.add(imageUrl);
                            }
                        }

                        Map<String, Size> sizes = new HashMap<>();
                        for (DataSnapshot sizeSnapshot : colorSnapshot.child("sizes").getChildren()) {
                            String sizeKey = sizeSnapshot.getKey();
                            Size sizeValue = sizeSnapshot.getValue(Size.class);
                            if (sizeKey != null && sizeValue != null) {
                                sizes.put(sizeKey, sizeValue);
                            }
                        }

                        if (colorKey != null && colorName != null) {
                            Color color = new Color(colorName, price != null ? price : 0.0, images, sizes);
                            colorList.add(color);
                            colorMap.put(colorKey, color);
                        }
                    }
                }
                listener.onColorsLoaded(colorMap);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("FirebaseError", databaseError.getMessage());
                listener.onLoadFailed(databaseError);
            }
        });
    }

    public interface OnSizesLoadedListener {
        void onSizesLoaded(List<String> sizes);
        void onLoadFailed(DatabaseError error); // Nếu cần xử lý lỗi
    }
    private void loadSizesForColor(String colorKey, RecyclerView recyclerviewProductSize, OnSizesLoadedListener listener) {
        DatabaseReference sizeReference = FirebaseDatabase.getInstance()
                .getReference("products")
                .child(productId)
                .child("colors")
                .child(colorKey)
                .child("sizes");

        sizes.clear();
        sizeReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                List<String> loadedSizes = new ArrayList<>();
                if (dataSnapshot.exists()) {
                    for (DataSnapshot sizeSnapshot : dataSnapshot.getChildren()) {
                        String sizeName = sizeSnapshot.child("sizeName").getValue(String.class);
                        if (sizeName != null) {
                            sizes.add(sizeName);
                        }
                    }
                }
                listener.onSizesLoaded(sizes);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("FirebaseError", databaseError.getMessage());
                listener.onLoadFailed(databaseError);
            }
        });
    }

    private void showBottomSheet(String title) {
        txtProductNameInBottomSheet.setText(product.getName());
        btnAddProductInBottomSheet.setText(title);
        colorList.clear();

        loadProductColor(new OnColorsLoadedListener() {
            @Override
            public void onColorsLoaded(Map<String, Color> colors) {
                bottomSheetDialog.show();
                Log.d("SelectedColor", "Selected Color: " + selectedColor);
//                colorList.addAll(colors.values());
                ColorAdapter colorAdapter = new ColorAdapter(ProductDetailActivity.this, colorList, new ColorAdapter.OnColorClickListener() {
                    @Override
                    public void onColorClick(Color color) {
                        selectedColor = color.getColorName();
                        Log.d("colorrrrrr", selectedColor + " ");
                        selectedSize = null;
                        linearQuantityBottomSheet.setVisibility(View.GONE);
                        quantity = 0;
                        loadSizesForColor(selectedColor, recyclerviewProductSize, new OnSizesLoadedListener() {
                            @Override
                            public void onSizesLoaded(List<String> sizes) {
                                Log.d("sizeeee", sizes.toString() + "");
                                SizeAdapter sizeAdapter = new SizeAdapter(ProductDetailActivity.this, sizes, new SizeAdapter.OnSizeClickListener() {
                                    @Override
                                    public void onSizeClick(String size) {
                                        selectedSize = size;
                                        linearQuantityBottomSheet.setVisibility(View.VISIBLE);
                                        getProductQuantityInCart(selectedColor, selectedSize, new QuantityCallback() {
                                            @Override
                                            public void onQuantityReceived(int quantity1) {
                                                quantity = quantity1;
                                                quantityTmp = quantity1;
                                                txtProductQuantityBottomSheet.setText(String.valueOf(quantity));
                                            }
                                        });
                                    }
                                });
                                recyclerviewProductSize.setLayoutManager(new GridLayoutManager(ProductDetailActivity.this, 5));
                                recyclerviewProductSize.setAdapter(sizeAdapter);
                                recyclerviewProductSize.setVisibility(sizes.isEmpty() ? View.GONE : View.VISIBLE);
                            }

                            @Override
                            public void onLoadFailed(DatabaseError error) {
                                recyclerviewProductSize.setVisibility(View.GONE);
                            }
                        });

                        // Cập nhật ảnh và giá của màu được chọn
                        if (!color.getImages().isEmpty()) {
                            linkImage = color.getImages().get(0);
                            Glide.with(ProductDetailActivity.this)
                                    .load(linkImage)
                                    .into(imgProductInBottomSheet);
                        }
                        txtProductPriceInBottomSheet.setText(String.valueOf(color.getPrice()));
                    }
                });

                recyclerviewProductColor.setLayoutManager(new GridLayoutManager(ProductDetailActivity.this, 4));
                recyclerviewProductColor.setAdapter(colorAdapter);
            }

            @Override
            public void onLoadFailed(DatabaseError error) {
                progressBar.setVisibility(View.GONE);
                // Xử lý lỗi, ví dụ hiển thị thông báo lỗi
            }
        });

        imgVAddProductBottomSheet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedColor == null && selectedSize == null) {
                    Toast.makeText(ProductDetailActivity.this, "Vui lòng chọn màu sắc và kích thước", Toast.LENGTH_SHORT).show();
                } else if (selectedColor != null && selectedSize == null) {
                    Toast.makeText(ProductDetailActivity.this, "Vui lòng chọn kích thước", Toast.LENGTH_SHORT).show();
                } else if (selectedColor != null && selectedSize != null) {
                    checkInventoryAndUpdateCart();
                }
            }
        });

        imgVDeleteProductBottomSheet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (quantity > 0) {
                    quantity -= 1;
                    txtProductQuantityBottomSheet.setText(String.valueOf(quantity));
                } else {
                    Toast.makeText(ProductDetailActivity.this, "Chưa có sản phẩm nào trong giỏ hàng", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnAddProductInBottomSheet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int quantity = Integer.parseInt(txtProductQuantityBottomSheet.getText().toString());
                String colorName = selectedColor;
                String sizeName = selectedSize;
                if (quantity <= 0) {
                    Toast.makeText(ProductDetailActivity.this, "Vui lòng chọn đầy đủ màu sắc, kích thước và số lượng", Toast.LENGTH_SHORT).show();
                } else {
                    if(title.equals("Mua ngay")){
                        String brandId = product.getBrandId();
                        DatabaseReference brandRef = FirebaseDatabase.getInstance().getReference("brands").child(brandId);
                        brandRef.child("name").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    String brandName = dataSnapshot.getValue(String.class);
                                    Intent intent = new Intent(ProductDetailActivity.this, PaymentActivity.class);
                                    cartProduct = new CartProduct(
                                            productId,
                                            product.getName(),
                                            colorName,
                                            linkImage,
                                            sizeName,
                                            txtProductPriceInBottomSheet.getText().toString(),
                                            brandName,
                                            Integer.parseInt(txtProductQuantityBottomSheet.getText().toString()),
                                            true
                                    );
                                    ArrayList<CartProduct> products = new ArrayList<>();
                                    products.add(cartProduct);
                                    int quantity = Integer.parseInt(txtProductQuantityBottomSheet.getText().toString());
                                    Double price = Double.parseDouble(txtProductPriceInBottomSheet.getText().toString());
                                    intent.putExtra("productList", products);
                                    intent.putExtra("totalAmount", String.valueOf(quantity*price));
                                    intent.putExtra("userId", userId);
                                    startActivity(intent);
                                } else {
                                    Log.d("Brand", "Không tìm thấy thương hiệu");
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                Log.e("Brand", "Lỗi khi lấy dữ liệu thương hiệu: " + databaseError.getMessage());
                            }
                        });
                    }
                    else if(title.equals("Thêm vào giỏ hàng")){
                        addToCart(quantity);
                    }
                    bottomSheetDialog.dismiss();
                }
            }
        });

        bottomSheetDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                colorList.clear();
                selectedColor = null;
                sizes.clear();
                selectedSize = null;
                linearQuantityBottomSheet.setVisibility(View.GONE);
            }
        });
//        if (isColorLoaded) {
//            bottomSheetDialog.show();
//        } else {
//            progressBar.setVisibility(View.VISIBLE);
//        }
    }

    //Kiem tra ton kho
    private void checkInventoryAndUpdateCart() {
        DatabaseReference productRef = FirebaseDatabase.getInstance().getReference("products")
                .child(productId)
                .child("colors")
                .child(selectedColor)
                .child("sizes")
                .child(selectedSize)
                .child("quantity");
        productRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    quantityInventory = dataSnapshot.getValue(Integer.class);
                    if (quantity + 1 > quantityInventory) {
                        Toast.makeText(ProductDetailActivity.this, "Số lượng vượt quá tồn kho", Toast.LENGTH_SHORT).show();
                    } else {
                        quantity += 1;
                        txtProductQuantityBottomSheet.setText(String.valueOf(quantity));
                    }
                } else {
                    Log.d("FirebaseValue", "Dữ liệu không tồn tại");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("FirebaseError", "Lỗi: " + databaseError.getMessage());
            }
        });
    }

    private void addToCart(int quantity) {
        DatabaseReference database = FirebaseDatabase.getInstance().getReference("carts");
        // Tham chiếu đến giỏ hàng của người dùng
        DatabaseReference cartRef = database.child(userId).child("items").child(productId);
        DatabaseReference colorRef = cartRef.child("colors").child(selectedColor);
        DatabaseReference sizeRef = colorRef.child("sizes").child(selectedSize);

        if (quantityTmp == 0) {
            // Nếu quantityTmp = 0, thêm màu mới vào sản phẩm
            cartRef.child("brandId").setValue(product.getBrandId());
            cartRef.child("productName").setValue(product.getName());
            sizeRef.child("quantity").setValue(quantity);
            colorRef.child("image").setValue(linkImage);
            double price = Double.parseDouble(txtProductPriceInBottomSheet.getText().toString());
            colorRef.child("price").setValue(price);
            Toast.makeText(ProductDetailActivity.this, "Thêm vào giỏ hàng thành công", Toast.LENGTH_SHORT).show();
        } else {
            // Nếu sản phẩm đã có màu đó, cập nhật số lượng
            sizeRef.child("quantity").setValue(quantity);
            Toast.makeText(ProductDetailActivity.this, "Cập nhật số lượng thành công", Toast.LENGTH_SHORT).show();
        }
    }


    //Lay so luong co san trong gio hang
    public void getProductQuantityInCart(String color, String size, QuantityCallback callback) {
        DatabaseReference database = FirebaseDatabase.getInstance().getReference("carts");
        DatabaseReference cartRef = database.child(userId).child("items").child(productId).child("colors").child(color).child("sizes").child(size).child("quantity");
        cartRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Integer quantity = dataSnapshot.getValue(Integer.class);
                if (quantity == null) {
                    quantity = 0;
                }
                callback.onQuantityReceived(quantity); // Trả về số lượng qua callback
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Xử lý lỗi khi không lấy được dữ liệu
                callback.onQuantityReceived(0); // Trả về số lượng là 0 nếu có lỗi
            }
        });
    }

    public interface QuantityCallback {
        void onQuantityReceived(int quantity);
    }
}
