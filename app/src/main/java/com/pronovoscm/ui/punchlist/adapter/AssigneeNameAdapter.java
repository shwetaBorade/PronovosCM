package com.pronovoscm.ui.punchlist.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;


import com.pronovoscm.R;

import java.util.List;

public class AssigneeNameAdapter extends RecyclerView.Adapter<AssigneeNameAdapter.MyHolder> {

    Context c;
    List<String> tvshows;

    public AssigneeNameAdapter(Context c, List<String> tvshows) {
        this.c = c;
        this.tvshows = tvshows;
    }

    //INITIALIE VH
    @Override
    public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_view_assignee_name, parent, false);
        MyHolder holder = new MyHolder(v);
        return holder;
    }

    //BIND DATA
    @Override
    public void onBindViewHolder(MyHolder holder, int position) {
        Log.d("Nitin", "onBindViewHolder: "+tvshows.get(position));
        holder.nameTxt.setText(tvshows.get(position));
    }

    @Override
    public int getItemCount() {
        return tvshows.size();
    }

    public class MyHolder extends RecyclerView.ViewHolder {

        TextView nameTxt;

        public MyHolder(View itemView) {
            super(itemView);
            nameTxt = (TextView) itemView.findViewById(R.id.assigneeNameId);
        }
    }
}
