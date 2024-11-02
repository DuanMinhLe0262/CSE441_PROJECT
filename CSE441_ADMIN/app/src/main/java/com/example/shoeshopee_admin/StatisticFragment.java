package com.example.shoeshopee_admin;

import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.shoeshopee_admin.Model.CartProduct;
import com.example.shoeshopee_admin.Model.Order;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class StatisticFragment extends Fragment {

    private BarChart monthlyBarChart;
    private BarChart productBarChart;
    private BarChart brandBarChart;

    private List<String> xMonths = new ArrayList<>(Arrays.asList("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"));

    private List<String> productNames = new ArrayList<>();
    private List<String> brandNames = new ArrayList<>();

    private float[] monthlyRevenue = new float[12];
    private float[] productRevenue; // Doanh thu theo sản phẩm
    private float[] brandRevenue; // Doanh thu theo thương hiệu

    public StatisticFragment() {
        // Required empty public constructor
    }

    public static StatisticFragment newInstance(String param1, String param2) {
        StatisticFragment fragment = new StatisticFragment();
        Bundle args = new Bundle();
        args.putString("param1", param1);
        args.putString("param2", param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            // Get parameters if needed
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_statistic, container, false);

        Spinner statisticsSpinner = view.findViewById(R.id.statisticsSpinner);
        monthlyBarChart = view.findViewById(R.id.monthlyChart);
        productBarChart = view.findViewById(R.id.productChart);
        brandBarChart = view.findViewById(R.id.brandChart);

        // Setup Spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.statistics_options, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        statisticsSpinner.setAdapter(adapter);

        fetchOrderData(new DataFetchListener() {
            @Override
            public void onDataFetched(float[] monthlyRevenue, float[] productRevenue, List<String> productNames, float[] brandRevenue, List<String> brandNames) {
                setupBarChart(monthlyBarChart, xMonths);
                setupBarChart(productBarChart, productNames);
                setupBarChart(brandBarChart, brandNames);
            }
        });

        Log.d("products", productNames.toString());
        Log.d("brands", brandNames.toString());

        statisticsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0: // Doanh thu theo tháng
                        monthlyBarChart.setVisibility(View.VISIBLE);
                        productBarChart.setVisibility(View.GONE);
                        brandBarChart.setVisibility(View.GONE);
                        updateChart(monthlyBarChart, monthlyRevenue, xMonths);
                        break;
                    case 1: // Doanh thu theo sản phẩm
                        monthlyBarChart.setVisibility(View.GONE);
                        productBarChart.setVisibility(View.VISIBLE);
                        brandBarChart.setVisibility(View.GONE);
                        updateChart(productBarChart, productRevenue, productNames);
                        break;
                    case 2: // Doanh thu theo thương hiệu
                        monthlyBarChart.setVisibility(View.GONE);
                        productBarChart.setVisibility(View.GONE);
                        brandBarChart.setVisibility(View.VISIBLE);
                        updateChart(brandBarChart, brandRevenue, brandNames);
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Không làm gì cả
            }
        });

        return view;
    }

    private void setupBarChart(BarChart barChart, List<String> xValues) {
        YAxis yAxis = barChart.getAxisLeft();
        yAxis.setAxisMaximum(30000000f); // Thay đổi giá trị tối đa tùy theo doanh thu cao nhất
        yAxis.setAxisLineWidth(2f);
        yAxis.setAxisLineColor(Color.BLACK);
        yAxis.setLabelCount(10, true);

        barChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(xValues));
        barChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        barChart.getXAxis().setGranularity(1f);
        barChart.getXAxis().setGranularityEnabled(true);
        barChart.getDescription().setEnabled(false);
    }

    private void fetchOrderData(DataFetchListener listener) {
        DatabaseReference ordersRef = FirebaseDatabase.getInstance().getReference("orders");

        ordersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Arrays.fill(monthlyRevenue, 0);

                // Initialize product and brand lists
                if (productNames == null) {
                    productNames = new ArrayList<>();
                }
                if (brandNames == null) {
                    brandNames = new ArrayList<>();
                }

                // Reset revenue arrays
                productRevenue = new float[productNames.size()];
                brandRevenue = new float[brandNames.size()];
                Arrays.fill(productRevenue, 0);
                Arrays.fill(brandRevenue, 0);

                // Check if there is data
                if (!dataSnapshot.exists()) {
                    return;
                }

                for (DataSnapshot orderSnapshot : dataSnapshot.getChildren()) {
                    Order order = new Order();

                    order.setId(orderSnapshot.getKey());
                    order.setTotal(orderSnapshot.child("total").getValue(Double.class));
                    order.setNote(orderSnapshot.child("note").getValue(String.class));
                    order.setAddress(orderSnapshot.child("address").getValue(String.class));
                    order.setPhone(orderSnapshot.child("phone").getValue(String.class));
                    order.setName(orderSnapshot.child("name").getValue(String.class));
                    order.setTime(orderSnapshot.child("time").getValue(String.class));
                    order.setStatus(orderSnapshot.child("status").getValue(String.class));

                    double orderTotal = 0;

                    for (DataSnapshot itemSnapshot : orderSnapshot.child("items").getChildren()) {
                        String productId = itemSnapshot.getKey();
                        String productName = itemSnapshot.child("name").getValue(String.class);
                        String brand = itemSnapshot.child("brand").getValue(String.class);

                        for (DataSnapshot colorSnapshot : itemSnapshot.child("colors").getChildren()) {
                            String colorName = colorSnapshot.getKey();
                            double price = colorSnapshot.child("price").getValue(Double.class);

                            for (DataSnapshot sizeSnapshot : colorSnapshot.child("sizes").getChildren()) {
                                int size = Integer.parseInt(sizeSnapshot.getKey());
                                int quantity = sizeSnapshot.child("quantity").getValue(Integer.class);

                                orderTotal += price * quantity;
                            }
                        }
                    }

                    order.setTotal(orderTotal);

                    if (order != null && order.getTime() != null && "Đã giao hàng".equals(order.getStatus())) {
                        String[] timeParts = order.getTime().split(" ");
                        if (timeParts.length >= 2) {
                            String[] dateParts = timeParts[1].split(":");
                            if (dateParts.length >= 3) {
                                int month = Integer.parseInt(dateParts[1]) - 1;
                                monthlyRevenue[month] += orderTotal;
                            }
                        }

                        // Revenue by product and brand
                        for (DataSnapshot itemSnapshot : orderSnapshot.child("items").getChildren()) {
                            String productName = itemSnapshot.child("name").getValue(String.class);
                            String brandName = itemSnapshot.child("brand").getValue(String.class);

                            // Product revenue
                            int productIndex = productNames.indexOf(productName);
                            if (productIndex == -1) {
                                productNames.add(productName);
                                productIndex = productNames.size() - 1;
                                productRevenue = Arrays.copyOf(productRevenue, productRevenue.length + 1);
                            }

                            double totalProductRevenue = 0;
                            for (DataSnapshot colorSnapshot : itemSnapshot.child("colors").getChildren()) {
                                for (DataSnapshot sizeSnapshot : colorSnapshot.child("sizes").getChildren()) {
                                    int quantity = sizeSnapshot.child("quantity").getValue(Integer.class);
                                    double price = colorSnapshot.child("price").getValue(Double.class);
                                    totalProductRevenue += price * quantity;
                                }
                            }
                            productRevenue[productIndex] += totalProductRevenue;

                            // Brand revenue
                            int brandIndex = brandNames.indexOf(brandName);
                            if (brandIndex == -1) {
                                brandNames.add(brandName);
                                Log.d("brandName", brandNames.toString());
                                brandIndex = brandNames.size() - 1;
                                brandRevenue = Arrays.copyOf(brandRevenue, brandRevenue.length + 1); // Expand if necessary
                            }
                            brandRevenue[brandIndex] += totalProductRevenue;


                        }
                        listener.onDataFetched(monthlyRevenue, productRevenue, productNames, brandRevenue, brandNames);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle error
            }
        });
    }


    private void updateChart(BarChart barChart, float[] revenue, List<String> xValues) {
        if (revenue == null || xValues == null || revenue.length == 0 || xValues.size() == 0) {
            return; // Không cập nhật biểu đồ nếu không có dữ liệu
        }

        ArrayList<BarEntry> entries = new ArrayList<>();
        for (int i = 0; i < revenue.length; i++) {
            entries.add(new BarEntry(i, revenue[i]));
        }

        BarDataSet dataSet = new BarDataSet(entries, "Doanh thu");
        dataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        BarData barData = new BarData(dataSet);
        barChart.setData(barData);
        barChart.invalidate();
    }
    public interface DataFetchListener {
        void onDataFetched(float[] monthlyRevenue, float[] productRevenue, List<String> productNames, float[] brandRevenue, List<String> brandNames);
    }

}
