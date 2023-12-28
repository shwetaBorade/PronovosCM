package com.pronovoscm.adapter;

import android.app.Activity;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Spannable;
import android.text.Spanned;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.pronovoscm.R;
import com.pronovoscm.model.response.resources.Users;
import com.pronovoscm.utils.NoUnderlineSpan;
import com.pronovoscm.utils.TextViewNoUnderline;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ProjectResourceUserAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    NoUnderlineSpan mNoUnderlineSpan;
    private List<Users> mResourceList;
    private Activity mActivity;

    public ProjectResourceUserAdapter(Activity mActivity, List<Users> photosList) {
        this.mResourceList = photosList;
        this.mActivity = mActivity;
        mNoUnderlineSpan = new NoUnderlineSpan();
        setHasStableIds(true);
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.resource_item_list, parent, false);

        return new ResourceViewHolder(view);

    }

    @Override
    public long getItemId(int position) {
        return mResourceList.get(position).getUsersId();
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (mResourceList.get(position) instanceof Users) {
            ((ResourceViewHolder) holder).bind((Users) mResourceList.get(position));
        }
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
        if (mResourceList.get(position) instanceof Users) {
            return position;
        } else {
            return -1;
        }
    }


    public class ResourceViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.userNameTextView)
        TextView userNameTextView;
        @BindView(R.id.userphoneTextView)
        TextView userphoneTextView;
        @BindView(R.id.useremailTextView)
        TextViewNoUnderline useremailTextView;

        public ResourceViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }


        private void bind(Users users) {
            userNameTextView.setText(users.getName());
            userphoneTextView.setText(TextUtils.isEmpty(users.getPhone()) ? "-" : users.getPhone());
            useremailTextView.setText(TextUtils.isEmpty(users.getEmail())?"-":users.getEmail());
            if (userphoneTextView.getText() instanceof Spannable) {
                Spannable s = (Spannable) userphoneTextView.getText();
                s.setSpan(mNoUnderlineSpan, 0, s.length(), Spanned.SPAN_MARK_MARK);
            }
            if (useremailTextView.getText() instanceof Spannable) {
                Spannable s = (Spannable) useremailTextView.getText();
                s.setSpan(mNoUnderlineSpan, 0, s.length(), Spanned.SPAN_MARK_MARK);
            }

        }

    }
}