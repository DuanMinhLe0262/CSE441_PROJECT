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

    private String[] xMonths = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
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

        setupBarChart(monthlyBarChart, xMonths);
        setupBarChart(productBarChart, productNames.toArray(new String[0]));
        setupBarChart(brandBarChart, brandNames.toArray(new String[0]));

        // Setup Spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.statistics_options, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        statisticsSpinner.setAdapter(adapter);
        fetchOrderData();

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
                        Log.d("productttt", Arrays.toString(productNames.toArray()));
                        Log.d("productttt1", Arrays.toString(productNames.toArray(new String[0])));
                        Log.d("reven", productRevenue.toString());
                        updateChart(productBarChart, productRevenue, productNames.toArray(new String[0]));
                        break;
                    case 2: // Doanh thu theo thương hiệu
                        monthlyBarChart.setVisibility(View.GONE);
                        productBarChart.setVisibility(View.GONE);
                        brandBarChart.setVisibility(View.VISIBLE);
                        updateChart(brandBarChart, brandRevenue, brandNames.toArray(new String[0]));
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

    private void setupBarChart(BarChart barChart, String[] xValues) {
        YAxis yAxis = barChart.getAxisLeft();
        yAxis.setAxisMaximum(3000000f); // Thay đổi giá trị tối đa tùy theo doanh thu cao nhất
        yAxis.setAxisLineWidth(2f);
        yAxis.setAxisLineColor(Color.BLACK);
        yAxis.setLabelCount(10, true);

        barChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(xValues));
        barChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        barChart.getXAxis().setGranularity(1f);
        barChart.getXAxis().setGranularityEnabled(true);
        barChart.getDescription().setEnabled(false);
        Log.d("brandđ", brandNames.toString());
    }

    private void fetchOrderData() {
        DatabaseReference ordersRef = FirebaseDatabase.getInstance().getReference("orders");

        ordersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Arrays.fill(monthlyRevenue, 0);

                // Khởi tạo danh sách cho sản phẩm và thương hiệu
                if (productNames == null) {
                    productNames = new ArrayList<>();
                }
                if (brandNames == null) {
                    brandNames = new ArrayList<>();
                }

                // Reset doanh thu
                if (productRevenue == null) {
                    productRevenue = new float[productNames.size()];
                } else {
                    Arrays.fill(productRevenue, 0);
                }

                if (brandRevenue == null) {
                    brandRevenue = new float[brandNames.size()];
                } else {
                    Arrays.fill(brandRevenue, 0);
                }

                // Kiểm tra nếu không có dữ liệu
                if (!dataSnapshot.exists()) {
                    // Nếu không có dữ liệu, không làm gì cả
                    return;
                }

                for (DataSnapshot orderSnapshot : dataSnapshot.getChildren()) {
                    Order order = orderSnapshot.getValue(Order.class);
                    if (order != null && order.getTime() != null && "Đã giao hàng".equals(order.getStatus())) {
                        // Doanh thu theo tháng
                        String[] timeParts = order.getTime().split(" ");
                        if (timeParts.length >= 2) {
                            String[] dateParts = timeParts[1].split(":");
                            if (dateParts.length >= 3) {
                                int month = Integer.parseInt(dateParts[1]) - 1;
                                monthlyRevenue[month] += order.getTotal();
                            }
                        }

                        // Doanh thu theo sản phẩm và thương hiệu
                        for (CartProduct product : order.getItems().values()) {
                            String productName = product.getName();
                            String brandName = product.getBrandName();

                            // Tính doanh thu cho sản phẩm
                            int productIndex = productNames.indexOf(productName);
                            if (productIndex == -1) {
                                productNames.add(productName);
                                productIndex = productNames.size() - 1; // Thêm mới sản phẩm vào danh sách
                            }

                            // Cập nhật doanh thu cho sản phẩm
                            if (productRevenue.length <= productIndex) {
                                productRevenue = Arrays.copyOf(productRevenue, productIndex + 1); // Mở rộng mảng nếu cần
                            }
                            productRevenue[productIndex] += product.getPrice() * product.getQuantity();

                            // Tính doanh thu cho thương hiệu
                            int brandIndex = brandNames.indexOf(brandName);
                            if (brandIndex == -1) {
                                brandNames.add(brandName);
                                brandIndex = brandNames.size() - 1; // Thêm mới thương hiệu vào danh sách
                            }

                            // Cập nhật doanh thu cho thương hiệu
                            if (brandRevenue.length <= brandIndex) {
                                brandRevenue = Arrays.copyOf(brandRevenue, brandIndex + 1); // Mở rộng mảng nếu cần
                            }
                            brandRevenue[brandIndex] += product.getPrice() * product.getQuantity();
                        }
                    }
                }

                // Cập nhật biểu đồ
                updateChart(monthlyBarChart, monthlyRevenue, xMonths);
                updateChart(productBarChart, productRevenue, productNames.toArray(new String[0]));
                updateChart(brandBarChart, brandRevenue, brandNames.toArray(new String[0]));
                Log.d("productttt0", productNames.toString());
                Log.d("brandđ0", brandNames.toString());
            }


            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Xử lý lỗi
            }
        });
    }

    private void updateChart(BarChart barChart, float[] revenue, String[] xValues) {
        if (revenue == null || xValues == null || revenue.length == 0 || xValues.length == 0) {
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

}
