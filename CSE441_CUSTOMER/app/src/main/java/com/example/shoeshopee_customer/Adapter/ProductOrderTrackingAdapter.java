package com.example.shoeshopee_customer.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.shoeshopee_customer.Model.CartProduct;
import com.example.shoeshopee_customer.Model.Order;
import com.example.shoeshopee_customer.R;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ProductOrderTrackingAdapter extends RecyclerView.Adapter<ProductOrderTrackingAdapter.ProductViewHolder> {

    private Context context;
    private List<Order> orderList;
    private String status;

    public ProductOrderTrackingAdapter(Context context, List<Order> orderList, String status) {
        this.context = context;
        this.orderList = orderList;
        this.status = status;
    }

    public static class ProductViewHolder extends RecyclerView.ViewHolder {
        TextView txtStatus, txtTotalAmount, txtNoteProductQuantity;
        RecyclerView recyclerViewOptions;
        Button btnFeedback;

        public ProductViewHolder(View itemView) {
            super(itemView);
            txtStatus = itemView.findViewById(R.id.txtProductStatusInOrderTracking);
            txtTotalAmount = itemView.findViewById(R.id.txtTotalAmountInOrderTracking);
            txtNoteProductQuantity = itemView.findViewById(R.id.txtNoteProductQuantityInOrderTracking);
            recyclerViewOptions = itemView.findViewById(R.id.optionRecyclerView);
            btnFeedback = itemView.findViewById(R.id.btnFeedback);
            btnFeedback.setVisibility(View.GONE);
        }
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product_ordertracking, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Order order = orderList.get(position);
        holder.txtStatus.setText(order.getStatus());
        holder.txtTotalAmount.setText(formatPrice(order.getTotalAmount()));
        holder.txtNoteProductQuantity.setText("Tổng số tiền (" + order.getProducts().size() + " sản phẩm): ");

        // Tạo và thiết lập adapter cho danh sách sản phẩm
        List<CartProduct> productList = new ArrayList<>();
        productList = order.getProducts();
        ProductOptionAdapter optionAdapter = new ProductOptionAdapter(context, productList);
        holder.recyclerViewOptions.setLayoutManager(new LinearLayoutManager(context));
        holder.recyclerViewOptions.setAdapter(optionAdapter);
//        optionAdapter.notifyDataSetChanged();
        if (status.equals("Đã giao hàng")){
            holder.btnFeedback.setVisibility(View.VISIBLE);
        }
        else holder.btnFeedback.setVisibility(View.GONE);
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    public String formatPrice(double price) {
        NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.US);
        return "₫" + numberFormat.format(price);
    }
}

