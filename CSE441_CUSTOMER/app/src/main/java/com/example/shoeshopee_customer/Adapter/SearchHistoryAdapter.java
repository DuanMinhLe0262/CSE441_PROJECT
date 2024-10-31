package com.example.shoeshopee_customer.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.shoeshopee_customer.Model.SearchHistory;
import com.example.shoeshopee_customer.R;

import java.util.List;

public class SearchHistoryAdapter extends ArrayAdapter<SearchHistory> {

    private final LayoutInflater inflater;
    private final int resource;

    public SearchHistoryAdapter(Context context, int resource, List<SearchHistory> objects) {
        super(context, resource, objects);
        this.inflater = LayoutInflater.from(context);
        this.resource = resource;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Inflate the view if it's not already inflated
        if (convertView == null) {
            convertView = inflater.inflate(resource, parent, false);
        }

        // Get the current search history item
        SearchHistory historyItem = getItem(position);
        if (historyItem != null) {
            // Set the query text in the TextView
            TextView tvQuery = convertView.findViewById(R.id.tv_query);
            tvQuery.setText(historyItem.getQuery());
        }

        return convertView;
    }
}
