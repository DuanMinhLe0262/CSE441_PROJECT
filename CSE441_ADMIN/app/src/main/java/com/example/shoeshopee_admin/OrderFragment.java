package com.example.shoeshopee_admin;

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
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.shoeshopee_admin.Adapter.OrderAdapter;
import com.example.shoeshopee_admin.Model.Order;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class OrderFragment extends Fragment {

    private RecyclerView orderRecyclerView;
    private OrderAdapter orderAdapter;
    private List<Order> orderList;
    private List<Order> orderListTmp;
    Spinner spinner;
    ArrayAdapter<String> adapter;
    EditText searchEditText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_order, container, false);
        orderRecyclerView = view.findViewById(R.id.orderRecyclerView);
        spinner = view.findViewById(R.id.filterSpinner);
        searchEditText = view.findViewById(R.id.searchEditText);
        orderRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        orderList = new ArrayList<>();
        orderListTmp = new ArrayList<>();
        Collections.reverse(orderList);
        orderAdapter = new OrderAdapter(orderList, getContext());
        orderRecyclerView.setAdapter(orderAdapter);
        String[] items = new String[]{"Tất cả", "Chờ xác nhận", "Đang giao hàng", "Đã giao hàng", "Đã hủy"};
        adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, items);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position){
                    case 0:
                        searchEditText.setText("");
                        fetchOrdersByStatus(items[0]);
                        break;
                    case 1:
                        searchEditText.setText("");
                        fetchOrdersByStatus(items[1]);
                        break;
                    case 2:
                        searchEditText.setText("");
                        fetchOrdersByStatus(items[2]);
                        break;
                    case 3:
                        searchEditText.setText("");
                        fetchOrdersByStatus(items[3]);
                        break;
                    case 4:
                        searchEditText.setText("");
                        fetchOrdersByStatus(items[4]);
                        break;
                }
                if (position == 0) {
                } else {
                    // Xử lý khi người dùng chọn các item khác
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String query = s.toString().trim();
                if (TextUtils.isEmpty(query)) {
                    updateRecyclerView(orderListTmp);
                } else {
                    searchProducts(query);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        return view;
    }

    private void fetchOrdersByStatus(String status) {
        DatabaseReference ordersRef = FirebaseDatabase.getInstance().getReference("orders");
        ordersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                orderList.clear();
                orderListTmp.clear();
                for (DataSnapshot orderSnapshot : dataSnapshot.getChildren()) {
                    Log.d("rrr1", orderSnapshot.toString() + "");
                    Order order = orderSnapshot.getValue(Order.class);
                    if(order != null){
                        order.setId(orderSnapshot.getKey());
                        if (status.equals("Tất cả")) {
                            orderList.add(order);
                            orderListTmp.add(order);

                        }
                        else if(status.equals(order.getStatus())) {
                            orderList.add(order);
                            orderListTmp.add(order);
                        }
                    }
                }
                orderAdapter.notifyDataSetChanged();
                Collections.reverse(orderList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle error if needed
            }
        });
    }

    private void searchProducts(String query) {
        List<Order> filteredList = new ArrayList<>();
        for (Order order : orderListTmp) {
            if (order.getName().toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(order);
            }
        }

        if (filteredList.isEmpty()) {
            Toast.makeText(getContext(), "Không tìm thấy đơn hàng", Toast.LENGTH_SHORT).show();
        }
        updateRecyclerView(filteredList);
    }

    private void updateRecyclerView(List<Order> newList) {
        updateData(newList);
        orderAdapter.notifyDataSetChanged();
    }

    public void updateData(List<Order> newList) {
        orderList.clear();
        orderList.addAll(newList);
        Collections.reverse(orderList);
        orderAdapter.notifyDataSetChanged();
    }
}
