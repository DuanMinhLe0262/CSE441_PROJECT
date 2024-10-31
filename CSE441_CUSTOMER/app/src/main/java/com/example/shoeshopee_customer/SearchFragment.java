package com.example.shoeshopee_customer;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.shoeshopee_customer.Adapter.ProductAdapter;
import com.example.shoeshopee_customer.Adapter.SearchHistoryAdapter;
import com.example.shoeshopee_customer.Model.Product;
import com.example.shoeshopee_customer.Model.SearchHistory;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SearchFragment extends Fragment {
    private RecyclerView recyclerView;
    private ProductAdapter productAdapter;
    private List<Product> productList;
    private DatabaseReference databaseReference;
    private EditText editTextSearch;
    private ImageView searchBtn;
    private TextView txtHistory, txtSuggested;
    private ListView lvSearchResult, lvSearchHistory;
    private List<String> searchHistoryList, searchResultList;
    private ArrayAdapter<String> searchResultAdapter, searchHistoryAdapter;
    private static final String ARG_USER_ID = "user_id";
    String userId = "";

    public static SearchFragment newInstance(String userId) {

        SearchFragment fragment = new SearchFragment();
        Bundle args = new Bundle();
        args.putString(ARG_USER_ID, userId);
        Log.d("searchid", userId +"");
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (getArguments() != null) {
            userId = getArguments().getString(ARG_USER_ID);
        }
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        recyclerView = view.findViewById(R.id.rcv_suggested_products);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));

        productList = new ArrayList<>();
        productAdapter = new ProductAdapter(getContext(), productList, userId);
        recyclerView.setAdapter(productAdapter);

        databaseReference = FirebaseDatabase.getInstance().getReference("products");
        loadProducts();
        loadSearchHistory();
//        loadSearchHistory();

        lvSearchResult = view.findViewById(R.id.lv_search_result);
        lvSearchHistory = view.findViewById(R.id.lv_search_history);

        txtHistory = view.findViewById(R.id.txtHistory);
        txtSuggested = view.findViewById(R.id.txtSuggested);

        searchHistoryList = new ArrayList<>();
        searchHistoryAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, searchHistoryList);
        lvSearchHistory.setAdapter(searchHistoryAdapter);

        searchResultList = new ArrayList<>();
        searchResultAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, searchResultList);
        lvSearchResult.setAdapter(searchResultAdapter);
        lvSearchResult.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                addSearchHistory(searchResultList.get(position));
                Intent intent = new Intent(getContext(), SearchDetailActivity.class);
                intent.putExtra("query", searchResultList.get(position));
                getContext().startActivity(intent);
            }
        });

        editTextSearch = view.findViewById(R.id.edtSearch);
        searchBtn = view.findViewById(R.id.searchBtn);
        lvSearchResult.setVisibility(View.GONE);

        editTextSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Do nothing here
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String query = s.toString().trim();
                if (!TextUtils.isEmpty(query)) {
                    lvSearchResult.setVisibility(View.VISIBLE);
                    lvSearchHistory.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.GONE);
                    txtHistory.setVisibility(View.GONE);
                    txtSuggested.setVisibility(View.GONE);

                    searchResultList.clear();
                    filterSearchResults(query);
                } else {
                    lvSearchResult.setVisibility(View.GONE);
                    lvSearchHistory.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.VISIBLE);
                    txtHistory.setVisibility(View.VISIBLE);
                    txtSuggested.setVisibility(View.VISIBLE);

                    loadProducts();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });


        searchBtn.setOnClickListener(v -> {
            String query = editTextSearch.getText().toString().trim();
            if (!TextUtils.isEmpty(query)) {
                // Bắt đầu hoạt động tìm kiếm
                Intent intent = new Intent(getContext(), SearchDetailActivity.class);
                intent.putExtra("query", query);
                getContext().startActivity(intent);

                // Thêm lịch sử tìm kiếm vào Firebase
                addSearchHistory(query);
            } else {
                Toast.makeText(getContext(), "Please enter a search query", Toast.LENGTH_SHORT).show();
            }
        });
        return view;
    }

    private void addSearchHistory(String query) {
        DatabaseReference historyRef = FirebaseDatabase.getInstance().getReference("search_history").child("user_id");
        SearchHistory searchHistory = new SearchHistory();
        searchHistory.setQuery(query);

        historyRef.push().setValue(searchHistory).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(getContext(), "Search history added successfully", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "Failed to add search history", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void filterSearchResults(String query) {
        String lowerCaseQuery = query.toLowerCase();
        for (Product product : productList) {
            if (product.getName().toLowerCase().contains(lowerCaseQuery)) {
                searchResultList.add(product.getName());
            }
        }
    }

    private void loadProducts() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                productList.clear(); // Clear the existing product list
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Product product = dataSnapshot.getValue(Product.class);
                    productList.add(product);
                }
                productAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Failed to load products.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadSearchHistory() {
        DatabaseReference historyRef = FirebaseDatabase.getInstance().getReference("search_history").child("user_id"); // Thay thế bằng user ID thực tế.
        historyRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                searchHistoryList.clear();

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    try {
                        SearchHistory searchHistory = dataSnapshot.getValue(SearchHistory.class);
                        if (searchHistory != null) {
                            // Thêm vào cuối danh sách
                            searchHistoryList.add(searchHistory.getQuery());
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(getContext(), "Error processing search history: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }

                // Đảo ngược danh sách để hiển thị từ mới đến cũ
                Collections.reverse(searchHistoryList);
                searchHistoryAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Failed to load search history: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


}
