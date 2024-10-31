package com.example.shoeshopee_admin;

import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.shoeshopee_admin.Adapter.CartProductAdapter;
import com.example.shoeshopee_admin.Model.CartProduct;
import com.example.shoeshopee_admin.Model.Order;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class OrderDetailActivity extends AppCompatActivity {

    private ImageView backBtn;
    private Spinner statusSpinner;
    private CartProductAdapter cartProductAdapter;
    private RecyclerView productRecyclerView;
    private List<CartProduct> cartProducts = new ArrayList<>();

    private TextView orderIdText, customerNameText, customerPhoneText, addressText, totalAmountText, statusText, noteText, timeText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_order_detail);

        Spinner statusSpinner = findViewById(R.id.statusSpinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.order_status, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        statusSpinner.setAdapter(adapter);



        backBtn = findViewById(R.id.backBtn);
        orderIdText = findViewById(R.id.orderIdText);
        customerNameText = findViewById(R.id.customerNameText);
        customerPhoneText = findViewById(R.id.customerPhoneText);
        addressText = findViewById(R.id.customerAddressText);
        totalAmountText = findViewById(R.id.totalAmountText);
        statusText = findViewById(R.id.statusText);
        noteText = findViewById(R.id.noteText);
        timeText = findViewById(R.id.timeText);

        String orderId = getIntent().getStringExtra("ORDER_ID");
        if (orderId != null) {
            fetchOrderDetails(orderId);
        } else {
            Toast.makeText(this, "Không tìm thấy mã đơn hàng.", Toast.LENGTH_SHORT).show();
            finish();
        }


        RecyclerView productRecyclerView = findViewById(R.id.productRecyclerView);
        cartProductAdapter = new CartProductAdapter(cartProducts);
        productRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        productRecyclerView.setAdapter(cartProductAdapter);

        backBtn.setOnClickListener(v -> finish());
    }

    private void fetchOrderDetails(String orderId) {
        DatabaseReference orderRef = FirebaseDatabase.getInstance().getReference("orders").child(orderId);

        orderRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Order order = snapshot.getValue(Order.class);
                if (order != null) {
                    // Gán dữ liệu cho các TextView
                    orderIdText.setText(String.format("Order ID: %s", orderId));
                    customerNameText.setText(String.format("Customer: %s", order.getCustomerName()));
                    customerPhoneText.setText(String.format("Phone: %s", order.getCustomerPhone()));
                    addressText.setText(String.format("Address: %s", order.getCustomerAddress()));
                    totalAmountText.setText(String.format("Total: %.2f", order.getTotalAmount()));
                    statusText.setText(String.format("Status: %s", order.getStatus()));
                    noteText.setText(String.format("Note: %s", order.getNote()));
                    timeText.setText(String.format("Time: %s", order.getTime()));


                    DataSnapshot itemsSnapshot = snapshot.child("items");
                    for (DataSnapshot itemSnapshot : itemsSnapshot.getChildren()) {
                        String productId = itemSnapshot.getKey();
                        String productName = itemSnapshot.child("name").getValue(String.class);
                        String brandName = itemSnapshot.child("brand").getValue(String.class);

                        // Lấy thông tin màu sắc
                        DataSnapshot colorsSnapshot = itemSnapshot.child("colors");
                        for (DataSnapshot colorSnapshot : colorsSnapshot.getChildren()) {
                            String colorName = colorSnapshot.getKey();
                            String image = colorSnapshot.child("image").getValue(String.class);
                            double price = colorSnapshot.child("price").getValue(Double.class);

                            // Lấy thông tin kích thước
                            DataSnapshot sizesSnapshot = colorSnapshot.child("sizes");
                            for (DataSnapshot sizeSnapshot : sizesSnapshot.getChildren()) {
                                String sizeName = sizeSnapshot.getKey();
                                int quantity = sizeSnapshot.child("quantity").getValue(Integer.class);

                                // Tạo đối tượng CartProduct và thêm vào danh sách
                                CartProduct cartProduct = new CartProduct(productId, productName, colorName, image, sizeName, price, brandName, quantity);
                                cartProducts.add(cartProduct);

                            }
                        }

                    }
                    cartProductAdapter.notifyDataSetChanged();

                } else {
                    Toast.makeText(OrderDetailActivity.this, "Dữ liệu đơn hàng không tồn tại.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(OrderDetailActivity.this, "Không thể tải dữ liệu.", Toast.LENGTH_SHORT).show();
                Log.e("OrderDetailActivity", "DatabaseError: " + error.getMessage());
            }
        });
    }
}
