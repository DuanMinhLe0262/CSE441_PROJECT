package com.example.shoeshopee_customer;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.shoeshopee_customer.Adapter.ProductAdapterInCart;
import com.example.shoeshopee_customer.Adapter.ProductAdapterInPayment;
import com.example.shoeshopee_customer.Model.CartProduct;
import com.example.shoeshopee_customer.Model.Order;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class PaymentActivity extends AppCompatActivity {
    LinearLayout linearShowAddressFill, linearAddressFill;
    EditText edtAddressPayment, edtNamePayment, edtPhonePayment, edtNotePayment;
    RecyclerView recyclerviewProductPayment;
    TextView txtTotalProductAmountPayment, txtShipcostPayment, txtdiscountPayment, txtTotalAmountPayment
            , txtTotalAmountPaymentWithButton, txtInfoInPayment, txtPaymentDetail;
    Button paymentButton;
    Boolean showAddresFill = false;
    List<CartProduct> productList = new ArrayList<>();
    private ProductAdapterInPayment adapter;
    Drawable place_icon, account_icon, phone_icon, note_icon, more_icon, info_icon, dollar_icon;
    String address = "", name = "", phone = "", note = "";
    Double discount = 12000.0, shipcost = 30000.0;
    String userId = "";
    Double totalAmount = 0.0;

    ImageView backImgBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_payment);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        backImgBtn = findViewById(R.id.backImgBtn);

        backImgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        place_icon = getResources().getDrawable(R.drawable.place_icon);
        setSizeIcon(place_icon);
        account_icon = getResources().getDrawable(R.drawable.account_icon);
        setSizeIcon(account_icon);
        phone_icon = getResources().getDrawable(R.drawable.phone_icon);
        setSizeIcon(phone_icon);
        note_icon = getResources().getDrawable(R.drawable.note_icon);
        setSizeIcon(note_icon);
        more_icon = getResources().getDrawable(R.drawable.more_icon);
        setSizeIcon(more_icon);
        info_icon = getResources().getDrawable(R.drawable.info_icon);
        setSizeIcon(info_icon);
        dollar_icon = getResources().getDrawable(R.drawable.dollar_icon);
        setSizeIcon(dollar_icon);

        txtInfoInPayment = findViewById(R.id.txtInfoInPayment);
        txtInfoInPayment.setCompoundDrawables(info_icon, null, more_icon, null);

        edtAddressPayment = findViewById(R.id.edtAddressPayment);
        edtAddressPayment.setCompoundDrawables(null, null, place_icon, null);

        edtNamePayment = findViewById(R.id.edtNamePayment);
        edtNamePayment.setCompoundDrawables(null, null, account_icon, null);

        edtPhonePayment = findViewById(R.id.edtPhonePayment);
        edtPhonePayment.setCompoundDrawables(null, null, phone_icon, null);

        edtNotePayment = findViewById(R.id.edtNotePayment);
        edtNotePayment.setCompoundDrawables(null, null, note_icon, null);

        txtPaymentDetail = findViewById(R.id.txtPaymentDetail);
        txtPaymentDetail.setCompoundDrawables(dollar_icon, null, null, null);

        linearShowAddressFill = findViewById(R.id.linearShowAddressFill);
        linearAddressFill = findViewById(R.id.linearAddressFill);
        recyclerviewProductPayment = findViewById(R.id.recyclerviewProductPayment);
        txtTotalProductAmountPayment = findViewById(R.id.txtTotalProductAmountPayment);
        txtShipcostPayment = findViewById(R.id.txtShipcostPayment);
        txtdiscountPayment = findViewById(R.id.txtdiscountPayment);
        txtTotalAmountPayment = findViewById(R.id.txtTotalAmountPayment);
        txtTotalAmountPaymentWithButton = findViewById(R.id.txtTotalAmountPaymentWithButton);
        paymentButton = findViewById(R.id.paymentButton);

        linearAddressFill.setVisibility(View.VISIBLE);
        linearShowAddressFill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddresFill = !showAddresFill;
                if(showAddresFill){
                    linearAddressFill.setVisibility(View.VISIBLE);
                }
                else linearAddressFill.setVisibility(View.GONE);
            }
        });

        userId = getIntent().getStringExtra("userId");
        String totalString = "";
        totalString = getIntent().getStringExtra("totalAmount");
        Double totalProductAmount = Double.parseDouble(totalString.toString());
        txtTotalProductAmountPayment.setText(formatPrice(totalProductAmount));
        txtShipcostPayment.setText(formatPrice(shipcost));
        txtdiscountPayment.setText(formatPrice(discount));
        totalAmount = totalProductAmount + shipcost - discount;
        txtTotalAmountPayment.setText(formatPrice(totalAmount));
        txtTotalAmountPaymentWithButton.setText(formatPrice(totalAmount));
        productList = (ArrayList<CartProduct>) getIntent().getSerializableExtra("productList");
        recyclerviewProductPayment.setLayoutManager(new LinearLayoutManager(PaymentActivity.this));
        adapter = new ProductAdapterInPayment(PaymentActivity.this, productList);
        recyclerviewProductPayment.setAdapter(adapter);

        paymentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                address = edtAddressPayment.getText().toString().trim();
                if(address.isEmpty()) {
                    edtAddressPayment.setError("Vui lòng nhập địa chỉ");
                    return;
                }
                name = edtNamePayment.getText().toString().trim();
                if(name.isEmpty()){
                    edtNamePayment.setError("Vui lòng nhập tên");
                    return;
                }
                phone = edtPhonePayment.getText().toString().trim();
                if(phone.isEmpty()){
                    edtPhonePayment.setError("Vui lòng nhập số điện thoại");
                    return;
                }
                note = edtNotePayment.getText().toString().trim();
                DatabaseReference databaseOrder = FirebaseDatabase.getInstance().getReference("orders");
                // Tạo ID ngẫu nhiên cho đơn hàng
                String orderId = databaseOrder.push().getKey();
                long currentTimeMillis = System.currentTimeMillis();
                // Định dạng thời gian theo định dạng "HH:mm:ss dd:MM:yyyy"
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss dd:MM:yyyy", Locale.getDefault());
                String formattedTime = sdf.format(new Date(currentTimeMillis));
                Order order = new Order(orderId,
                        userId,
                        phone,
                        name,
                        address,
                        productList,
                        totalAmount,
                        "Chờ xác nhận",
                        note,
                        formattedTime);
                addOrder(order, new OnOrderCompleteListener() {
                    @Override
                    public void onOrderComplete() {
                        Intent intent = new Intent(PaymentActivity.this, OrderTrackingActivity.class);
                        intent.putExtra("userId", userId);
                        startActivity(intent);
                        finish();
                        for (CartProduct product : productList) {
                            DatabaseReference cartRef = FirebaseDatabase.getInstance().getReference("carts").child(userId);
                            DatabaseReference productRef = cartRef.child("items").child(product.getId());
                            DatabaseReference colorRef = productRef.child("colors").child(product.getColorName());
                            colorRef.removeValue().addOnCompleteListener(
                                    removeTask -> {
                                        if (removeTask.isSuccessful()) {
                                            Log.d("Order", "Giỏ hàng đã được xóa thành công sau khi tạo đơn hàng.");
                                        } else {
                                            Log.e("Order", "Không thể xóa giỏ hàng: " + removeTask.getException());
                                        }
                                    });
                        }
                    }
                });
            }
        });
    }
    private void addOrder(Order order, OnOrderCompleteListener listener){
        DatabaseReference databaseOrder = FirebaseDatabase.getInstance().getReference("orders");
        DatabaseReference orderRef = databaseOrder.child(order.getId());
        orderRef.child("userId").setValue(order.getUserId());
        orderRef.child("phone").setValue(order.getCustomerPhone());
        orderRef.child("name").setValue(order.getCustomerName());
        orderRef.child("address").setValue(order.getCustomerAddress());
        orderRef.child("status").setValue(order.getStatus());
        orderRef.child("total").setValue(order.getTotalAmount());
        orderRef.child("note").setValue(order.getNote());
        orderRef.child("time").setValue(order.getTime());
        DatabaseReference itemRef = orderRef.child("items");
        for (CartProduct product : productList) {
            DatabaseReference productRef = itemRef.child(product.getId());
            productRef.child("name").setValue(product.getName());
            productRef.child("brand").setValue(product.getBrandName());
            DatabaseReference colorRef = productRef.child("colors").child(product.getColorName());
            DatabaseReference sizeRef = colorRef.child("sizes").child(product.getSizeName());
            colorRef.child("price").setValue(product.getPrice());
            colorRef.child("image").setValue(product.getImage());
            sizeRef.child("quantity").setValue(product.getQuantity());
            updateProductQuantityAfterOrder(product.getId(), product.getColorName(), product.getSizeName(),product.getQuantity());
        }
        listener.onOrderComplete();
    }

    public interface OnOrderCompleteListener {
        void onOrderComplete();
    }

    private void setSizeIcon(Drawable drawable){
        drawable.setBounds(0, 0, 70, 70);
    }

    private Boolean isValid(){
        return !address.isEmpty() && !name.isEmpty() && !phone.isEmpty();
    }

    public String formatPrice(double price) {
        NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.US);
        return "₫" + numberFormat.format(price);
    }

    public void updateProductQuantityAfterOrder(String productId, String colorName, String sizeName, int quantityOrdered) {
        // Lấy tham chiếu đến sản phẩm cụ thể
        DatabaseReference productQuantityRef = FirebaseDatabase.getInstance().getReference("products")
                .child(productId)
                .child("colors")
                .child(colorName)
                .child("sizes")
                .child(sizeName)
                .child("quantity");

        // Truy vấn giá trị hiện tại của quantity
        productQuantityRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Lấy giá trị hiện tại của quantity
                    int currentQuantity = dataSnapshot.getValue(Integer.class);

                    // Kiểm tra xem số lượng còn đủ không
                    if (currentQuantity >= quantityOrdered) {
                        // Tính số lượng mới
                        int newQuantity = currentQuantity - quantityOrdered;

                        // Cập nhật số lượng mới vào Firebase
                        productQuantityRef.setValue(newQuantity)
                                .addOnSuccessListener(aVoid -> {
                                    Log.d("FirebaseUpdate", "Số lượng đã được cập nhật thành công.");
                                })
                                .addOnFailureListener(e -> {
                                    Log.e("FirebaseUpdate", "Cập nhật số lượng thất bại: " + e.getMessage());
                                });
                    } else {
                        Log.e("FirebaseUpdate", "Không đủ số lượng để đặt hàng.");
                    }
                } else {
                    Log.e("FirebaseUpdate", "Nút quantity không tồn tại.");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("FirebaseUpdate", "Lỗi khi truy vấn: " + databaseError.getMessage());
            }
        });
    }

}