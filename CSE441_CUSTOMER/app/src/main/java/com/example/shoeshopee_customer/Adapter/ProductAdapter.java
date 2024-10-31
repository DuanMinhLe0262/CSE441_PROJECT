package com.example.shoeshopee_customer.Adapter;

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
import com.example.shoeshopee_customer.Model.Color;
import com.example.shoeshopee_customer.Model.Product;
import com.example.shoeshopee_customer.ProductDetailActivity;
import com.example.shoeshopee_customer.R;

import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    private Context context;
    private List<Product> productList;
    private String userId;

    public ProductAdapter(Context context, List<Product> productList, String userId) {
        this.productList = productList;
        this.context = context;
        this.userId = userId;
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

            // Lấy màu đầu tiên từ Map
            Color firstColor = product.getColors().values().iterator().next();

            // Đặt tên màu và giá
            //holder.colorName.setText(firstColor.getColorName());
            holder.productPrice.setText(String.format("₫%.2f", firstColor.getPrice()));

            // Tải hình ảnh đầu tiên của màu
            if (firstColor.getImages() != null && !firstColor.getImages().isEmpty()) {
                Log.d("ProductAdapter", "Image URL: " + firstColor.getImages().get(0));
                Glide.with(context)
                        .load(firstColor.getImages().get(0)) // Tải hình ảnh đầu tiên từ danh sách
                        .into(holder.productImage);
            } else {
                // Nếu không có hình ảnh, sử dụng hình ảnh mặc định
                holder.productImage.setImageResource(R.drawable.load);
            }
        } else {
            // Xử lý trường hợp không có màu
            //holder.colorName.setText("No color available");
            // holder.colorPrice.setText("$0.00"); // Hoặc một giá trị mặc định
            holder.productImage.setImageResource(R.drawable.load); // Hình ảnh placeholder
        }
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ProductDetailActivity.class);
            intent.putExtra("productId", product.getId());
            intent.putExtra("userId", userId);
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
            productImage = itemView.findViewById(R.id.imgProduct);
            productPrice = itemView.findViewById(R.id.txtPrice);
        }
    }
}