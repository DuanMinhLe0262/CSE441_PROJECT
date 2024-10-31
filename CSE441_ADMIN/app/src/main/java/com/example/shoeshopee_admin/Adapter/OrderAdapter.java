package com.example.shoeshopee_admin.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.shoeshopee_admin.Model.Order;
import com.example.shoeshopee_admin.OrderDetailActivity;
import com.example.shoeshopee_admin.ProductDetailActivity;
import com.example.shoeshopee_admin.R;

import java.util.List;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {

    private List<Order> orderList;
    private Context context;

    public OrderAdapter(List<Order> orderList, Context context) {
        this.orderList = orderList;
        this.context = context;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_order, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Order order = orderList.get(position);
        holder.orderIdTextView.setText("Order ID: " + order.getId());
        holder.customerNameTextView.setText("Customer: " + order.getCustomerName());
        holder.totalAmountTextView.setText("Total: " + order.getTotalAmount());
        holder.statusTextView.setText("Status: " + order.getStatus());
        holder.timeTextView.setText("Time: " + order.getTime());

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, OrderDetailActivity.class);
            intent.putExtra("ORDER_ID",order.getId()); // Truyền ID sản phẩm cho activity chi tiết
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    public static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView orderIdTextView;
        TextView customerNameTextView;
        TextView totalAmountTextView;
        TextView statusTextView;
        TextView timeTextView;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            orderIdTextView = itemView.findViewById(R.id.orderIdTextView);
            customerNameTextView = itemView.findViewById(R.id.customerNameTextView);
            totalAmountTextView = itemView.findViewById(R.id.totalAmountTextView);
            statusTextView = itemView.findViewById(R.id.statusTextView);
            timeTextView = itemView.findViewById(R.id.timeTextView);
        }
    }

    public void updateData(List<Order> newOrderList) {
        this.orderList.clear();
        this.orderList.addAll(newOrderList);
        notifyDataSetChanged();
    }
}
