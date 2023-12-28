package com.pronovoscm.adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

import com.pronovoscm.R;
import com.pronovoscm.persistence.domain.EquipmentRegion;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class AutocompleteSelectEquipmentAdapter extends ArrayAdapter {

    private final int itemLayout;
    private final ListFilter listFilter = new ListFilter();
    private List<EquipmentRegion> dataList;
    private List<EquipmentRegion> dataListAllItems;


    public AutocompleteSelectEquipmentAdapter(Context context, int resource, List<EquipmentRegion> storeDataLst) {
            super(context, resource, storeDataLst);
        dataList = storeDataLst;
        itemLayout = resource;
    }

    @Override
    public int getCount() {
        return dataList.size();
    }

    @Override
    public EquipmentRegion getItem(int position) {
        return dataList.get(position);
    }

    @Override
    public View getView(int position, View view, @NonNull ViewGroup parent) {

        if (view == null) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(itemLayout, parent, false);
        }

        TextView label = view.findViewById(R.id.label);
        label.setText(Objects.requireNonNull(getItem(position)).getName());
        return view;
    }

    @NonNull
    @Override
    public Filter getFilter() {
        return listFilter;
    }

    class ListFilter extends Filter {
        private final Object lock = new Object();

        @Override
        protected FilterResults performFiltering(CharSequence prefix) {
            FilterResults results = new FilterResults();
            if (dataListAllItems == null) {
                synchronized (lock) {
                    dataListAllItems = new ArrayList<>(dataList);
                }
            }

            if (prefix == null || prefix.length() == 0) {
                synchronized (lock) {
                    results.values = dataListAllItems;
                    results.count = dataListAllItems.size();
                }
            } else {
                final String searchStrLowerCase = prefix.toString().toLowerCase();

                ArrayList<EquipmentRegion> matchValues = new ArrayList<>();

                for (EquipmentRegion dataItem : dataListAllItems) {
                    if (dataItem.getName() != null && dataItem.getName().toLowerCase().contains(searchStrLowerCase)) {
                        matchValues.add(dataItem);
                    } else if (dataItem.getName() != null && dataItem.getName().toLowerCase().contains(searchStrLowerCase)) {
                        matchValues.add(dataItem);
                    }
                }

                results.values = matchValues;
                results.count = matchValues.size();
            }

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            if (results.values != null) {
                dataList = (ArrayList<EquipmentRegion>) results.values;
            } else {
                dataList = null;
            }
            if (results.count > 0) {
                notifyDataSetChanged();
            } else {
                notifyDataSetInvalidated();
            }
        }

    }
}
