package com.example.shoeshopee_customer;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.shoeshopee_customer.Adapter.ProductOrderTrackingAdapter;
import com.example.shoeshopee_customer.Model.CartProduct;
import com.example.shoeshopee_customer.Model.Order;
import com.example.shoeshopee_customer.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class BaseOrderFragment extends Fragment {
    private RecyclerView recyclerView;
    private ProductOrderTrackingAdapter adapter;
    private List<Order> orderList;

    protected abstract String getOrderStatus();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_confirm, container, false);

        recyclerView = view.findViewById(R.id.ordertrackingRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        orderList = new ArrayList<>();
        adapter = new ProductOrderTrackingAdapter(getContext(), orderList, getOrderStatus());
        recyclerView.setAdapter(adapter);

        // Gọi getOrderList và cập nhật adapter khi có dữ liệu
        getOrderList(new OrderListCallback() {
            @Override
            public void onOrderListLoaded(List<Order> orders) {
                orderList.clear();
                orderList.addAll(orders);
                Collections.reverse(orderList);
                adapter.notifyDataSetChanged(); // Cập nhật adapter
            }
        });

        return view;
    }

    public void getOrderList(OrderListCallback callback) {
        List<Order> orders = new ArrayList<>();
        DatabaseReference orderRef = FirebaseDatabase.getInstance().getReference("orders");
        orderRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                orders.clear(); // Xóa danh sách cũ để cập nhật mới
                for (DataSnapshot orderSnapshot : snapshot.getChildren()) {
                    String status = orderSnapshot.child("status").getValue(String.class);
                    if (status != null && status.equals(getOrderStatus())) {
                        Order order = new Order();
                        order.setId(orderSnapshot.getKey());
                        order.setUserId(orderSnapshot.child("userId").getValue(String.class));
                        order.setCustomerPhone(orderSnapshot.child("phone").getValue(String.class));
                        order.setCustomerName(orderSnapshot.child("name").getValue(String.class));
                        order.setCustomerAddress(orderSnapshot.child("address").getValue(String.class));
                        order.setTotalAmount(orderSnapshot.child("total").getValue(Double.class));
                        order.setStatus(orderSnapshot.child("status").getValue(String.class));
                        order.setNote(orderSnapshot.child("note").getValue(String.class));

                        // Process items list
                        List<CartProduct> products = new ArrayList<>();
                        DataSnapshot itemsSnapshot = orderSnapshot.child("items");
                        for (DataSnapshot itemSnapshot : itemsSnapshot.getChildren()) {
                            CartProduct product = new CartProduct();

                            product.setBrandName(itemSnapshot.child("brand").getValue(String.class));
                            product.setName(itemSnapshot.child("name").getValue(String.class));

                            for (DataSnapshot colorSnapshot : itemSnapshot.child("colors").getChildren()) {
                                String colorKey = colorSnapshot.getKey();
                                product.setColorName(colorKey);
                                String imageUrl = colorSnapshot.child("image").getValue(String.class);
                                product.setImage(imageUrl);
                                Double price = colorSnapshot.child("price").getValue(Double.class);
                                product.setPrice(price);

                                for (DataSnapshot sizeSnapshot : colorSnapshot.child("sizes").getChildren()) {
                                    String sizeKey = sizeSnapshot.getKey();
                                    product.setSizeName(sizeKey);
                                    Integer quantity = sizeSnapshot.child("quantity").getValue(Integer.class);
                                    product.setQuantity(quantity);
                                }
                            }
                            products.add(product);
                            Log.d("producttt", product.toString());
                        }
                        order.setProducts(products);
                        orders.add(order);
                    }
                }
                // Gọi callback khi dữ liệu đã được tải
                callback.onOrderListLoaded(orders);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Xử lý lỗi nếu cần thiết
            }
        });
    }

    // Định nghĩa interface callback
    public interface OrderListCallback {
        void onOrderListLoaded(List<Order> orders);
    }
}
