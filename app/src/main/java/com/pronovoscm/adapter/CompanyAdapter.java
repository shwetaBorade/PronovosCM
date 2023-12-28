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
import com.pronovoscm.model.PunchListStatus;
import com.pronovoscm.model.RFIStatusEnum;
import com.pronovoscm.model.SubmittalStatusEnum;
import com.pronovoscm.persistence.domain.CompanyList;
import com.pronovoscm.persistence.domain.DrawingList;

import java.util.List;

public class CompanyAdapter extends ArrayAdapter<String> {

    private final LayoutInflater mInflater;
    private final Context mContext;
    private final List<Object> items;
    private final int mResource;

    public CompanyAdapter(@NonNull Context context, @LayoutRes int resource,
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
        if (items.get(position) instanceof CompanyList) {
            TextView offTypeTv = view.findViewById(R.id.spinnerTextView);
            CompanyList companyList = (CompanyList) items.get(position);
            offTypeTv.setText(companyList.getName());
        } else if (items.get(position) instanceof PunchListStatus) {
            TextView offTypeTv = view.findViewById(R.id.spinnerTextView);
            PunchListStatus punchListStatus = (PunchListStatus) items.get(position);
            if (PunchListStatus.Complete.getValue() == punchListStatus.getValue()) {
                offTypeTv.setText(R.string.punchlist_status_completed);
            } else {
                offTypeTv.setText(punchListStatus.name());
            }


        } else if (items.get(position) instanceof DrawingList) {
            TextView offTypeTv = view.findViewById(R.id.spinnerTextView);
            DrawingList drawingList = (DrawingList) items.get(position);
            offTypeTv.setText(drawingList.getDrawingName());
        } else if (items.get(position) instanceof String) {
            TextView offTypeTv = view.findViewById(R.id.spinnerTextView);
            offTypeTv.setText((String) items.get(position));
        } else if (items.get(position) instanceof RFIStatusEnum) {
            TextView offTypeTv = view.findViewById(R.id.spinnerTextView);
            offTypeTv.setText((((RFIStatusEnum) items.get(position)).getStatusString()));
        } else if (items.get(position) instanceof SubmittalStatusEnum) {
            TextView offTypeTv = view.findViewById(R.id.spinnerTextView);
            offTypeTv.setText((((SubmittalStatusEnum) items.get(position)).getStatusString()));
        }
        return view;
    }
}
