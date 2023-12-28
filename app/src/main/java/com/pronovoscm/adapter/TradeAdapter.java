package com.pronovoscm.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.pronovoscm.R;
import com.pronovoscm.model.response.transferlocation.TransferLocationResponse;
import com.pronovoscm.persistence.domain.EquipmentInventory;
import com.pronovoscm.persistence.domain.PjRfiContactList;
import com.pronovoscm.persistence.domain.PunchlistAssignee;
import com.pronovoscm.persistence.domain.Trades;

import java.util.List;

public class TradeAdapter extends ArrayAdapter<String> {

    private final LayoutInflater mInflater;
    private final Context mContext;
    private final List<Object> items;
    private final int mResource;

    public TradeAdapter(@NonNull Context context, @LayoutRes int resource,
                        @NonNull List objects) {
        super(context, resource, 0, objects);

        mContext = context;
        mInflater = LayoutInflater.from(context);
        mResource = resource;
        items = objects;
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView,
                                @NonNull ViewGroup parent) {
        return createItemView(position, convertView, parent);
    }


    @Override
    public @NonNull
    View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return createItemView(position, convertView, parent);
    }

    private View createItemView(int position, View convertView, ViewGroup parent) {
        final View view = mInflater.inflate(mResource, parent, false);
        TextView spinnerTextView = view.findViewById(R.id.spinnerTextView);
        if (items.get(position) != null && items.get(position) instanceof Trades) {
            Trades trades = (Trades) items.get(position);
            spinnerTextView.setText(trades.getName());
            spinnerTextView.setVisibility(View.VISIBLE);

        } else if (items.get(position) != null && items.get(position) instanceof PunchlistAssignee) {
            PunchlistAssignee trades = (PunchlistAssignee) items.get(position);
            spinnerTextView.setText(trades.getName());
            spinnerTextView.setVisibility(View.VISIBLE);

        } else if (items.get(position) instanceof TransferLocationResponse.Locations) {
            TextView offTypeTv = view.findViewById(R.id.spinnerTextView);
            TransferLocationResponse.Locations locations = (TransferLocationResponse.Locations) items.get(position);
            offTypeTv.setText(locations.getProjectName());

        } else if (items.get(position) != null && items.get(position) instanceof String) {
            TextView offTypeTv = view.findViewById(R.id.spinnerTextView);
            offTypeTv.setText((String) items.get(position));

        } else if (items.get(position) != null && items.get(position) instanceof EquipmentInventory) {
            TextView offTypeTv = view.findViewById(R.id.spinnerTextView);
            offTypeTv.setText(((EquipmentInventory) items.get(position)).getCompanyIdNumber());

        } else if (items.get(position) != null && items.get(position) instanceof PjRfiContactList) {
            TextView offTypeTv = view.findViewById(R.id.spinnerTextView);
            offTypeTv.setText(((PjRfiContactList) items.get(position)).getName());

        } else {
            spinnerTextView.setVisibility(View.GONE);

        }
        return view;
    }
}
