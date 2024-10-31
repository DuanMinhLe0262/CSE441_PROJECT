package com.example.shoeshopee_admin.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.shoeshopee_admin.Model.CartProduct;
import com.example.shoeshopee_admin.R;

import java.util.List;

public class CartProductAdapter extends RecyclerView.Adapter<CartProductAdapter.ViewHolder> {

    private final List<CartProduct> cartProducts;

    public CartProductAdapter(List<CartProduct> cartProducts) {
        this.cartProducts = cartProducts;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product_order, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CartProduct product = cartProducts.get(position);

        // Thiết lập thông tin sản phẩm
        holder.txtBrandNameInPayment.setText(product.getBrandName());
        holder.txtProductNameInPayment.setText(product.getName());
        holder.txtProductDescriptionInPayment.setText(String.format("%s, %s", product.getColorName(), product.getSizeName()));
        holder.txtProductPriceInPayment.setText(product.getPrice()+"");
        holder.txtProductQuantityInPayment.setText(String.format("x%d", product.getQuantity()));

        // Tải hình ảnh bằng Glide
        Glide.with(holder.itemView.getContext())
                .load(product.getImage())
                .placeholder(R.drawable.error_image) // Hình ảnh placeholder
                .error(R.drawable.error_image) // Hình ảnh hiển thị khi có lỗi
                .into(holder.imgProductInPayment);
    }

    @Override
    public int getItemCount() {
        return cartProducts.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtBrandNameInPayment;
        TextView txtProductNameInPayment;
        TextView txtProductDescriptionInPayment;
        TextView txtProductPriceInPayment;
        TextView txtProductQuantityInPayment;
        ImageView imgProductInPayment;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtBrandNameInPayment = itemView.findViewById(R.id.txtBrandNameInPayment);
            txtProductNameInPayment = itemView.findViewById(R.id.txtProductNameInPayment);
            txtProductDescriptionInPayment = itemView.findViewById(R.id.txtProductDescriptionInPayment);
            txtProductPriceInPayment = itemView.findViewById(R.id.txtProductPriceInPayment);
            txtProductQuantityInPayment = itemView.findViewById(R.id.txtProductQuantityInPayment);
            imgProductInPayment = itemView.findViewById(R.id.imgProductInPayment);
        }
    }
}
