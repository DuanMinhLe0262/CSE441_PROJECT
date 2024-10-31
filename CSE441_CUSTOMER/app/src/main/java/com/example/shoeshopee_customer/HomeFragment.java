package com.example.shoeshopee_customer;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.example.shoeshopee_customer.Adapter.ProductAdapter;
import com.example.shoeshopee_customer.Adapter.SliderAdapter;
import com.example.shoeshopee_customer.Model.Product;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tbuonomo.viewpagerdotsindicator.WormDotsIndicator;

import java.util.ArrayList;
import java.util.List;


public class HomeFragment extends Fragment {
    private static final String ARG_USER_ID = "user_id";
    private ViewPager2 viewPager;
    private SliderAdapter sliderAdapter;
    private List<Integer> sliderList;
    private WormDotsIndicator wormDotsIndicator;

    private RecyclerView recyclerView;
    private ProductAdapter productAdapter;
    private List<Product> productList;
    private DatabaseReference databaseReference;
    String userId = "";

    public static HomeFragment newInstance(String userId) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_USER_ID, userId); // Add user ID to arguments
        Log.d("idhome", userId +"");
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        if (getArguments() != null) {
            userId = getArguments().getString(ARG_USER_ID);
            // Use the userId as needed
        }

        recyclerView = view.findViewById(R.id.rcv_home_fragment);
        int numberOfColumns = 2;
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), numberOfColumns);
        recyclerView.setLayoutManager(gridLayoutManager);

        productList = new ArrayList<>();
        productAdapter = new ProductAdapter(getContext(), productList, userId);
        recyclerView.setAdapter(productAdapter);

        databaseReference = FirebaseDatabase.getInstance().getReference("products");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                productList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Product product = dataSnapshot.getValue(Product.class);
                    productList.add(product);
                }
                productAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Failed to load data.", Toast.LENGTH_SHORT).show();
            }
        });

        sliderList = new ArrayList<>();
        sliderList.add(R.drawable.banner1);
        sliderList.add(R.drawable.banner2);

        wormDotsIndicator = view.findViewById(R.id.worm_dots_indicator);
        viewPager = view.findViewById(R.id.viewpagerSlider);

        sliderAdapter = new SliderAdapter(sliderList);
        viewPager.setAdapter(sliderAdapter);

        wormDotsIndicator.attachTo(viewPager);

        return view;
    }
}
