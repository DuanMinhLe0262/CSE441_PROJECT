package com.example.shoeshopee_customer.Adapter;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.shoeshopee_customer.Model.CartProduct;
import com.example.shoeshopee_customer.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class ProductAdapterInCart extends RecyclerView.Adapter<ProductAdapterInCart.ProductViewHolder> {

    private Context context;
    private List<CartProduct> productList;
    private String userId;
    private TextView txtTotalPrice;
    int quantityInventoryTmp = 0;
    int quantitybefore = 0;

    public ProductAdapterInCart(Context context, List<CartProduct> productList, String userId, TextView txtTotalPrice) {
        this.context = context;
        this.productList = productList;
        this.userId = userId;
        this.txtTotalPrice = txtTotalPrice;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_product_cart, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        CartProduct product = productList.get(position);
        holder.txtProductNameInCart.setText(product.getName());
        holder.txtBrandNameInCart.setText(product.getBrandName());
        holder.txtProductDescriptionInCart.setText(product.getColorName() + ", " + product.getSizeName());
        // Cập nhật số lượng và giá
        holder.edtProductQuantityInCart.setText(String.valueOf(product.getQuantity()));
        int quantity = product.getQuantity();
        double price = Double.parseDouble(product.getPrice().toString()) * quantity;
        holder.txtProductToMoneyInCart.setText(formatPrice(price));
        holder.ckProductInCart.setChecked(product.isSelected());
        updateTotalPrice();
        holder.ckProductInCart.setOnCheckedChangeListener((buttonView, isChecked) -> {
            product.setSelected(isChecked);
            updateTotalPrice();
        });

        Glide.with(context)
                .load(product.getImage())
                .placeholder(R.drawable.load)
                .into(holder.imgProductInCart);

        holder.imgVTrashFromCart.setOnClickListener(view -> {
            Log.d("trash", "asdfg");
            DatabaseReference sizeRef = createRef(product);
            productList.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, productList.size());
            sizeRef.removeValue();
            updateTotalPrice();
        });

        holder.imgVAddProductToCart.setOnClickListener(view -> {
            Log.d("add", "asdfg");
            getQuantityInventory(product, new QuantityCallback() {
                @Override
                public void onQuantityRetrieved(int quantityInventory) {
                    if (product.getQuantity() + 1 > quantityInventory) {
                        Toast.makeText(context, "Số lượng vượt quá tồn kho", Toast.LENGTH_SHORT).show();
                    } else {
                        int newQuantity = product.getQuantity() + 1;
                        product.setQuantity(newQuantity);
                        holder.edtProductQuantityInCart.setText(String.valueOf(newQuantity)); // Cập nhật số lượng
                        DatabaseReference sizeRef = createRef(product);
                        sizeRef.child("quantity").setValue(newQuantity); // Cập nhật số lượng lên DB
                        holder.txtProductToMoneyInCart.setText(formatPrice(newQuantity * Double.parseDouble(product.getPrice().toString()))); // Cập nhật giá
                    }
                    updateTotalPrice();
                }
            });
        });


        holder.imgVDeleteProductFromCart.setOnClickListener(view -> {
            Log.d("delete", "asdfg");
            DatabaseReference sizeRef = createRef(product);
            int newQuantity = product.getQuantity() - 1;
            if (newQuantity == 0){
                productList.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, productList.size());
                sizeRef.removeValue();
            } else {
                product.setQuantity(newQuantity);
                holder.edtProductQuantityInCart.setText(String.valueOf(newQuantity));
                sizeRef.child("quantity").setValue(newQuantity);
                holder.txtProductToMoneyInCart.setText(formatPrice(newQuantity * Double.parseDouble(product.getPrice().toString()))); // Cập nhật giá
            }
            updateTotalPrice();
        });
        quantitybefore = Integer.parseInt(String.valueOf(product.getQuantity()));
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Không có hành động nào cần thực hiện
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String quantityText = s.toString().trim();
                if (!quantityText.isEmpty()) {
                    try {
                        int enteredQuantity = Integer.parseInt(quantityText);
                        getQuantityInventory(product, new QuantityCallback() {
                            @Override
                            public void onQuantityRetrieved(int quantityInventory) {
                                if (enteredQuantity > quantityInventory) {
//                                    Toast.makeText(context, "Số lượng vượt quá tồn kho", Toast.LENGTH_SHORT).show();
                                    holder.edtProductQuantityInCart.setText(String.valueOf(quantitybefore));
                                    holder.edtProductQuantityInCart.requestFocus();
                                    holder.edtProductQuantityInCart.setSelection(holder.edtProductQuantityInCart.getText().length());
                                } else {
                                    product.setQuantity(enteredQuantity);
                                    DatabaseReference sizeRef = createRef(product);
                                    sizeRef.child("quantity").setValue(enteredQuantity);
                                }
                            }
                        });
                    } catch (NumberFormatException e) {
                        Log.e("QuantityError", "Giá trị nhập vào không hợp lệ: " + quantityText);
                    }
                } else {
                    if (!holder.edtProductQuantityInCart.hasFocus()) {
                        holder.edtProductQuantityInCart.setText(String.valueOf(product.getQuantity()));
                        holder.edtProductQuantityInCart.setSelection(holder.edtProductQuantityInCart.getText().length());
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Không có hành động nào cần thực hiện
            }
        };
        holder.bindTextWatcher(textWatcher);
    }


    @Override
    public int getItemCount() {
        return productList.size();
    }

    static class ProductViewHolder extends RecyclerView.ViewHolder {

        CheckBox ckProductInCart;
        ImageView imgProductInCart, imgVTrashFromCart, imgVDeleteProductFromCart, imgVAddProductToCart;
        TextView txtProductNameInCart, txtProductDescriptionInCart, txtProductToMoneyInCart, txtBrandNameInCart;
        EditText edtProductQuantityInCart;
        TextWatcher textWatcher;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            ckProductInCart = itemView.findViewById(R.id.ckProductInCart);
            imgProductInCart = itemView.findViewById(R.id.imgProductInCart);
            imgVTrashFromCart = itemView.findViewById(R.id.imgVTrashFromCart);
            imgVDeleteProductFromCart = itemView.findViewById(R.id.imgVDeleteProductFromCart);
            imgVAddProductToCart = itemView.findViewById(R.id.imgVAddProductToCart);
            txtProductNameInCart = itemView.findViewById(R.id.txtProductNameInCart);
            txtProductDescriptionInCart = itemView.findViewById(R.id.txtProductDescriptionInCart);
            edtProductQuantityInCart = itemView.findViewById(R.id.edtProductQuantityInCart);
            txtProductToMoneyInCart = itemView.findViewById(R.id.txtProductToMoneyInCart);
            txtBrandNameInCart = itemView.findViewById(R.id.txtBrandNameInCart);
        }
        public void bindTextWatcher(TextWatcher watcher) {
            if (textWatcher != null) {
                edtProductQuantityInCart.removeTextChangedListener(textWatcher);
            }
            textWatcher = watcher;
            edtProductQuantityInCart.addTextChangedListener(textWatcher);
        }
    }

    public String formatPrice(double price) {
        NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.US);
        return "₫" + numberFormat.format(price);
    }

    public DatabaseReference createRef(CartProduct product){
        DatabaseReference database = FirebaseDatabase.getInstance().getReference("carts");
        DatabaseReference cartRef = database.child(userId).child("items").child(product.getId());
        DatabaseReference colorRef = cartRef.child("colors").child(product.getColorName());
        DatabaseReference sizeRef = colorRef.child("sizes").child(product.getSizeName());
        return sizeRef;
    }

    public void getQuantityInventory(CartProduct product, QuantityCallback callback) {
        DatabaseReference productRef = FirebaseDatabase.getInstance().getReference("products")
                .child(product.getId())
                .child("colors")
                .child(product.getColorName())
                .child("sizes")
                .child(product.getSizeName())
                .child("quantity");

        productRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    int quantityInventory = dataSnapshot.getValue(Integer.class);
                    callback.onQuantityRetrieved(quantityInventory); // Gọi callback với giá trị lấy được
                } else {
                    Log.d("FirebaseValue", "Dữ liệu không tồn tại");
                    callback.onQuantityRetrieved(0); // Gọi callback với giá trị 0 nếu không có dữ liệu
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("FirebaseError", "Lỗi: " + databaseError.getMessage());
                callback.onQuantityRetrieved(0); // Gọi callback với giá trị 0 nếu có lỗi
            }
        });
    }

    // Callback interface để xử lý giá trị nhận được
    public interface QuantityCallback {
        void onQuantityRetrieved(int quantity);
    }


    private void updateTotalPrice() {
        double total = 0;
        for (CartProduct product : productList) {
            if (product.isSelected()) {
                total += Double.parseDouble(product.getPrice().toString()) * product.getQuantity();
            }
            Log.d("total", total+"");
        }
        txtTotalPrice.setText(formatPrice(total)); // Cập nhật TextView tổng tiền
    }
}

