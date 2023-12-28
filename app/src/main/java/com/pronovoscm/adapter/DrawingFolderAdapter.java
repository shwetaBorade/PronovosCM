package com.pronovoscm.adapter;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.pronovoscm.R;
import com.pronovoscm.activity.DrawingListTabActivity;
import com.pronovoscm.persistence.domain.DrawingFolders;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DrawingFolderAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private int projectId;
    private List<DrawingFolders> drawingFolderList;
    private Activity mActivity;
    private String projectName;

    public DrawingFolderAdapter(Activity mActivity, List<DrawingFolders> drawingFolderList, int projectId, String projectName) {
        this.drawingFolderList = drawingFolderList;
        this.mActivity = mActivity;
        this.projectId = projectId;
        this.projectName = projectName;


    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        View view = inflater.inflate(R.layout.drawing_folder_list_item, parent, false);
        return new DrawingFolderHolder(view);

    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((DrawingFolderHolder) holder).bind();
    }

    @Override
    public int getItemCount() {
        if (drawingFolderList != null) {
            return drawingFolderList.size();
        } else {
            return 0;
        }
    }

    @Override
    public int getItemViewType(int position) {

        return position;

    }

    public class DrawingFolderHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.folderNameTextView)
        TextView folderNameTextView;
        @BindView(R.id.folderCardView)
        CardView folderCardView;

        public DrawingFolderHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        private void bind() {
            folderNameTextView.setText(drawingFolderList.get(getAdapterPosition()).getFolderName());
            folderCardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    mActivity.startActivity(new Intent(mActivity, DrawingListActivity.class).putExtra("drw_folder_id",drawingFolderList.get(getAdapterPosition()).getDrwFoldersId()).putExtra("project_id",projectId));
                    mActivity.startActivity(new Intent(mActivity, DrawingListTabActivity.class)
                            .putExtra("drw_folder_id", drawingFolderList.get(getAbsoluteAdapterPosition())
                                    .getDrwFoldersId()).putExtra("project_id", projectId));
                }
            });
        }
    }
}
