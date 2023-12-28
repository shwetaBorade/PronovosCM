package com.pronovoscm.adapter;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.pronovoscm.R;
import com.pronovoscm.model.response.cclist.Cclist;
import com.pronovoscm.utils.dialogs.CCDialog;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CCAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<Cclist> mCclists;
    private CCDialog mCCDialog;
    private List<Cclist> mSelectedCclists;

    public CCAdapter(CCDialog CCDialog, List<Cclist> cclists, List<Cclist> selectedCCList) {
        mCclists = cclists;
        mSelectedCclists = selectedCCList;
        mCCDialog = CCDialog;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.tags_item_list, parent, false);

        return new ImageTagViewHolder(view);

    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        ((ImageTagViewHolder) holder).bind(mCclists.get(position));
    }

    @Override
    public int getItemCount() {
        if (mCclists != null) {
            return mCclists.size();
        } else {
            return 0;
        }
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public interface updateTags {
        void onUpdateSelectedTags(List<Cclist> selectedTag);
    }

    public class ImageTagViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tagTextView)
        TextView tagTextView;
        @BindView(R.id.tagCheckBox)
        CheckBox tagCheckBox;
        @BindView(R.id.tagsView)
        ConstraintLayout tagsView;

        public ImageTagViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        private void bind(final Cclist cclist) {
            if (cclist != null) {

                tagTextView.setText(cclist.getName());
                Log.i("Selected", "bind: " + mSelectedCclists.size());
                if (mSelectedCclists.contains(cclist)) {
                    tagCheckBox.setChecked(true);
                } else {
                    tagCheckBox.setChecked(false);
                }
                tagsView.setOnClickListener(v -> {
                    tagCheckBox.setChecked(!tagCheckBox.isChecked());
                    if (tagCheckBox.isChecked()) {
                        mSelectedCclists.add(cclist);
                    } else {
                        mSelectedCclists.remove(cclist);
                    }
                    (mCCDialog).onUpdateSelectedTags(mSelectedCclists);
                });
                tagCheckBox.setOnClickListener(v -> {
                    if (tagCheckBox.isChecked()) {
                        mSelectedCclists.add(cclist);
                    } else {
                        mSelectedCclists.remove(cclist);
                    }
                    (mCCDialog).onUpdateSelectedTags(mSelectedCclists);
                });
            }
        }

    }
}