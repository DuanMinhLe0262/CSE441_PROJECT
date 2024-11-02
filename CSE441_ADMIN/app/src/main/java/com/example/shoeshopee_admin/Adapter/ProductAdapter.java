package com.example.shoeshopee_admin.Adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.shoeshopee_admin.Model.Color;
import com.example.shoeshopee_admin.Model.Product;
import com.example.shoeshopee_admin.ProductDetailActivity;
import com.example.shoeshopee_admin.R;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    private Context context;
    private List<Product> productList;

    public ProductAdapter(List<Product> productList, Context context) {
        this.productList = productList;
        this.context = context;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_product, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = productList.get(position);
        holder.productName.setText(product.getName());

        // Kiểm tra nếu sản phẩm có màu
        if (product.getColors() != null && !product.getColors().isEmpty()) {
            Log.d("ProductAdapter", "Colors: " + product.getColors());
            // Lấy màu đầu tiên từ Map
            Color firstColor = product.getColors().values().iterator().next();
            holder.productPrice.setText(formatPrice(firstColor.getPrice()));

            // Tải hình ảnh đầu tiên của màu
            if (firstColor.getImages() != null && !firstColor.getImages().isEmpty()) {
                Log.d("ProductAdapter", "Image URL: " + firstColor.getImages().get(0));
                Glide.with(context)
                        .load(firstColor.getImages().get(0)) // Tải hình ảnh đầu tiên từ danh sách
                        .into(holder.productImage);
            } else {
                // Nếu không có hình ảnh, sử dụng hình ảnh mặc định
                holder.productImage.setImageResource(R.drawable.error_image);
            }
        } else {
            // Xử lý trường hợp không có màu
            holder.productImage.setImageResource(R.drawable.error_image); // Hình ảnh placeholder
        }

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ProductDetailActivity.class);
            intent.putExtra("PRODUCT_ID", product.getId()); // Truyền ID sản phẩm cho activity chi tiết
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public void updateData(List<Product> newList) {
        productList.clear();
        productList.addAll(newList);
        notifyDataSetChanged();
    }

    public static class ProductViewHolder extends RecyclerView.ViewHolder {
        TextView productName, productPrice;
        ImageView productImage;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            productName = itemView.findViewById(R.id.txtProductName);
            productPrice = itemView.findViewById(R.id.txtPrice);
            productImage = itemView.findViewById(R.id.imgProduct);
        }
    }

    public String formatPrice(double price) {
        NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.US);
        return "₫" + numberFormat.format(price);
    }
}
