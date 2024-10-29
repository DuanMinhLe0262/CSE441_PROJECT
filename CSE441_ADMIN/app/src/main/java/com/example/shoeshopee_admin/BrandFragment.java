package com.example.shoeshopee_admin;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.shoeshopee_admin.Adapter.BrandAdapter;
import com.example.shoeshopee_admin.AddBrandActivity;
import com.example.shoeshopee_admin.Model.Brand;
import com.example.shoeshopee_admin.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class BrandFragment extends Fragment {

    private RecyclerView recyclerView;
    private BrandAdapter adapter;
    private ArrayList<Brand> brandList;
    private ArrayList<Brand> brandList1;
    private List<Brand> filteredList;
    private DatabaseReference mDatabase;
    private FloatingActionButton fabAddBrand;
    private EditText editTextSearch;
    private ImageView imageViewSearch;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_brand, container, false);

        recyclerView = view.findViewById(R.id.recyclerViewBrands);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        editTextSearch = view.findViewById(R.id.editTextSearch);


        imageViewSearch = view.findViewById(R.id.imageViewSearch);
        fabAddBrand = view.findViewById(R.id.fabAddBrand);
        fabAddBrand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AddBrandActivity.class);
                startActivity(intent);
            }
        });

        mDatabase = FirebaseDatabase.getInstance().getReference("brands");

        brandList = new ArrayList<>();
        filteredList = new ArrayList<>();
        brandList1 = new ArrayList<>();

        adapter = new BrandAdapter(brandList, getActivity());
        recyclerView.setAdapter(adapter);

        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                brandList.clear();
                for (DataSnapshot brandSnapshot : dataSnapshot.getChildren()) {
                    Brand brand = brandSnapshot.getValue(Brand.class);
                    if (brand != null) {
                        brandList.add(brand);
                        brandList1.add(brand);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w("BrandFragment", "loadPost:onCancelled", databaseError.toException());
            }
        });



        editTextSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String query = s.toString().trim();
                if (TextUtils.isEmpty(query)) {
                    updateRecyclerView(brandList1);
                } else {
                    searchBrands(query);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        return view;
    }

    private void searchBrands(String query) {
        filteredList.clear();
        for (Brand brand : brandList1) {
            if (brand.getName().toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(brand);
            }
        }

        if (filteredList.isEmpty()) {
            Toast.makeText(getContext(), "Không tìm thấy thương hiệu", Toast.LENGTH_SHORT).show();
        }
        updateRecyclerView(filteredList);
    }

    private void updateRecyclerView(List<Brand> newList) {
        if(newList == null){
            Log.d("BrandFragment", "newList is null");
        }else {
            adapter.updateData(newList);
            adapter.notifyDataSetChanged();
        }
    }
}
