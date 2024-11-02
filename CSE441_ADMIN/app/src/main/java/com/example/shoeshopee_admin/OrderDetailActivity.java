package com.example.shoeshopee_admin;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
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

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class OrderDetailActivity extends AppCompatActivity {

    private ImageView backBtn;
    private Button updateStatusButton;
    private Spinner statusSpinner;
    private CartProductAdapter cartProductAdapter;
    private RecyclerView productRecyclerView;
    private List<CartProduct> cartProducts = new ArrayList<>();

    ArrayAdapter<CharSequence> adapter;

    private TextView orderIdText, customerNameText, customerPhoneText, addressText, totalAmountText, statusText, noteText, timeText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_order_detail);

        statusSpinner = findViewById(R.id.statusSpinner);
        adapter = ArrayAdapter.createFromResource(this,
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
        updateStatusButton = findViewById(R.id.updateStatusButton);

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

        updateStatusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateOrderStatus(orderId);
            }
        });
    }

    private void updateOrderStatus(String orderId) {
        String selectedStatus = statusSpinner.getSelectedItem().toString();
        DatabaseReference orderRef = FirebaseDatabase.getInstance().getReference("orders").child(orderId);
        orderRef.child("status").setValue(selectedStatus).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(OrderDetailActivity.this, "Trạng thái đã được cập nhật.", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(OrderDetailActivity.this, "Cập nhật trạng thái thất bại.", Toast.LENGTH_SHORT).show();
                Log.e("OrderDetailActivity", "Error: " + task.getException());
            }
        });
    }

    private void fetchOrderDetails(String orderId) {
        DatabaseReference orderRef = FirebaseDatabase.getInstance().getReference("orders").child(orderId);

        orderRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Order order = snapshot.getValue(Order.class);
                if (order != null) {

                    orderIdText.setText(String.format("Mã đơn hàng: %s", orderId));
                    customerNameText.setText(String.format("Khách hàng: %s", order.getName()));
                    customerPhoneText.setText(String.format("Số điện thoại: %s", order.getPhone()));
                    addressText.setText(String.format("Địa chỉ giao hàng: %s", order.getAddress()));
                    totalAmountText.setText(String.format("Tổng tiền: %s", formatPrice(order.getTotal())));
                    statusText.setText(String.format("Trạng thái: %s", order.getStatus()));
                    noteText.setText(String.format("Ghi chú: %s", order.getNote()));
                    timeText.setText(String.format("Thời gian đặt hàng: %s", order.getTime()));

                    // Thiết lập giá trị cho Spinner
                    int spinnerPosition = adapter.getPosition(order.getStatus());
                    if (spinnerPosition >= 0) {
                        statusSpinner.setSelection(spinnerPosition);
                    }


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


                            DataSnapshot sizesSnapshot = colorSnapshot.child("sizes");
                            for (DataSnapshot sizeSnapshot : sizesSnapshot.getChildren()) {
                                String sizeName = sizeSnapshot.getKey();
                                int quantity = sizeSnapshot.child("quantity").getValue(Integer.class);


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

    public String formatPrice(double price) {
        NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.US);
        return "₫" + numberFormat.format(price);
    }
}
