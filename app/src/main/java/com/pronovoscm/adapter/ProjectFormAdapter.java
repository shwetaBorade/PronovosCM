package com.pronovoscm.adapter;

import android.app.Activity;
import android.content.Intent;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.pronovoscm.R;
import com.pronovoscm.activity.ProjectFormUserActivity;
import com.pronovoscm.persistence.domain.Forms;
import com.pronovoscm.utils.Constants;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ProjectFormAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private int projectId;
    private List<Forms> projectFormList;
    private Activity mActivity;
    // variable to track event time
    private long mLastClickTime = 0;

    public ProjectFormAdapter(Activity mActivity, List<Forms> ProjectFormList, int projectId) {
        this.projectFormList = ProjectFormList;
        this.mActivity = mActivity;
        this.projectId = projectId;


    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        View view = inflater.inflate(R.layout.project_form_list_item, parent, false);
        return new ProjectFormHolder(view);

    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((ProjectFormHolder) holder).bind();
    }

    @Override
    public int getItemCount() {
        if (projectFormList != null) {
            return projectFormList.size();
        } else {
            return 0;
        }
    }

    @Override
    public int getItemViewType(int position) {

        return position;

    }

    public class ProjectFormHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.folderNameTextView)
        TextView folderNameTextView;
        @BindView(R.id.folderCardView)
        CardView folderCardView;
        @BindView(R.id.invisibleView)
        View invisibleView;
        @BindView(R.id.invisibleFirstView)
        View invisibleFirstView;

        public ProjectFormHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        private void bind() {

            folderNameTextView.setText(projectFormList.get(getAdapterPosition()).getFormName());
            folderCardView.setOnClickListener(v -> {
                // Preventing multiple clicks, using threshold of 1 second
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                Log.e("ProjectFormAdapter", "bind:  go to ProjectFormUserActivity FormsId " + projectFormList.get(getAdapterPosition()).getFormsId() + " RevisionNumber   " + projectFormList.get(getAdapterPosition()).getRevisionNumber() + "  OriginalFormsId() " + projectFormList.get(getAdapterPosition()).getOriginalFormsId());
                if (getAdapterPosition()!=-1 && projectFormList.size()!=-1 && projectFormList.size() >getAdapterPosition()) {
                    mActivity.startActivity(new Intent(mActivity, ProjectFormUserActivity.class).putExtra("project_id", projectId)
                                    .putExtra(Constants.INTENT_KEY_FORM_ID, projectFormList.get(getAdapterPosition()).getFormsId())
                                    .putExtra(Constants.INTENT_KEY_ORIGINAL_FORM_ID, projectFormList.get(getAdapterPosition()).getOriginalFormsId())
                                    .putExtra(Constants.INTENT_KEY_FORM_ACTIVE_REVISION_NUMBER, projectFormList.get(getAdapterPosition()).getRevisionNumber())
                            .putExtra("form_type", "Sync")
                             /*.putExtra(Constants.INTENT_KEY_FORM_SECTIONS,projectFormList.get(getAdapterPosition()).getFormSections())*/);

                }
            });
            if (getAdapterPosition() == 0 || projectFormList.get(getAdapterPosition()).getFormCategoriesId() != projectFormList.get(getAdapterPosition() - 1).getFormCategoriesId()) {
                invisibleView.setVisibility(View.VISIBLE);
            } else {
                invisibleView.setVisibility(View.GONE);
            }
            if (getAdapterPosition() == 0) {
                invisibleFirstView.setVisibility(View.VISIBLE);
            } else {
                invisibleFirstView.setVisibility(View.GONE);
            }

        }
    }
}
