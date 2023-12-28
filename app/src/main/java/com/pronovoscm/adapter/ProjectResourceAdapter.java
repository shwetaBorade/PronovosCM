package com.pronovoscm.adapter;

import android.app.Activity;
import android.graphics.Rect;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.pronovoscm.R;
import com.pronovoscm.model.response.resources.Resources;
import com.pronovoscm.model.response.resources.Users;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ProjectResourceAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<Resources> mResourceList;
    private Activity mActivity;

    public ProjectResourceAdapter(Activity mActivity, List<Resources> photosList) {
        this.mResourceList = photosList;
        this.mActivity = mActivity;
        setHasStableIds(true);

    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//        if (viewType == -1) {

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.resource_header, parent, false);

        return new ResourceHeaderViewHolder(view);
       /* } else {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View view = inflater.inflate(R.layout.resource_item_list, parent, false);

            return new ResourceViewHolder(view);

        }*/
    }

    @Override
    public long getItemId(int position) {
        return mResourceList.get(position).getProjectRolesId();
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
      /*  if (mResourceList.get(position) instanceof Users) {
            ((ResourceViewHolder) holder).bind((Users) mResourceList.get(position));
        } else {*/
        ((ResourceHeaderViewHolder) holder).bind();

//        }
    }

    @Override
    public int getItemCount() {
        if (mResourceList != null) {
            return mResourceList.size();
        } else {
            return 0;
        }
    }

    @Override
    public int getItemViewType(int position) {
//        if (mResourceList.get(position) instanceof Users) {
        return position;
//        } else {
//            return -1;
//        }
    }


    public class ResourceHeaderViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.resourceNameTV)
        TextView resourceNameTV;
        @BindView(R.id.userRecyclerView)
        RecyclerView userRecyclerView;
        @BindView(R.id.invisibleView)
        View invisibleView;
        List<Users> usersArrayList = new ArrayList<>();
        private LinearLayoutManager linearLayoutManager;
        private ProjectResourceUserAdapter projectResourceUserAdapter;

        public ResourceHeaderViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            projectResourceUserAdapter = new ProjectResourceUserAdapter(mActivity, usersArrayList);
            linearLayoutManager = new LinearLayoutManager(mActivity);
            DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(mActivity, linearLayoutManager.getOrientation()) {
                @Override
                public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                    int position = parent.getChildAdapterPosition(view);
                    // hide the divider for the last child
                    if (position == parent.getAdapter().getItemCount() - 1) {
                        outRect.setEmpty();
                    } else {
                        super.getItemOffsets(outRect, view, parent, state);
                    }
                }
            };
            dividerItemDecoration.setDrawable(ContextCompat.getDrawable(userRecyclerView.getContext(), R.drawable.divider));
            userRecyclerView.addItemDecoration(dividerItemDecoration);
            userRecyclerView.setAdapter(projectResourceUserAdapter);
        }


        private void bind() {
            if (getAdapterPosition()==0){
                invisibleView.setVisibility(View.VISIBLE);
            }else
            {
                invisibleView.setVisibility(View.GONE);
            }
            Resources resources = mResourceList.get(getAdapterPosition());
            resourceNameTV.setText(TextUtils.isEmpty(resources.getProjectRoleName()) ? "-" : resources.getProjectRoleName());
            userRecyclerView.setLayoutManager(linearLayoutManager);
            usersArrayList.clear();
            usersArrayList.addAll(resources.getUsers());
            projectResourceUserAdapter.notifyDataSetChanged();
        }

    }
}