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
import com.example.shoeshopee_admin.BrandDetailActivity;
import com.example.shoeshopee_admin.Model.Brand;
import com.example.shoeshopee_admin.R;

import java.util.ArrayList;
import java.util.List;

public class BrandAdapter extends RecyclerView.Adapter<BrandAdapter.BrandViewHolder> {

    private Context context;
    private ArrayList<Brand> brandList;


    public BrandAdapter(ArrayList<Brand> brandList, Context context) {
        this.brandList = brandList;
        this.context = context;
    }

    public BrandAdapter(ArrayList<Brand> brandList) {
        this.brandList = brandList;
    }

    public BrandAdapter() {
    }

    @NonNull
    @Override
    public BrandViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_brand, parent, false);
        return new BrandViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BrandViewHolder holder, int position) {
        Brand brand = brandList.get(position);
        holder.textViewBrandName.setText(brand.getName());

        Glide.with(holder.imageViewBrand.getContext())
                .load(brand.getImageUrl())
                .centerCrop()
                .into(holder.imageViewBrand);


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, BrandDetailActivity.class);
                intent.putExtra("brandId", brand.getId());
                Log.d("loi", "brandId: " + brand.getId() + "");
                context.startActivity(intent);
            }
        });
    }


    @Override
    public int getItemCount() {
        return brandList.size();
    }

    public static class BrandViewHolder extends RecyclerView.ViewHolder {
        TextView textViewBrandName;
        ImageView imageViewBrand;

        public BrandViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewBrandName = itemView.findViewById(R.id.textViewBrandName);
            imageViewBrand = itemView.findViewById(R.id.imageViewBrand);
        }
    }

    public void updateData(List<Brand> newBrandList) {
        this.brandList.clear();
        this.brandList.addAll(newBrandList);
        notifyDataSetChanged();
    }

}
