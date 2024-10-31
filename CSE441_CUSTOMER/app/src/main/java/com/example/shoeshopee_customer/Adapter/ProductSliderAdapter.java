package com.example.shoeshopee_customer.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.shoeshopee_customer.R;

import java.util.List;

public class ProductSliderAdapter extends RecyclerView.Adapter<ProductSliderAdapter.SliderViewHolder> {

    private List<String> bannerList;
    private Context context;

    public ProductSliderAdapter(Context context, List<String> bannerList) {
        this.context = context;
        this.bannerList = bannerList;
    }

    @NonNull
    @Override
    public SliderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.banner_item, parent, false);
        return new SliderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SliderViewHolder holder, int position) {
        String bannerImageUrl = bannerList.get(position);
        Glide.with(context)
                .load(bannerImageUrl)
                .into(holder.bannerImageView);
    }

    @Override
    public int getItemCount() {
        return bannerList.size();
    }

    public static class SliderViewHolder extends RecyclerView.ViewHolder {
        ImageView bannerImageView;

        public SliderViewHolder(@NonNull View itemView) {
            super(itemView);
            bannerImageView = itemView.findViewById(R.id.bannerImage);
        }
    }
}
