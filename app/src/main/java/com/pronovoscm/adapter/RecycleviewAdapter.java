package com.pronovoscm.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;

import com.pronovoscm.R;
import com.pronovoscm.persistence.domain.CompanyList;
import com.pronovoscm.persistence.domain.PunchlistAssignee;
import com.pronovoscm.persistence.domain.Trades;

import java.util.List;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

public class RecycleviewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final UpdateObject updateObject;
    private List<Object> mPhotoFolders;
    private Object mPhotoFolder;

    public RecycleviewAdapter(List<Object> photoFolders, Object photoFolder, UpdateObject updateObject) {
        this.mPhotoFolders = photoFolders;
        this.mPhotoFolder = photoFolder;
        this.updateObject = updateObject;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.select_album_item_list, parent, false);

        return new RecycleViewHolder(view);

    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        ((RecycleViewHolder) holder).bind(mPhotoFolders.get(position));
    }

    @Override
    public int getItemCount() {
        if (mPhotoFolders != null) {
            return mPhotoFolders.size();
        } else {
            return 0;
        }
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public interface UpdateObject {
        void onUpdateSelectedObject(Object object);
    }

    public class RecycleViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.albumTextView)
        TextView albumTextView;
        @BindView(R.id.albumRadioButton)
        RadioButton albumRadioButton;
        @BindView(R.id.tagsView)
        ConstraintLayout tagsView;

        public RecycleViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        private void bind(final Object object) {
            if (object != null) {

                if (object instanceof Trades) {
                    Trades trades = (Trades) object;
                    albumTextView.setText(trades.getName());
                    if (mPhotoFolder != null && trades.getTradesId().equals(((Trades)mPhotoFolder).getTradesId())) {
                        albumRadioButton.setChecked(true);
                    } else {
                        albumRadioButton.setChecked(false);
                    }
                } else if (object instanceof CompanyList) {
                    CompanyList companyList = (CompanyList) object;
                    albumTextView.setText(companyList.getName());
                    if (mPhotoFolder != null && companyList.getCompanyId().equals(((CompanyList)mPhotoFolder).getCompanyId())) {
                        albumRadioButton.setChecked(true);
                    } else {
                        albumRadioButton.setChecked(false);
                    }
                } else if (object instanceof PunchlistAssignee) {
                    PunchlistAssignee punchlistAssignee = (PunchlistAssignee) object;
                    albumTextView.setText(punchlistAssignee.getName());
                    if (mPhotoFolder != null && punchlistAssignee.getUsersId().equals(((PunchlistAssignee)mPhotoFolder).getUsersId())) {
                        albumRadioButton.setChecked(true);
                    } else {
                        albumRadioButton.setChecked(false);
                    }
                }

                albumRadioButton.setClickable(false);
                tagsView.setOnClickListener(v -> {
                            albumRadioButton.setChecked(!albumRadioButton.isChecked());
                            mPhotoFolder = object;
                            notifyDataSetChanged();
                            updateObject.onUpdateSelectedObject(mPhotoFolder);
                        }
                );

            }
        }

    }
}