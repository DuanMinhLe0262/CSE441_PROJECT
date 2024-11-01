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

public class ProductOptionAdapter extends RecyclerView.Adapter<ProductOptionAdapter.ProductOptionViewHolder> {

    private Context context;
    private List<CartProduct> productOptions;

    public ProductOptionAdapter(Context context, List<CartProduct> productOptions) {
        this.context = context;
        this.productOptions = productOptions;
    }

    public static class ProductOptionViewHolder extends RecyclerView.ViewHolder {
        TextView txtBrandName, txtProductName, txtDescription, txtPrice, txtQuantity;
        ImageView imgProduct;

        public ProductOptionViewHolder(View itemView) {
            super(itemView);
            txtBrandName = itemView.findViewById(R.id.txtBrandNameInOrderTracking);
            txtProductName = itemView.findViewById(R.id.txtProductNameInOrderTracking);
            txtDescription = itemView.findViewById(R.id.txtProductDescriptionInOrderTracking);
            txtPrice = itemView.findViewById(R.id.txtProductPriceInOrderTracking);
            txtQuantity = itemView.findViewById(R.id.txtProductQuantityInOrderTracking);
            imgProduct = itemView.findViewById(R.id.imgProductInOrderTracking);
        }
    }

    @NonNull
    @Override
    public ProductOptionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_option_ordertracking, parent, false);
        return new ProductOptionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductOptionViewHolder holder, int position) {
        CartProduct orderProduct = productOptions.get(position);
        holder.txtBrandName.setText(orderProduct.getBrandName());
        holder.txtProductName.setText(orderProduct.getName());
        holder.txtDescription.setText(orderProduct.getColorName() + ", " + orderProduct.getSizeName());
        holder.txtPrice.setText(orderProduct.getPrice() + "");
        holder.txtQuantity.setText("x" + orderProduct.getQuantity());
        // Đặt ảnh cho imgProduct nếu cần thiết
        Glide.with(context)
                .load(orderProduct.getImage())
                .placeholder(R.drawable.load)
                .into(holder.imgProduct);
    }

    @Override
    public int getItemCount() {
        return productOptions.size();
    }
}

