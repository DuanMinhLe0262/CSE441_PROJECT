package com.example.shoeshopee_customer.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.shoeshopee_customer.R;

import java.util.List;

public class SizeAdapter extends RecyclerView.Adapter<SizeAdapter.SizeViewHolder> {
    private List<String> sizeList; // Thay đổi kiểu dữ liệu thành List<String>
    private Context context;
    private OnSizeClickListener sizeClickListener;
    private int selectedPosition = RecyclerView.NO_POSITION; // Biến để lưu vị trí màu đã chọn

    // Constructor
    public SizeAdapter(Context context, List<String> sizeList, OnSizeClickListener listener) {
        this.context = context;
        this.sizeList = sizeList;
        this.sizeClickListener = listener;
    }

    @NonNull
    @Override
    public SizeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_chip_bottomsheet, parent, false);
        return new SizeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SizeViewHolder holder, int position) {
        String sizeName = sizeList.get(position); // Lấy kích thước từ danh sách
        holder.textView.setText(sizeName);
        if (position == selectedPosition) {
            holder.textView.setBackground(context.getResources().getDrawable(R.drawable.chip_custom_selected));
        } else {
            holder.textView.setBackground(context.getResources().getDrawable(R.drawable.chip_custome));
        }

        // Thiết lập sự kiện nhấp chuột
        holder.itemView.setOnClickListener(v -> {
            int currentPosition = holder.getAdapterPosition();
            if (currentPosition != RecyclerView.NO_POSITION) {
                if (sizeClickListener != null) {
                    // Cập nhật selectedPosition
                    int previousPosition = selectedPosition; // Lưu vị trí trước đó
                    selectedPosition = currentPosition;

                    // Gọi phương thức onColorClick
                    sizeClickListener.onSizeClick(sizeName);

                    // Thông báo cho Adapter để cập nhật giao diện
                    notifyItemChanged(previousPosition); // Cập nhật vị trí trước đó
                    notifyItemChanged(selectedPosition); // Cập nhật vị trí mới
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return sizeList.size();
    }

    // Interface để xử lý sự kiện nhấp chuột
    public interface OnSizeClickListener {
        void onSizeClick(String size); // Thay đổi tham số thành String
    }

    static class SizeViewHolder extends RecyclerView.ViewHolder {
        TextView textView;

        public SizeViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.txtChip);
        }
    }
}
