package com.example.shoeshopee_customer.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.shoeshopee_customer.Model.Color;
import com.example.shoeshopee_customer.R;

import java.util.List;

public class ColorAdapter extends RecyclerView.Adapter<ColorAdapter.ColorViewHolder> {
    private List<Color> colorList; // Thay đổi kiểu dữ liệu thành List<Color>
    private Context context;
    private OnColorClickListener colorClickListener;
    private int selectedPosition = RecyclerView.NO_POSITION;

    // Constructor
    public ColorAdapter(Context context, List<Color> colorList, OnColorClickListener listener) {
        this.context = context;
        this.colorList = colorList;
        this.colorClickListener = listener;
    }

    @NonNull
    @Override
    public ColorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_chip_bottomsheet, parent, false);
        return new ColorViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ColorViewHolder holder, int position) {
        Color color = colorList.get(position); // Lấy đối tượng Color từ danh sách
        holder.textView.setText(color.getColorName());

        // Kiểm tra xem màu này có được chọn không và thay đổi giao diện
        if (position == selectedPosition) {
            holder.textView.setBackground(context.getResources().getDrawable(R.drawable.chip_custom_selected));
        } else {
            holder.textView.setBackground(context.getResources().getDrawable(R.drawable.chip_custome));
        }

        // Thiết lập sự kiện nhấp chuột
        holder.itemView.setOnClickListener(v -> {
            int currentPosition = holder.getAdapterPosition();
            if (currentPosition != RecyclerView.NO_POSITION) {
                if (colorClickListener != null) {
                    // Cập nhật selectedPosition
                    int previousPosition = selectedPosition; // Lưu vị trí trước đó
                    selectedPosition = currentPosition;

                    // Gọi phương thức onColorClick
                    colorClickListener.onColorClick(color);

                    // Thông báo cho Adapter để cập nhật giao diện
                    notifyItemChanged(previousPosition); // Cập nhật vị trí trước đó
                    notifyItemChanged(selectedPosition); // Cập nhật vị trí mới
                }
            }
        });
    }


    @Override
    public int getItemCount() {
        return colorList.size();
    }

    // Interface để xử lý sự kiện nhấp chuột
    public interface OnColorClickListener {
        void onColorClick(Color color);
    }

    static class ColorViewHolder extends RecyclerView.ViewHolder {
        TextView textView;

        public ColorViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.txtChip);
        }
    }
}
