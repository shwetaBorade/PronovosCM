package com.pronovoscm.adapter;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;

import com.pronovoscm.R;
import com.pronovoscm.model.response.emailassignee.AssigneeList;
import com.pronovoscm.utils.dialogs.ToDialog;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SelectToAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<AssigneeList> mAssigneeLists;
    private ToDialog mAlbumsDialog;
    private AssigneeList mAssigneeList;

    public SelectToAdapter(ToDialog albumsDialog, List<AssigneeList> assigneeLists, AssigneeList assigneeList) {
        this.mAssigneeLists = assigneeLists;
        this.mAssigneeList = assigneeList;
        this.mAlbumsDialog = albumsDialog;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.select_album_item_list, parent, false);

        return new AlbumListViewHolder(view);

    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        ((AlbumListViewHolder) holder).bind(mAssigneeLists.get(position));
    }

    @Override
    public int getItemCount() {
        if (mAssigneeLists != null) {
            return mAssigneeLists.size();
        } else {
            return 0;
        }
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public interface updateAssignee {
        void onUpdateSelectedAssignee(AssigneeList photoFolder);
    }

    public class AlbumListViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.albumTextView)
        TextView albumTextView;
        @BindView(R.id.albumRadioButton)
        RadioButton albumRadioButton;
        @BindView(R.id.tagsView)
        ConstraintLayout tagsView;

        public AlbumListViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        private void bind(final AssigneeList assigneeList) {
            if (assigneeList != null) {

                albumTextView.setText(assigneeList.getName());
                if (mAssigneeList != null && assigneeList.equals(mAssigneeList)) {
                    albumRadioButton.setChecked(true);
                } else {
                    albumRadioButton.setChecked(false);
                }
                albumRadioButton.setClickable(false);
                tagsView.setOnClickListener(v -> {
                            albumRadioButton.setChecked(!albumRadioButton.isChecked());
                            mAssigneeList = assigneeList;
                            notifyDataSetChanged();
                            (mAlbumsDialog).onUpdateSelectedAssignee(mAssigneeList);
                        }
                );

            }
        }

    }
}