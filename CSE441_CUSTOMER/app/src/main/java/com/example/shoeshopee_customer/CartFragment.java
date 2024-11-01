package com.example.shoeshopee_customer;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.shoeshopee_customer.Adapter.ProductAdapter;
import com.example.shoeshopee_customer.Adapter.ProductAdapterInCart;
import com.example.shoeshopee_customer.Model.CartProduct;
import com.example.shoeshopee_customer.Model.Product;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CartFragment extends Fragment {

    private static final String ARG_USER_ID = "user_id";
    private RecyclerView recyclerView;
    private ProductAdapterInCart adapter;
    private List<CartProduct> productList;
    private DatabaseReference databaseReference;
    String userId = "";
    CheckBox selectAllCheckBox;
    Button purchaseButton;
    TextView totalAmountValue;
    Double unformatPrice = 0.0;
    ImageView imgWhenCartEmpty;

    public static CartFragment newInstance(String userId) {
        CartFragment fragment = new CartFragment();
        Bundle args = new Bundle();
        args.putString(ARG_USER_ID, userId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cart, container, false);
        purchaseButton = view.findViewById(R.id.purchaseButton);
        selectAllCheckBox = view.findViewById(R.id.selectAllCheckBox);
        recyclerView = view.findViewById(R.id.cartRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        totalAmountValue = view.findViewById(R.id.totalAmountValue);
        imgWhenCartEmpty = view.findViewById(R.id.imgWhenCartEmpty);
        imgWhenCartEmpty.setVisibility(View.GONE);
        // Initialize product list and adapter
        productList = new ArrayList<>();
        // Initialize Firebase reference
        databaseReference = FirebaseDatabase.getInstance().getReference("carts");


        if (getArguments() != null) {
            userId = getArguments().getString(ARG_USER_ID);
            // Use the userId as needed
        }
        adapter = new ProductAdapterInCart(getContext(), productList, userId, totalAmountValue);
        recyclerView.setAdapter(adapter);
        // Fetch products from Firebase
        fetchProductsFromFirebase(userId);

        selectAllCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isChecked = selectAllCheckBox.isChecked(); // Lấy trạng thái của checkbox "Chọn tất cả"
                for (CartProduct product : productList) {
                    product.setSelected(isChecked); // Cập nhật tất cả sản phẩm dựa trên trạng thái của checkbox "Chọn tất cả"
                }
                adapter.notifyDataSetChanged();
            }
        });

        ArrayList<CartProduct> selectedProducts = new ArrayList<>();
        purchaseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for(CartProduct product: productList){
                    if(product.isSelected()){
                        selectedProducts.add(product);
                    }
                }
                if(selectedProducts.isEmpty()){
                    Toast.makeText(view.getContext(), "Vui lòng chọn sản phẩm cần mua", Toast.LENGTH_SHORT).show();
                }
                else {
                    unformatPrice = unformatPrice(totalAmountValue.getText().toString());
                    Intent intent = new Intent(getActivity(), PaymentActivity.class);
                    intent.putExtra("totalAmount", unformatPrice.toString());
                    intent.putExtra("userId", userId);
                    intent.putExtra("productList", selectedProducts);
                    startActivity(intent);
                }
            }
        });

        return view;
    }


    private void fetchProductsFromFirebase(String userId) {
        DatabaseReference brandsReference = FirebaseDatabase.getInstance().getReference("brands");
        brandsReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot brandsSnapshot) {
                Map<String, String> brandMap = new HashMap<>();
                for (DataSnapshot brandSnapshot : brandsSnapshot.getChildren()) {
                    String brandId = brandSnapshot.getKey();
                    String brandName = brandSnapshot.child("name").getValue(String.class);
                    if (brandId != null && brandName != null) {
                        brandMap.put(brandId.trim(), brandName);
                    }
                }
                fetchCartProducts(userId, brandMap);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("FirebaseError", error.getMessage());
            }
        });
    }

    private void fetchCartProducts(String userId, Map<String, String> brandMap) {
        DatabaseReference cartReference = FirebaseDatabase.getInstance().getReference("carts");
        cartReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                productList.clear();

                for (DataSnapshot cartSnapshot : snapshot.getChildren()) {
                    String cartUserId = cartSnapshot.getKey();

                    // Kiểm tra nếu user_id của giỏ hàng trùng với userId hiện tại
                    if (userId.equals(cartUserId)) {
                        DataSnapshot itemsSnapshot = cartSnapshot.child("items");
                        for (DataSnapshot productSnapshot : itemsSnapshot.getChildren()) {
                            String productId = productSnapshot.getKey();
                            String productName = productSnapshot.child("productName").getValue(String.class);
                            String brandId = productSnapshot.child("brandId").getValue(String.class);
                            String brandName = brandMap.get(brandId);
                            Log.d("branasdf", brandName+" ");

                            // Chỉ lấy sản phẩm nếu thương hiệu tồn tại
                            if (brandName != null) {
                                DataSnapshot colorsSnapshot = productSnapshot.child("colors");
                                for (DataSnapshot colorSnapshot : colorsSnapshot.getChildren()) {
                                    String colorName = colorSnapshot.getKey(); // Tên màu
                                    String image = colorSnapshot.child("image").getValue(String.class);
                                    Double price = colorSnapshot.child("price").getValue(Double.class);

                                    DataSnapshot sizesSnapshot = colorSnapshot.child("sizes");
                                    for (DataSnapshot sizeSnapshot : sizesSnapshot.getChildren()) {
                                        String sizeName = sizeSnapshot.getKey();
                                        Integer quantity = sizeSnapshot.child("quantity").getValue(Integer.class);

                                        // Tạo đối tượng sản phẩm mới
                                        CartProduct product = new CartProduct();
                                        product.setId(productId);
                                        product.setName(productName);
                                        product.setColorName(colorName);
                                        product.setImage(image);
                                        product.setSizeName(sizeName);
                                        product.setQuantity(quantity);
                                        product.setPrice(price != null ? price : 0.0);
                                        product.setBrandName(brandName); // Thêm thông tin thương hiệu
                                        product.setSelected(false);
                                        productList.add(product); // Thêm sản phẩm vào danh sách
                                    }
                                }
                            }
                        }
                        break; // Dừng vòng lặp khi đã tìm thấy giỏ hàng của người dùng
                    }
                }
                Log.d("asdfghj", productList.size()+"");
                if (productList.isEmpty()){
                    imgWhenCartEmpty.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                }
                else {
                    imgWhenCartEmpty.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("FirebaseError", error.getMessage());
            }
        });
    }

    public double unformatPrice(String formattedPrice) {
        try {
            // Loại bỏ các ký tự không phải số hoặc dấu chấm thập phân
            String cleanedPrice = formattedPrice.replaceAll("[^\\d.]", "");

            // Chuyển đổi chuỗi đã làm sạch về dạng double
            return Double.parseDouble(cleanedPrice);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return 0.0; // Trả về 0 nếu có lỗi chuyển đổi
        }
    }
}
