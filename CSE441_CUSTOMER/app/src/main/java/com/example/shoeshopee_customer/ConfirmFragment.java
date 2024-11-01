package com.example.shoeshopee_customer;

//import android.content.Intent;
//import android.os.Bundle;
//
//import androidx.fragment.app.Fragment;
//import androidx.recyclerview.widget.LinearLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.Button;
//import android.widget.CheckBox;
//import android.widget.ImageView;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import com.example.projectadidas.Adapter.ProductAdapterInCart;
//import com.example.projectadidas.Adapter.ProductOrderTrackingAdapter;
//import com.example.projectadidas.Model.CartProduct;
//import com.example.projectadidas.Model.Order;
//import com.google.firebase.database.DataSnapshot;
//import com.google.firebase.database.DatabaseError;
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;
//import com.google.firebase.database.ValueEventListener;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//
//
//import android.os.Bundle;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import androidx.annotation.NonNull;
//import androidx.annotation.Nullable;
//import androidx.fragment.app.Fragment;
//import androidx.recyclerview.widget.LinearLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Map;
//
//public class ConfirmFragment extends Fragment {
//    private static final String ARG_USER_ID = "user_id";
//    private RecyclerView recyclerView;
//    private ProductOrderTrackingAdapter adapter;
//    private List<Order> orderList;
//
//    public static ConfirmFragment newInstance(String userId) {
//        ConfirmFragment fragment = new ConfirmFragment();
//        Bundle args = new Bundle();
//        args.putString(ARG_USER_ID, userId);
//        fragment.setArguments(args);
//        return fragment;
//    }
//
//    @Nullable
//    @Override
//    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        View view = inflater.inflate(R.layout.fragment_confirm, container, false);
//
//        recyclerView = view.findViewById(R.id.ordertrackingRecyclerView);
//        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
//        orderList = new ArrayList<>();
//        adapter = new ProductOrderTrackingAdapter(getContext(), orderList, "Chờ xác nhận");
//        recyclerView.setAdapter(adapter);
//
//        // Gọi getOrderList và cập nhật adapter khi có dữ liệu
//        getOrderList(new OrderListCallback() {
//            @Override
//            public void onOrderListLoaded(List<Order> orders) {
//                orderList.clear();
//                orderList.addAll(orders);
//                adapter.notifyDataSetChanged(); // Cập nhật adapter
//            }
//        });
//
//        return view;
//    }
//
//    public void getOrderList(OrderListCallback callback) {
//        List<Order> orders = new ArrayList<>();
//        DatabaseReference orderRef = FirebaseDatabase.getInstance().getReference("orders");
//
//        orderRef.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                for (DataSnapshot orderSnapshot : snapshot.getChildren()) {
//                    if(orderSnapshot.child("status").getValue(String.class).equals("Chờ xác nhận")) {
//                        Order order = new Order();
//                        order.setId(orderSnapshot.getKey());
//                        order.setUserId(orderSnapshot.child("userId").getValue(String.class));
//                        order.setCustomerPhone(orderSnapshot.child("phone").getValue(String.class));
//                        order.setCustomerName(orderSnapshot.child("name").getValue(String.class));
//                        order.setCustomerAddress(orderSnapshot.child("address").getValue(String.class));
//                        order.setTotalAmount(orderSnapshot.child("total").getValue(Double.class));
//                        order.setStatus(orderSnapshot.child("status").getValue(String.class));
//                        order.setNote(orderSnapshot.child("note").getValue(String.class));
//
//                        // Process items list
//                        List<CartProduct> products = new ArrayList<>();
//                        DataSnapshot itemsSnapshot = orderSnapshot.child("items");
//                        for (DataSnapshot itemSnapshot : itemsSnapshot.getChildren()) {
//                            CartProduct product = new CartProduct();
//
//                            product.setBrandName(itemSnapshot.child("brand").getValue(String.class));
//                            product.setName(itemSnapshot.child("name").getValue(String.class));
//
//                            for (DataSnapshot colorSnapshot : itemSnapshot.child("colors").getChildren()) {
//                                String colorKey = colorSnapshot.getKey();
//                                product.setColorName(colorKey);
//                                String imageUrl = colorSnapshot.child("image").getValue(String.class);
//                                product.setImage(imageUrl);
//                                Double price = colorSnapshot.child("price").getValue(Double.class);
//                                product.setPrice(price);
//
//                                for (DataSnapshot sizeSnapshot : colorSnapshot.child("sizes").getChildren()) {
//                                    String sizeKey = sizeSnapshot.getKey();
//                                    product.setSizeName(sizeKey);
//                                    Integer quantity = sizeSnapshot.child("quantity").getValue(Integer.class);
//                                    product.setQuantity(quantity);
//                                }
//                            }
//                            products.add(product);
//                        }
//                        order.setProducts(products);
//                        orders.add(order);
//                    }
//                }
//                // Gọi callback khi dữ liệu đã được tải
//                callback.onOrderListLoaded(orders);
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//                // Handle possible errors
//            }
//        });
//    }
//
//    // Định nghĩa interface callback
//    public interface OrderListCallback {
//        void onOrderListLoaded(List<Order> orders);
//    }
//}

public class ConfirmFragment extends BaseOrderFragment {
    public static ConfirmFragment newInstance() {
        return new ConfirmFragment();
    }

    @Override
    protected String getOrderStatus() {
        return "Chờ xác nhận";
    }
}

