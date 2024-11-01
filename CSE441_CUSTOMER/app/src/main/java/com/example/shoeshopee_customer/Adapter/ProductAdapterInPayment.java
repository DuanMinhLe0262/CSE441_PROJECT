package com.example.shoeshopee_customer.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.shoeshopee_customer.Model.CartProduct;
import com.example.shoeshopee_customer.R;

import java.util.List;

public class ProductAdapterInPayment extends RecyclerView.Adapter<ProductAdapterInPayment.ProductViewHolder> {

    private final Context context;
    private final List<CartProduct> productList;

    public ProductAdapterInPayment(Context context, List<CartProduct> productList) {
        this.context = context;
        this.productList = productList;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_product_payment, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        CartProduct product = productList.get(position);
        // Gán dữ liệu cho các TextView và ImageView từ product
        holder.txtBrandName.setText(product.getBrandName());
        holder.txtProductName.setText(product.getName());
        holder.txtDescription.setText(product.getColorName() + ", " + product.getSizeName());
        holder.txtPrice.setText(String.valueOf(product.getPrice()));
        holder.txtQuantity.setText("x" + product.getQuantity());
        holder.txtNoteQuantity.setText("(" + product.getQuantity() + " sản phẩm)");
        holder.txtTotalAmount.setText(String.valueOf(product.getPrice() * product.getQuantity()));
        Glide.with(context)
                .load(product.getImage())
                .placeholder(R.drawable.load)
                .into(holder.imgProduct);
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public static class ProductViewHolder extends RecyclerView.ViewHolder {

        ImageView imgProduct;
        TextView txtBrandName, txtProductName, txtDescription, txtPrice, txtQuantity, txtNoteQuantity, txtTotalAmount;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            imgProduct = itemView.findViewById(R.id.imgProductInPayment);
            txtBrandName = itemView.findViewById(R.id.txtBrandNameInPayment);
            txtProductName = itemView.findViewById(R.id.txtProductNameInPayment);
            txtDescription = itemView.findViewById(R.id.txtProductDescriptionInPayment);
            txtPrice = itemView.findViewById(R.id.txtProductPriceInPayment);
            txtQuantity = itemView.findViewById(R.id.txtProductQuantityInPayment);
            txtNoteQuantity = itemView.findViewById(R.id.txtNoteProductQuantityInPayment);
            txtTotalAmount = itemView.findViewById(R.id.txtTotalAmountInPayment);
        }
    }
}

